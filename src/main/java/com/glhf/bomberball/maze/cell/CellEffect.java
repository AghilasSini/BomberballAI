package com.glhf.bomberball.maze.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.glhf.bomberball.Bomberball;

public abstract class CellEffect {

    private Cell cell;
    protected Color color;

    protected Animation<AtlasRegion> animation;

    public CellEffect(Cell cell) {
        this.cell = cell;
        this.color = Color.WHITE;
    }

    public AtlasRegion getSprite() {
        return animation.getKeyFrame(Bomberball.time_elapsed);
    }

    public void selfRemove() {
        cell.removeEffect();
    }

    public Color getColor() {
        return this.color;
    }
}
