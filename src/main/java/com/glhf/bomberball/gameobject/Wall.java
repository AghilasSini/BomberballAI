package com.glhf.bomberball.gameobject;

public abstract class Wall extends GameObject {

    protected Wall() {
        super(1);
    }
    @Override
    public int scoreWhileDestroyed(){
        return 10;
    }
}
