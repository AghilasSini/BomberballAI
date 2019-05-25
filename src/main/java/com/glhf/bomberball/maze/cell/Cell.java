package com.glhf.bomberball.maze.cell;

import com.badlogic.gdx.graphics.Color;
import com.glhf.bomberball.gameobject.Bomb;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.utils.Directions;

import java.util.ArrayList;

/**
 * Maze's main component, represents a cell in a maze.
 *
 * @author nayala
 */
public class Cell {

    private int x;
    private int y;

    /**
     * GameObjects in cell
     */
    private ArrayList<GameObject> objects;

    /**
     * Adjacent cells in maze.
     * [RIGHT, UP, LEFT, DOWN]
     */
    private transient Cell[] adjacent_cells;

    private transient CellEffect cell_effect;


    /**
     * Cell constructor
     * @param x x coordinates in maze
     * @param y coordinates in maze
     */
    public Cell(int x, int y)
    {
        this.x = x;
        this.y = y;
        objects = new ArrayList<>();
        adjacent_cells = new Cell[Directions.values().length];
    }
    
    
    public Cell clone() {
    	Cell clone = new Cell(x,y);
    	for (GameObject o : this.objects) {
    		GameObject cloneO = o.clone();
    		clone.addGameObject(cloneO);
    	}
    	// Warning: adjacent cells cannot be set here
    	return clone;
    }
    
    

    /**
     * Initializes this cell (gives a cell a local view of the maze)
     * @param cell_right right adjacent cell (null if none)
     * @param cell_up    up adjacent cell    (null if none)
     * @param cell_left  left adjacent cell  (null if none)
     * @param cell_down  down adjacent cell  (null if none)
     */
    public void initialize(Cell cell_right, Cell cell_up, Cell cell_left, Cell cell_down)
    {
        this.adjacent_cells = new Cell[] {cell_right, cell_up, cell_left, cell_down};
        for (GameObject o : objects) {
            o.setCell(this);
            o.initialize();
        }
    }

    /*========== Getters ========*/

    public int getY() { return y; }

    public int getX() { return x; }

    public ArrayList<GameObject> getGameObjects() { return objects; }

    public Cell getAdjacentCell(Directions dir) { return adjacent_cells[dir.ordinal()]; }

    public CellEffect getCellEffect() { return this.cell_effect; }

    /*===========================*/

    /**
     * @return true if the cell is empty (else otherwise)
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * @param cell
     * @return Returns the direction of cell if cell is an adjacent cell (null instead)
     */
    public Directions getCellDir(Cell cell)
    {
        for (Directions dir : Directions.values()) {
            if (getAdjacentCell(dir) == cell) {
                return dir;
            }
        }
        return null;
    }

    /**
     * @return List of all adjacent cells
     * Discards null adjacent cells
     */
    public ArrayList<Cell> getAdjacentCellsInMaze()
    {
        ArrayList<Cell> cells = new ArrayList<>();
        for (Directions dir : Directions.values()) {
            Cell c = getAdjacentCell(dir);
            if (c != null) {
                cells.add(c);
            }
        }
        return cells;
    }
    
    /**
     * @return List of all adjacent cells
     * Discards null adjacent cells
     */
    public ArrayList<Cell> getAdjacentCells()
    {
        ArrayList<Cell> cells = new ArrayList<>();
        for (Directions dir : Directions.values()) {
            Cell c = getAdjacentCell(dir);
            cells.add(c);
        }
        return cells;
    }

    /**
     * Damages all GameObjects in cell
     * @param damage Damage amount to apply
     */
    public void getDamage(int damage)
    {
        ArrayList<GameObject> gameObjects = new ArrayList<>(objects);
        gameObjects.forEach((o) -> o.getDamage(damage));
    }

    /**
     * Add a GameObject to the cell
     * @param gameObject GameObject to add
     */
    public void addGameObject(GameObject gameObject)
    {
        gameObject.setCell(this);
        objects.add(gameObject);
    }

    /**
     * Remove a GameObject from the cell
     * @param gameObject GameObject to remove
     */
    public void removeGameObject(GameObject gameObject) {
        gameObject.setCell(null);
        objects.remove(gameObject);
    }

    /**
     * Removes all objects from the cell
     */
    public void removeGameObjects() {
        objects.clear();
    }

    /**
     * @return Returns true if that cell is walkable (false instead)
     */
    public boolean isWalkable()
    {
        for (GameObject o : objects) {
            if (!o.isWalkable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Explodes the cell (recursive)
     * @param dir propagation direction
     * @param damage damage to apply to gameObjects
     * @param range propagation range (1 means no further propagation)
     * The explosion stops when it encounters a non walkable object.
     */
    public void explode(Directions dir, int damage, int range)
    {
        boolean propagate = this.isWalkable();
        getDamage(damage);
        if (propagate) {
            if (range > 1 && dir != null) {
                Cell adjacent_cell = getAdjacentCell(dir);
                if (adjacent_cell != null) {
                    adjacent_cell.explode(dir, damage, range - 1);
                }
            }
        }
        this.setExplosionEffect(dir, propagate ? range : 1);
    }

    /**
     * Processes end turn, explodes all the bombs in the cell.
     */
    public void processEndTurn()
    {
        for (Bomb b : this.getInstancesOf(Bomb.class)) {
            b.explode();
        }
    }

    /**
     * Search in cell for gameObjects instances of specified class.
     * @param clz Class to extract
     * @param <T>
     * @return All gameObjects in cell, instances of c.
     */
    public <T extends GameObject> ArrayList<T> getInstancesOf(Class<T> clz) {
        ArrayList<T> instances = new ArrayList<>();
        for(GameObject o : objects){
            if(clz.isInstance(o)){
                instances.add(clz.cast(o));
            }
        }
        return instances;
    }

    /**
     * @param clz Class to search for
     * @param <T>
     * @return True if the cell has an object of class clz (False otherwise)
     */
    public <T> boolean hasInstanceOf(Class<T> clz) {
        for(GameObject o : objects){
            if(clz.isInstance(o)){
                return true;
            }
        }
        return false;
    }

    /**
     * Removes current cell effect.
     */
    public void removeEffect() {
        cell_effect = null;
    }

    /**
     * Sets the select effect on the cell
     * @param color tint color
     */
    public void setSelectEffect(Color color) {
        cell_effect = new SelectEffect(this, color);
    }

    /**
     * Sets the explosion effect on the cell
     * @param dir explosion's direction
     * @param range explosion's range
     */
    private void setExplosionEffect(Directions dir, int range) {
        cell_effect = new ExplosionEffect(this, dir, range);
    }

    public float distanceTo(Cell cell) {
        int dx = x - cell.x;
        int dy = y - cell.y;
        return (float) Math.sqrt(dx*dx+dy*dy);
    }
}
