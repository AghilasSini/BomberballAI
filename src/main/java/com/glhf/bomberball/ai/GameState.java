package com.glhf.bomberball.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.screens.GameScreen;
import com.glhf.bomberball.screens.VictoryMenuScreen;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;



public class GameState {
	private Maze maze;
	private List<Player> players;
	private int currentPlayerId;
	private Player currentPlayer;

	 
	 
	 
	public GameState(Maze maze, int currentPlayerId) {
		this.maze=maze;
		this.players= maze.getPlayers();
		this.currentPlayerId = currentPlayerId;
		currentPlayer=players.get(currentPlayerId);
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public int getCurrentPlayerId() {
		return currentPlayerId;
	}
	public void setCurrentPlayerId(int currentPlayerId) {
		this.currentPlayerId=currentPlayerId;
	}
	public GameState clone() {
		GameState gameSateClone=new GameState((Maze) this.maze.clone(),this.currentPlayerId);
		gameSateClone.currentPlayer=(Player) currentPlayer.clone();
		return gameSateClone;
		
	}
	public boolean isOver() {
		int nAlive = 0;
		for (Player p : players) {
			if (p.isAlive()) { nAlive++; }
			if (nAlive > 1) { return false; }
		}
		return true;
	}
	
	public Player getCurrentPlayer() {
		return players.get(currentPlayerId);
	}
	
	
	public List<Action> getAllPossibleActions(){
		
		List <Action> possibleActions=new ArrayList<Action>();
		// if the player is alive so it can choose to 
		// pass the turn by activating the Action.EndTurn
		possibleActions.add(Action.ENDTURN);
		
		currentPlayer=players.get(currentPlayerId);
		List<Cell> reachableCells=MazeTransversal.getReacheableCells(currentPlayer.getCell());
		for (Cell cell :reachableCells) {
			// get the direction
			Directions possibleDirection=currentPlayer.getCell().getCellDir(cell);
			
			
			if (possibleDirection !=null ){
				if(currentPlayer.getMovesRemaining()>0)
					possibleActions.add(getPossibleMove(possibleDirection));
			
			
				if(currentPlayer.getNumberBombRemaining()>0)
						possibleActions.add(getPossibleBombDropping(possibleDirection));
	
			}
			
		
		}
		
		return possibleActions;
	}
		
		
		
	private Action getPossibleMove(Directions direction) {
		Action possibleAction=null;
		 switch (direction) {
			case DOWN:
				possibleAction=Action.MOVE_DOWN;
				break;
			case LEFT:
				possibleAction=Action.MOVE_LEFT;
			case UP:
				possibleAction=Action.MOVE_UP;
				break;
			case RIGHT:
				possibleAction=Action.MOVE_RIGHT;
				break;
			default:
				break;
			}
		 return possibleAction;
	}

	private Action getPossibleBombDropping(Directions direction) {
		Action possibleAction=null;
		 switch (direction) {
			case DOWN:
				possibleAction=Action.DROP_BOMB_DOWN;
				break;
			case LEFT:
				possibleAction=Action.DROP_BOMB_LEFT;
			case UP:
				possibleAction=Action.DROP_BOMB_UP;
				break;
			case RIGHT:
				possibleAction=Action.DROP_BOMB_RIGHT;
				break;
			default:
				break;
			}
		 return possibleAction;
	}

	
	
	public void apply(Action action) {
		// TODO Auto-generated method stub
		currentPlayer=getCurrentPlayer();
		switch (action) {
		case MOVE_UP:
			
			currentPlayer.move(Directions.UP);
			
			break;
		case MOVE_DOWN:
		
			currentPlayer.move(Directions.DOWN);
			break;
		case MOVE_LEFT:
			
			currentPlayer.move(Directions.LEFT);
			break;
		case MOVE_RIGHT:
		
			currentPlayer.move(Directions.RIGHT);
			break;
		case DROP_BOMB_RIGHT:
			
			currentPlayer.dropBomb(Directions.RIGHT);
			break;
		case DROP_BOMB_UP:
		
			currentPlayer.dropBomb(Directions.UP);
			break;
		case DROP_BOMB_LEFT:
		
			currentPlayer.dropBomb(Directions.LEFT);
			break;
		case DROP_BOMB_DOWN:
			
			currentPlayer.dropBomb(Directions.DOWN);
			break;
		case ENDTURN:
			currentPlayer.endTurn();
			break;
		default:
			break;
		}
		
	}
	


   
	
	
    public Maze getMaze() {
		return maze;
	}
	
	
}
