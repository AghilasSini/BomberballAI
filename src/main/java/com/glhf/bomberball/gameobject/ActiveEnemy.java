package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.maze.MazeTransversal;

import static com.glhf.bomberball.utils.Constants.MAX_DEPTH;

public class ActiveEnemy extends Enemy {
    public ActiveEnemy(String skin, int life, int initial_moves, int strength) {
        super(skin, life, initial_moves, strength);
    }

    public ActiveEnemy(){
        super();
    }

    @Override
    public GameObject clone() {
        return new ActiveEnemy(skin,life,initial_moves, strength);
    }

    @Override
    public void createAI() {
        this.nb_reachable_cells = MazeTransversal.getReacheableCellsInRange(this.cell, 100).size();
        this.way = MazeTransversal.longestWayMovesSequence(MazeTransversal.constructWay(this.getCell(), MAX_DEPTH));
    }

    @Override
    public void updateAI() {
        if(cell!=null){// equivalent to isAlive()
            this.way = MazeTransversal.longestWayMovesSequence(MazeTransversal.constructWay(this.getCell(), MAX_DEPTH));
        }
    }
}
