package com.glhf.bomberball.maze.cell;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.utils.Directions;

public class ExplosionEffect extends CellEffect {

    private float time_start;

    private static String[] end_range_animation = {"right", "up", "left", "down"};
    private static String[] mid_range_animation = {"horizontal", "vertical", "horizontal", "vertical"};

    public ExplosionEffect(Cell cell, Directions dir, int range) {
        super(cell);
        time_start = Bomberball.time_elapsed;
        animation = new Animation<>(
                0.07f,
                Graphics.Anims.get("cell/explo/" + getAnimation(dir, range)),
                Animation.PlayMode.NORMAL);
    }

    private static String getAnimation(Directions dir, int range) {
        if (dir == null) {
            return "center";
        }
        return (range == 1) ? end_range_animation[dir.ordinal()] : mid_range_animation[dir.ordinal()];
    }

    @Override
    public AtlasRegion getSprite() {
        float t = Bomberball.time_elapsed - time_start;
        if (!animation.isAnimationFinished(t)) {
            return animation.getKeyFrame(t);
        } else{
            selfRemove();
            return null;
        }
    }
}
