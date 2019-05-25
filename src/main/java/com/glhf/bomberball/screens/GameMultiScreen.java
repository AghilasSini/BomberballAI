package com.glhf.bomberball.screens;

import com.badlogic.gdx.Gdx;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.ai.AIThread;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.NumberTurn;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.ui.GameUI;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.maze.Maze;


import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class GameMultiScreen extends GameScreen {

	private List<Player> players;
	private int maze_id;
	private Player winner;
	protected int current_player_id;
	protected BlockingQueue<Object> enAttente;
	public static final int TURN_LIMIT = 40;

	public GameMultiScreen(Maze maze, int maze_id) {
		super(maze);
		this.maze_id = maze_id;
		GameMultiConfig config = GameMultiConfig.get();
		this.players = maze.spawnPlayers(config.player_count);
		this.enAttente = new LinkedBlockingQueue<Object>(1);
		NumberTurn.getInstance().setNbTurn(TURN_LIMIT);
		addUI(new GameUI(players, false, true));
		addUI(maze_drawer);
		startGame();
	}

	// added by asini: Constructor for AI purpose

	public GameMultiScreen(Maze maze, int maze_id, List<Player> players) {
		super(maze);
		this.maze_id = maze_id;
		this.players = players;
//		this.maze.setPlayers(players);
		this.maze.spawnPlayers(players);
		this.enAttente = new LinkedBlockingQueue<Object>(1);
		NumberTurn.getInstance().setNbTurn(TURN_LIMIT);
		addUI(new GameUI(this.players, false, true));
		addUI(maze_drawer);
		startGame();
	}

	public boolean gameIsOver() {
		int nAlive = 0;
		for (Player p : players) {
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
	 * gives the next player after a turn. If the next player is dead, choose the
	 * following player.
	 */
	@Override
	protected void nextPlayer() {
		winner = null;
		boolean is_last = true;
		
		try {
			enAttente.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (Player p : players) {
			if (winner == null && p.isAlive()) {
				winner = p;
			} else if (p.isAlive()) {
				is_last = false;
			}
		}
		if (!is_last) { winner = null; }
		if (NumberTurn.getInstance().getNbTurn() == 0 || is_last) {
			endGame();
			return;
		}

		int i = players.indexOf(current_player);
		do {
			i = (i + 1) % players.size();
		} while (!players.get(i).isAlive());
		current_player_id = i;
		current_player = players.get(i);
		current_player.initiateTurn();
		setMoveEffect();
		setMoveMode();


	}

	@Override
	protected void endGame() {
		final int final_maze_id = this.maze_id;
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				Bomberball.changeScreen(new VictoryMenuScreen(winner, final_maze_id, true));
			}
		});
	}

	@Override
	protected void startGame() {

		current_player = players.get(0);
		current_player.setPlayerId(0);
		current_player.initiateTurn(); // after the UI because initiateTurn notify the ui

		setMoveMode();

		Thread t = new Thread(() -> {
			while (!gameIsOver()) {
				// Tries to take the turn
				try {
					enAttente.put(new Object());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (current_player instanceof AbstractAI) {
					launchTurn();
				}
			}
		});
		t.start();

	}

/// Handling the AI 
	@SuppressWarnings("deprecation")
	public void playAI() {
		System.out.println("----> Playing " + current_player);
		if (current_player instanceof AbstractAI) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			AbstractAI ia = (AbstractAI) current_player;
			// Create virtual game state on which simulations can be performed
			GameState gameState = new GameState(((Maze) this.maze.clone()), current_player_id, NumberTurn.getInstance().getNbTurn());
			AIThread calcul = new AIThread(ia, gameState, executor);
			executor.execute(calcul);
			try {
				if (!executor.awaitTermination(AbstractAI.TIME_TO_THINK, TimeUnit.MILLISECONDS)) {
					executor.shutdown();
				}
			} catch (InterruptedException e) {
				e.getStackTrace();
			}

			try {
				calcul.join();
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
			Action action = null;
			if (calcul.getChoosedAction() != null) {
				action = calcul.getChoosedAction();
			}
			else if (calcul.getChoosedAction() == null && ia.getMemorizedAction() != null) {
				System.err.println("Aucune action choisie mais action mémorisée");
				action = ia.getMemorizedAction();
			}
			
			// Backup to a random choice if needed
			while (action == null || !isPossibleAction(action)) {
				System.out.println("Choix de l'action au hasard");
				Random rand = new Random();
				List<Action> possibleActions= gameState.getAllPossibleActions();
				int actionIndex=rand.nextInt(possibleActions.size());
				action = possibleActions.get(actionIndex);
			}

			System.out.println("--------------------------------------------------------------> " + action);
//			if (NumberTurn.getInstance().getNbTurn() == TURN_LIMIT) {
//				action = Action.DROP_BOMB_LEFT;
//			}
			applyAction(action);

			// Kill remaining IAThread threads
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				for (StackTraceElement ste : t.getStackTrace()) {
					if (ste.getClassName().equals("com.glhf.bomberball.ai.AIThread")) {
						t.stop();
					}
				}
			}

		}

	}

	public void launchTurn() {
		System.out.println("=============== Tour de " + current_player + " ===============");
		int initial_player_id = current_player_id;

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (initial_player_id == current_player_id && !turnIsOver() && !gameIsOver()) {
			playAI();
		}
		System.out.println("--------------- Fin de tour de " + current_player + " ---------------");

	}

}
