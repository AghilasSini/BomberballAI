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
	private GameScreen gameScreen;
	 private Player winner;
	 
	 
	 
	public GameState(Maze maze, int currentPlayerId) {
		this.maze=maze;
		this.players= maze.getPlayers();
		this.currentPlayerId = currentPlayerId;
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
		return new GameState((Maze) this.maze.clone(),this.currentPlayerId);
		
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
		System.out.println(getCurrentPlayerId()+"<->"+ players.size());
		currentPlayer=players.get(getCurrentPlayerId());
		List<Cell> reachableCells=MazeTransversal.getReacheableCells(currentPlayer.getCell());		
		for (Cell cell :reachableCells) {
		
			Directions possibleDirection=currentPlayer.getCell().getCellDir(cell);
			if (possibleDirection !=null ){
				if(currentPlayer.getMovesRemaining()>0)
					possibleActions.add(getPossibleMove(possibleDirection));
				if(currentPlayer.getNumberBombRemaining()>0){
					possibleActions.add(getPossibleBombDropping(possibleDirection));
				}
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
			gameScreen.setMoveMode();
			currentPlayer.move(Directions.UP);
			
			break;
		case MOVE_DOWN:
			gameScreen.setMoveMode();
			currentPlayer.move(Directions.DOWN);
			break;
		case MOVE_LEFT:
			gameScreen.setMoveMode();
			currentPlayer.move(Directions.LEFT);
			break;
		case MOVE_RIGHT:
			gameScreen.setMoveMode();
			currentPlayer.move(Directions.RIGHT);
			break;
		case DROP_BOMB_RIGHT:
			gameScreen.setBombMode();
			currentPlayer.dropBomb(Directions.RIGHT);
			break;
		case DROP_BOMB_UP:
			gameScreen.setBombMode();
			currentPlayer.dropBomb(Directions.UP);
			break;
		case DROP_BOMB_LEFT:
			gameScreen.setBombMode();
			currentPlayer.dropBomb(Directions.LEFT);
			break;
		case DROP_BOMB_DOWN:
			gameScreen.setBombMode();
			currentPlayer.dropBomb(Directions.DOWN);
			break;
		case ENDTURN:
			
			maze.processEndTurn();
			gameScreen.endTurn();
			break;
		default:
			break;
		}
		
	}
	


    @SuppressWarnings("deprecation")
	public synchronized void playAI() {
		
    	if (maze.getPlayers().get(currentPlayerId) instanceof AbstractAI) {
    		ExecutorService executor = Executors.newSingleThreadExecutor();
            AbstractAI ia = (AbstractAI) maze.getPlayers().get(currentPlayerId);
            AIThread calcul = new AIThread(ia, this, executor);
            executor.execute(calcul);
            try {
            	 if (!executor.awaitTermination(AbstractAI.TIME_TO_THINK, TimeUnit.MILLISECONDS)) {
            		 executor.shutdown();
            	 }
            }catch (InterruptedException e) {
            	e.getStackTrace();
			}
            
            try {
            	calcul.join();
            }catch (InterruptedException e) {
            	 e.getStackTrace();
			}
            
            Action action;
            if (calcul.getChoosedAction() == null && ia.getMemorizedAction() == null) {
            	GameMultiConfig config = GameMultiConfig.get();
            	action = (Action) new RandomAI(config,config.player_skins[currentPlayerId],currentPlayerId).choosedAction(this);
           
            }else if (calcul.getChoosedAction() == null && ia.getMemorizedAction() != null) {
    		
    		              System.err.println("Aucune action choisie mais action mémorisée");
    		                action = ia.getMemorizedAction();
    		
    		
    		            } else {
    		
    		                action = calcul.getChoosedAction();
    		}
    		
            if (!isOver()) {
            	apply(action);
            }
              // Kill remaining IAThread threads
              for (Thread t : Thread.getAllStackTraces().keySet()) {
                  for (StackTraceElement ste : t.getStackTrace()) {
                      if (ste.getClassName().equals("fr.lesprogbretons.seawar.ia.IAThread")) {
                          t.stop();
                      }
                  }
              }
  
              try {
  
                  Thread.sleep(200);
              } catch (InterruptedException ex) {
  
  
              }
  
            
            
            
    	}
		
	}
	
    

	
    public void launchTurn(GameScreen gameScreen) {
        Thread t = new Thread(() -> {
        	this.gameScreen=gameScreen;
			//logger.debug("Is there any Information");
        	
        	while (getCurrentPlayer() instanceof AbstractAI) {
        	 if (!isOver()) {
                playAI();
                
              }
            	
                
        	}
        });
        
        t.start();
       
    }
	
	
	
	
	
}
