package com.glhf.bomberball.ai;

import java.util.ArrayList;
import java.util.List;

import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;

public class GameState {
	private Maze maze;
	private int current_player_id;
	private int remaining_turns;

	public GameState(Maze maze, int currentPlayerId, int turnNumber) {
		this.maze = maze;
		this.current_player_id = currentPlayerId;
		this.remaining_turns = turnNumber;
	}

	public List<Player> getPlayers() {
		return maze.getPlayers();
	}

	public int getCurrentPlayerId() {
		return current_player_id;
	}

	public void setCurrentPlayerId(int currentPlayerId) {
		this.current_player_id = currentPlayerId;
	}

	public GameState clone() {
		GameState gameStateClone = new GameState((Maze) this.maze.clone(),
												this.current_player_id,
												this.remaining_turns);
		return gameStateClone;
	}

	public boolean gameIsOver() {
		int nAlive = 0;
		if (getRemainingTurns() == 0) { return true; }
		for (Player p : getPlayers()) {
			if (p.isAlive()) {
				nAlive++;
			}
			if (nAlive > 1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return the winner or null if none (draw or the game is not over)
	 * @return An instance of Player or null
	 */
	public Player getWinner() {
		int nAlive = 0;
		Player winner = null;
		for (Player p : getPlayers()) {
			if (p.isAlive()) {
				nAlive++;
				winner = p;
			}
			if (nAlive > 1) {
				winner = null;
			}
		}
		return winner;
	}

	public Player getCurrentPlayer() {
		return getPlayers().get(current_player_id);
	}

	public List<Action> getAllPossibleActions() {

		List<Action> possibleActions = new ArrayList<Action>();

		Player current_player = getPlayers().get(current_player_id);

		// Right
		List<Cell> adjacentCells = current_player.getCell().getAdjacentCells();
		if (adjacentCells.get(0) != null && adjacentCells.get(0).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0) {
				possibleActions.add(Action.DROP_BOMB_RIGHT);
			}
			if (current_player.getMovesRemaining() > 0) {
				possibleActions.add(Action.MOVE_RIGHT);
			}
		}
		// Left
		if (adjacentCells.get(2) != null && adjacentCells.get(2).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0) {
				possibleActions.add(Action.DROP_BOMB_LEFT);
			}
			if (current_player.getMovesRemaining() > 0) {
				possibleActions.add(Action.MOVE_LEFT);
			}
		}
		// Up
		if (adjacentCells.get(1) != null && adjacentCells.get(1).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0) {
				possibleActions.add(Action.DROP_BOMB_UP);
			}
			if (current_player.getMovesRemaining() > 0) {
				possibleActions.add(Action.MOVE_UP);
			}
		}
		// Down
		if (adjacentCells.get(3) != null && adjacentCells.get(3).isWalkable()) {
			if (current_player.getNumberBombRemaining() > 0) {
				possibleActions.add(Action.DROP_BOMB_DOWN);
			}
			if (current_player.getMovesRemaining() > 0) {
				possibleActions.add(Action.MOVE_DOWN);
			}
		}
		
		// if the player is alive so it can choose to
		// pass the turn by activating the Action.EndTurn
		possibleActions.add(Action.ENDTURN);
		
		return possibleActions;
	}
	
	public boolean turnIsOver() {
    	return (getCurrentPlayer().getMovesRemaining() == 0 && getCurrentPlayer().getNumberBombRemaining() == 0);
    }
	
	protected void nextPlayer() {
		do {
			current_player_id = (current_player_id + 1) % getPlayers().size();
		} while (!getPlayers().get(current_player_id).isAlive());
		getPlayers().get(current_player_id).initiateTurn();
	}
	
    public void endTurn()
    {
	    maze.processEndTurn();
	    getCurrentPlayer().endTurn();
	    
	    remaining_turns--;
	    if (!gameIsOver()) {
	    	nextPlayer();
	    }

    }


	public void apply(Action action) {
		switch (action) {
		case MOVE_UP:
			getCurrentPlayer().move(Directions.UP);
			break;
			
		case MOVE_DOWN:
			getCurrentPlayer().move(Directions.DOWN);
			break;
			
		case MOVE_LEFT:
			getCurrentPlayer().move(Directions.LEFT);
			break;
			
		case MOVE_RIGHT:
			getCurrentPlayer().move(Directions.RIGHT);
			break;
			
		case DROP_BOMB_RIGHT:
			getCurrentPlayer().dropBomb(Directions.RIGHT);
			break;
			
		case DROP_BOMB_UP:
			getCurrentPlayer().dropBomb(Directions.UP);
			break;
			
		case DROP_BOMB_LEFT:
			getCurrentPlayer().dropBomb(Directions.LEFT);
			break;
			
		case DROP_BOMB_DOWN:
			getCurrentPlayer().dropBomb(Directions.DOWN);
			break;
			
		case ENDTURN:
			endTurn();
			break;
			
		default:
			break;
		}

		// Force to end the turn if no more action are possible
		if (action != Action.ENDTURN && turnIsOver()) {
			endTurn();
		}

	}

	public Maze getMaze() {
		return maze;
	}
	
	public int getRemainingTurns() {
		return remaining_turns;
	}

}
