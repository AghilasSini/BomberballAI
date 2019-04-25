package com.glhf.bomberball.gameobject;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import java.util.ArrayList;

public abstract class Enemy extends Character {

    protected int strength = 1;

    //protected transient  int actual_move;
    protected ArrayList<Directions> way;
    protected int nb_reachable_cells;

    protected int actual_move; //current index of path followed

    protected Enemy(String skin, int life, int initial_moves, int strength) {
        super(skin, life, initial_moves);
        this.strength = strength;
        this.nb_reachable_cells = 0;
    }

    public Enemy() {
        super();
    }

    /**
     * set damage to a player that the enemy touch
     *
     * @param player the player touched by the ennemy
     */
    public void touchPlayer(Player player) {
        player.getDamage(strength);
    }

    /**
     * give a way to follow to the enemy
     *
     * @param way
     */
    public void setWay(ArrayList<Directions> way) {
        this.way = way;
    }

    @Override
    public void initiateTurn() {
        super.initiateTurn();
        if (nb_reachable_cells < MazeTransversal.getReacheableCellsInRange(this.cell, 100).size()) {
            updateAI();
        }
    }

    /**
     * the enemy has to follow the way he receveid
     */
    public void followWay() {
        if (moves_remaining > 0 && !way.isEmpty()) {
            this.move(way.get(actual_move));
            actual_move = (actual_move + 1) % way.size();
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    followWay();
                }
            }, 0.1f);
        }
    }


    @Override
    public void interactWithCell(Cell cell) {
        ArrayList<GameObject> goList = cell.getGameObjects();
        for (GameObject go : goList) {
            if (go instanceof Player) {
                go.getDamage(strength);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////

    public abstract void createAI();

    public abstract void updateAI();
    
    
    
    
    
    
}