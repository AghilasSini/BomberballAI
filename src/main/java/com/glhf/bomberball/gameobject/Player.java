package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.maze.cell.Cell;

import java.util.ArrayList;
import java.util.Observer;

import com.glhf.bomberball.utils.Directions;

public class Player extends Character {

    private ArrayList<Observer> observers;
    private boolean active;

    private int initial_bomb_number;
    private int initial_bomb_range;

    private transient int bombs_remaining;

    public transient int bonus_bomb_number = 0;
    public transient int bonus_bomb_range = 0;
    public transient int bonus_moves = 0;
    protected int playerId;
    public Player(String player_skin,
                  int life,
                  int initial_moves,
                  int initial_bomb_number,
                  int initial_bomb_range)
    {
        super(player_skin, life, initial_moves);
        this.observers = new ArrayList<>();
        this.active = false;
        this.initial_bomb_number = initial_bomb_number;
        this.initial_bomb_range = initial_bomb_range;
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * initiate the turn of the player, initialize the number of bombs and the number of moves
     */
    @Override
    public void initiateTurn() {
        active = true;
        moves_remaining = initial_moves + bonus_moves;
        bombs_remaining = initial_bomb_number + bonus_bomb_number;
        this.notifyObservers();
    }

    @Override
    public void endTurn() {
        active = false;
        this.notifyObservers();
    }

    @Override
    public boolean move(Directions dir)
    {
        if (super.move(dir)) {
            for (GameObject go : cell.getGameObjects()) {
                if (go instanceof Enemy) {
                    ((Enemy) go).touchPlayer(this);
                }
            }
            this.notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * The player create a bomb and put it on the square given
     * @param dir
     * @return if the bomb has been dropped
     */
    public boolean dropBomb(Directions dir) {
        boolean dropped = false;
        if (bombs_remaining > 0) {
            Cell dest_cell = cell.getAdjacentCell(dir);
            if (dest_cell != null && dest_cell.isWalkable()) {
                bombs_remaining--;
                Bomb bomb = new Bomb(1, initial_bomb_range + bonus_bomb_range);
                dest_cell.addGameObject(bomb);
                this.notifyObservers();
                dropped = true;
            }
        }
        return dropped;
    }

    @Override
	public void interactWithCell(Cell cell) {
        for (Bonus bonus : cell.getInstancesOf(Bonus.class)) {
            bonus.dispose();
            bonus.applyEffect(this);
        }
    }

    public int getNumberBombRemaining() {
        return bombs_remaining;
    }

    public int getNumberMoveRemaining() {
        return moves_remaining;
    }

    public int getBombRange() {
        return bonus_bomb_range+initial_bomb_range;
    }

    public boolean isActive() {
        return active;
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    private void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update(null, this);
        }
    }

    @Override
    public GameObject clone() {
        GameObject p = new Player(skin, life, initial_moves, initial_bomb_number, initial_bomb_range);

        return p;
    }

	public int getCurrentPlayerId() {
		// TODO Auto-generated method stub
		return playerId;
	}
	public void setCurrentPlayerId(int playerId) {
		this.playerId=playerId;
		
	}
}

