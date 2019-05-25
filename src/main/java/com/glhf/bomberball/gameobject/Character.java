package com.glhf.bomberball.gameobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.utils.Directions;

public abstract class Character extends GameObject {

    protected int initial_moves = 5;
    protected String skin = "knight_m";

    protected transient int moves_remaining;

    protected transient Animation<TextureAtlas.AtlasRegion> animation;

    public Character(String skin, int life, int initial_moves) {
        super(life);
        this.skin = skin;
        this.initial_moves = initial_moves;
        this.moves_remaining = initial_moves;
        initialize();
    }

    public Character() {

    }

    @Override
    public void initialize() {
        super.initialize();
        setAnimation("idle");
    }

    public int getMovesRemaining() {
        return moves_remaining;
    }

    /**
     * set the animation of the character
     * @param animation_name animation name
     */
    protected void setAnimation(String animation_name)
    {
        animation = new Animation<>(0.15f, Graphics.Anims.get(skin + "/" + animation_name), Animation.PlayMode.LOOP);
    }

    public Animation<TextureAtlas.AtlasRegion> getAnimation() {
        return animation;
    }

    /**
     * Initiate attribute number_move_remaining at the beginning of a turn
     */
    public void initiateTurn() { moves_remaining = initial_moves; }

    @Override
    public TextureAtlas.AtlasRegion getSprite()
    {
        return animation.getKeyFrame(Bomberball.time_elapsed);
    }

    @Override
    public boolean isWalkable(){
        return false;
    }

    public void endTurn() {
    }

    @Override
    public boolean move(Directions dir)
    {
        if (moves_remaining > 0 && super.move(dir)) {
            moves_remaining--;
            return true;
        }
        return false;
    }

}
