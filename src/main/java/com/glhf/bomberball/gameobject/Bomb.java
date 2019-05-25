package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;

public class Bomb extends GameObject {

    private int damage;
    private int range;

    /**
     * constructor
     * @param range number of squares reached by the bomb in the four directions ( north, east, sout, west)
     * @return Bomb
     */
    public Bomb(int damage, int range) {
        // initially, bomb inflict 1 getDamage
        super(1);
        this.damage = damage;
        this.range = range;
        sprite = Graphics.Sprites.get("bomb");
    }
    
    @Override
    public GameObject clone() {
    	return new Bomb(damage, range);
    }

    public void explode() {
//        Audio.BOMB.play();
        cell.explode(null, damage, 0);
        for (Directions dir : Directions.values()) {
            Cell adjacent_cell = cell.getAdjacentCell(dir);
            if (adjacent_cell != null) {
                adjacent_cell.explode(dir, damage, range);
            }
        }
        cell.removeGameObject(this);
    }

    @Override
    public void getDamage(int damage){ }
}
