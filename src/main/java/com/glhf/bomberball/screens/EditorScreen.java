package com.glhf.bomberball.screens;

import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.ui.EditorUI;
import com.glhf.bomberball.utils.VectorInt2;

import java.util.ArrayList;

public class EditorScreen extends MenuScreen {

    private final Maze maze;
    private final EditorUI ui;
    private boolean symmetric = true;
    private GameObject objectSelected;
    private VectorInt2 player_pos = null;
    private int maze_id;

    public EditorScreen(Maze maze)
    {
        super();
        this.maze = maze;
        ui = new EditorUI(this, maze);
        maze_id = Maze.countMazesMulti();
        addUI(ui);
        addSpawns();
    }

    public EditorScreen(int maze_id)
    {
        super();
        this.maze = Maze.importMazeMulti("maze_" + maze_id);
        this.maze_id = maze_id;
        ui = new EditorUI(this, maze);
        addUI(ui);
        addSpawns();
    }

    private void addSpawns() {
        for (VectorInt2 p : maze.getPlayersSpawns()) {
            maze.getCellAt(p.x, p.y).addGameObject(new Player("knight_m", 1, 1,1, 1));
            player_pos = p;
        }
    }

    @Override
    protected void registerActionsHandlers() {
        super.registerActionsHandlers();
        input_handler.registerActionHandler(Action.DROP_SELECTED_OBJECT, (x,y)->dropSelectedObject(ui.screenPosToCell(x,y)));
        input_handler.registerActionHandler(Action.DELETE_OBJECT, (x,y)->deleteObject(ui.screenPosToCell(x,y)));
    }

    private ArrayList<VectorInt2> getPositions(VectorInt2 p) {
        ArrayList<VectorInt2> positions = new ArrayList<VectorInt2>();
//        if (!maze.isCellInBounds(p.x, p.y))
//            return positions;

        if (!symmetric) {
            positions.add(p);
        }
        else {
            float half_w = (maze.getWidth()-1) / 2f;
            float half_h = (maze.getHeight()-1) / 2f;
            float x = p.x - half_w;
            float y = p.y - half_h;
            positions.add(new VectorInt2((int)(x + half_w), (int)(y + half_h)));
            positions.add(new VectorInt2((int)(-x + half_w), (int)(-y + half_h)));
            positions.add(new VectorInt2((int)(-x + half_w), (int)(y + half_h)));
            positions.add(new VectorInt2((int)(x + half_w), (int)(-y + half_h)));
        }
        return positions;
    }

    private void dropSelectedObject(VectorInt2 coords) {
            if(!maze.isCellInBounds(coords.x, coords.y)) return;

        // Checks if already placed players spawns
        if (objectSelected instanceof Player) {
            if (player_pos != null) {
                for (VectorInt2 p : getPositions(player_pos)) {
                    maze.getCellAt(p.x, p.y).removeGameObjects();
                }
            }
            player_pos = coords;
        }

        for (VectorInt2 p : getPositions(coords)) {
            Cell cell = maze.getCellAt(p.x, p.y);
            if (cell != null && objectSelected != null){
                if (cell.isEmpty() || (objectSelected.isWalkable() && cell.isWalkable())) {
                    cell.addGameObject(objectSelected.clone());
                }
            }
        }
    }

    private void deleteObject(VectorInt2 coords) {
        if(!maze.isCellInBounds(coords.x, coords.y)) return;
        for (VectorInt2 p : getPositions(coords)) {
            Cell cell = maze.getCellAt(p.x, p.y);
            if(cell != null) {
                if (cell.hasInstanceOf(Player.class))
                    player_pos = null;
                cell.removeGameObjects();
            }
        }
    }

    public void select(GameObject object){
        objectSelected = object;
    }

    public void saveMaze() {
        if (player_pos == null) {
            return;
        }

        // Removing Players and setting spawn positions
        for (VectorInt2 p : getPositions(player_pos)) {
            maze.getCellAt(p.x, p.y).removeGameObjects();
        }
        maze.setPlayerSpawns(getPositions(player_pos));

        maze.exportCustomMaze("maze_" + maze_id);
        Bomberball.changeScreen(new EditorMenuScreen());
    }
}
