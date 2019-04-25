package com.glhf.bomberball.gameobject;


import com.glhf.bomberball.utils.Directions;

import java.util.ArrayList;

public class PassiveEnemy extends Enemy {

    public PassiveEnemy(){
        super();
    }

    public PassiveEnemy(String skin, int life, int initial_moves, int strength, ArrayList<Directions> way) {
        super(skin, life, initial_moves, strength);
        this.setWay(way);
    }

    @Override
    public void createAI() {

    }

    @Override
    public GameObject clone() {
        return new PassiveEnemy(skin,life,initial_moves, strength, way);
    }

    @Override
    public void updateAI() {


    }
    @Override
    public int scoreWhileDestroyed() {
        return 30;
    }
}
