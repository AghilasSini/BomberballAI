package com.glhf.bomberball.ai;

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.maze.Maze;



public class AIThread extends Thread {


	    /**
	     * Joueur artificiel
	     */
	    private final AbstractAI ai;

	    /**
	     * Partie en cours
	     */
	    private final GameState gameState;
	    
	    /**
	     * Service d'execution du thread
	     */
	    private final ExecutorService executor;

	    /**
	     * Coup choisi a l'issu de la recherche
	     */
	    private Action choosedAction;

	    /**
	     * Constructor
	     * @param ia  Artificial Player
	     * @param gameState  represent  game state
	     * @param executor Service 
	     */
	    public AIThread (AbstractAI ai, GameState gameState, ExecutorService executor) {
	    	super("Calcul");
	    	setName("Calcul");
	        this.ai = ai;
	        this.gameState = gameState;
	        this.executor = executor;
	        this.choosedAction = null;
	    }
	    
	    public Action getChoosedAction() {
	        return choosedAction;
	    }

	    /**
	     * Lance la recherche d'un nouveau coup dans un thread separe
	     */
	    @Override
	    public void run() {
	        try {
	        	choosedAction = ai.choosedAction((GameState) gameState.clone());
	        }
	        catch (Exception ex) {
	            Logger.getLogger(Maze.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        finally {
	           executor.shutdownNow();
	        }
	    }


	
	
	
	
	
	
}
