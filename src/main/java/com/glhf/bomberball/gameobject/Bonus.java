package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.audio.Audio;

public class Bonus extends GameObject {
    public enum Type {
        SPEED,
        BOMB_RANGE,
        BOMB_NUMBER
    }

    private Type type;
    public Bonus() {
        super();
    }

    public Bonus(Type type) {
        super(1);
        this.type = type;
    }
    
    @Override
    public GameObject clone() {
    	return new Bonus(type);
    }

    @Override
    public void initialize() {
        switch (type) {
            case SPEED:
                sprite = Graphics.Sprites.get("speed_bonus");
                break;
            case BOMB_RANGE:
                sprite = Graphics.Sprites.get("explo_bonus");
                break;
            case BOMB_NUMBER:
                sprite = Graphics.Sprites.get("bomb_bonus");
                break;
        }
    }

    public Type getType(){
        return this.type;
    }

    @Override
    public boolean isWalkable() { return true; }

    @Override
    public int scoreWhileDestroyed() {
        return 10;
    }

    public void applyEffect(Player player) {
//        Audio.POWER_UP.play();
//        Score.getINSTANCE().increaseScore(10);
        switch (type) {
            case SPEED:
                player.bonus_moves++;
                break;
            case BOMB_RANGE:
                player.bonus_bomb_range++;
                break;
            case BOMB_NUMBER:
                player.bonus_bomb_number++;
                break;
        }
    }
}