package com.glhf.bomberball.gameobject;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;

public class IndestructibleWall extends Wall {

    private transient AtlasRegion top_sprite;

    public IndestructibleWall() {
        super();
        this.sprite = Graphics.Sprites.get("wall");
        this.top_sprite = Graphics.Sprites.get("wall_top");
    }

    /**
     * Override of the GameObject's getDamage method : an indestructible can't be broken
     * @param damage
     */
    @Override
    public void getDamage(int damage){ }

    @Override
    public AtlasRegion getSprite() {
        if (this.cell != null) {
            Cell down = this.cell.getAdjacentCell(Directions.DOWN);
            if (down == null || down.hasInstanceOf(IndestructibleWall.class)) {
                return this.top_sprite;
            }
        }
        return this.sprite;
    }

    @Override
    public GameObject clone() {
        IndestructibleWall o = new IndestructibleWall();
        o.sprite = this.sprite;
        return o;
    }
}
