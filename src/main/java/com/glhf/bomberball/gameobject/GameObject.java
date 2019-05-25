/* abstract class GameObject

    class mère de tous les objets qui évoluent dans le labyrinthe

    Un GameObject a une vie, un sprite (fixe ou animation) et voit la cell dans laquelle il se trouve

 */

package com.glhf.bomberball.gameobject;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.glhf.bomberball.utils.Constants;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;

public abstract class GameObject {

    protected int life = 1;
    protected transient AtlasRegion sprite;
    protected transient Cell cell;
    
    //animation de deplacement
    private transient Vector2 offsetVec;
    private transient int frames_left;

    public GameObject(){
        offsetVec = new Vector2();
    }

    protected GameObject(int life) {
        this.life = life;
        offsetVec = new Vector2();
    }

    public AtlasRegion getSprite() { return this.sprite; }

    public void setLife(int life) {
        this.life = life;
    }

    public int getLife() {
        return life;
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }

    @Override
	public GameObject clone(){
        throw new RuntimeException(this.getClass()+" does not have clone method !!");
    }

    /**
     * used for the initialisation of the different subclasses. Needed because of the deserialization.
     */
    public void initialize() { }

    /**@author Jyra
     * @return the score given to the player when a game object is destroyed
     * I use a class in order to be able to override the value returned
     */
    public int scoreWhileDestroyed()
    {
        return 0;
    }

    /**
     * modification of the life of the gameObject
     * @param damage
     */
    public void getDamage(int damage){
        life -= damage;
//        Score.getINSTANCE().increaseScore(scoreWhileDestroyed());
        if (life <= 0) {
            this.dispose();
        }
    }

    /**
     * remove the GameObject from its cell
     */
    public void dispose() {
        cell.removeGameObject(this);
    }

    /**
     * to know if an object has been destroyed or not
     * @return boolean if the gameObject is Alive
     */
    public boolean isAlive() {
        return life > 0;
    }

    /**
     * a GameObject is walkable if a character can walk on it.
     * a non walkable GameObject will stop the bombs explosions
     * @return boolean if a GameObject is walkable
     */
    public boolean isWalkable() { return false; }

    /**
     * move the GameObject to an adjacent cell.
     * @param dir RIGTH, UP, LEFT or DOWN
     * @return boolean if the GameObject can move to the specified direction
     */
    public boolean move(Directions dir)
    {
        return moveToCell(getCell().getAdjacentCell(dir));
    }

    /**
     * move the GameObject to the cell pointed by dest_cell
     * @param dest_cell the cell you want the GameObject to move to.
     * @return boolean if the GameObject can move to dest_cell
     */
    public boolean moveToCell(Cell dest_cell)
    {
        boolean moved;
        if (dest_cell != null && dest_cell.isWalkable()) {
            this.startAnimation(dest_cell);
            cell.removeGameObject(this);
            dest_cell.addGameObject(this);
            this.interactWithCell(dest_cell);
            moved = true;
        }
        else
            moved = false;
        return moved;
    }

    /**
     * function used in a strategy design pattern, see subclasses for the different effects
     * @param cell  the cell the GameObject interact with
     */
    public void interactWithCell(Cell cell) { }


    public void setCell(Cell cell)
    {
        this.cell = cell;
    }

    public Cell getCell()
    {
        if (cell != null) {
            return this.cell;
        } else {
            throw new RuntimeException("GameObject's cell is null");
        }
    }
    
    //============== animation =================
    
    private void startAnimation(Cell dest_cell){
        offsetVec.x = (cell.getX()-dest_cell.getX()) / (float) Constants.NB_ANIMATION_FRAMES;
        offsetVec.y = (cell.getY()-dest_cell.getY()) / (float) Constants.NB_ANIMATION_FRAMES;
        frames_left = Constants.NB_ANIMATION_FRAMES;
    }

    public Vector2 getOffset() {
        if(frames_left>0) frames_left--;
        return new Vector2().mulAdd(offsetVec, frames_left);
    }
}
