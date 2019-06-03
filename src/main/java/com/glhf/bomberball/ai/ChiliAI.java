package com.glhf.bomberball.ai;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ChiliAI extends AbstractAI {

	private static int minUtility = -100;
	private static int maxUtility = 100;
	private List<Pair<Integer, Action>> actionsJouees;


	public ChiliAI(GameConfig config, String player_skin, int playerId) {
		super(config, "chort", "ChiliAI", playerId);
		actionsJouees = new LinkedList<Pair<Integer, Action>>();
	}

	@Override
	public Action choosedAction(GameState gameState) {
		int profondeurLimite = 1;
		List<Action> actionsPossibles = gameState.getAllPossibleActions();
		if (actionsPossibles.size() == 1) {
			return actionsPossibles.get(0);
		}
		for (int i = 0; i < 1; i++) {
			System.out.println("[PROFONDEUR = " + profondeurLimite + "]");
			Action action = startAlphaBeta(gameState, actionsPossibles, profondeurLimite);
			this.memorizeAction(action);
			profondeurLimite++;
		}
		return getMemorizedAction();
	}

	private Action startAlphaBeta(GameState gameState, List<Action> actionsPossibles, int profondeurLimite) {
		int bestAlpha = minUtility;
		Action bestAction = null;
		int newAlpha;
		for (Action action : actionsPossibles) {
			GameState state = gameState.clone();
			state.apply(action);
			actionsJouees.add(new Pair<Integer, Action>(state.getCurrentPlayerId(), action));
			System.out.println("Test "+action);
			newAlpha = alphaBeta(bestAlpha, maxUtility, state, 1, profondeurLimite);
			actionsJouees.remove(actionsJouees.size()-1);
			if (newAlpha > bestAlpha) {
				bestAlpha = newAlpha;
				bestAction = action;
			}
		}

		System.out.println("[ACTION = "+bestAction+" "+bestAlpha+"]");
		return bestAction;
	}

	private int alphaBeta(int alpha, int beta, GameState state, int profondeur, int profondeurLimite) {

		/*
		 * Partie terminée
		 */
		if (state.gameIsOver()) {
			int u = utilite(state);
			System.out.println("[U]\t"+actionsJouees+ "\t"+u);
			return u;
		}
		/*
		 * Profondeur limite atteinte
		 */
		if (profondeur == profondeurLimite) {
			System.out.print("[H]\t"+actionsJouees);
			state.endTurn();
			int h = heuristique(state);
			System.out.println("\t"+h);
			return h;
		}
		/*
		 * Noeud MAX
		 */
		if (state.getCurrentPlayerId() == this.getPlayerId()) {
			List<Action> actions = state.getAllPossibleActions();
			int n = actions.size();
			for (int i = 0; i < n && alpha < beta; i++) {
				GameState newState = state.clone();
				Action chosenAction = actions.get(i);
				newState.apply(chosenAction);
				actionsJouees.add(new Pair<Integer, Action>(state.getCurrentPlayerId(), chosenAction));
				alpha = max(alpha, alphaBeta(alpha, beta, newState, profondeur+1, profondeurLimite));
				actionsJouees.remove(actionsJouees.size()-1);
			}
			return alpha;
		}
		/*
		 *  Noeud MIN
		 */
		else {
			List<Action> actions = state.getAllPossibleActions();
			int n = actions.size();
			for (int i = 0; i < n && alpha < beta; i++) {
				GameState newState = state.clone();
				Action chosenAction = actions.get(i);
				newState.apply(chosenAction);
				actionsJouees.add(new Pair<Integer, Action>(state.getCurrentPlayerId(), chosenAction));
				beta = min(beta, alphaBeta(alpha, beta, newState, profondeur+1, profondeurLimite));
				actionsJouees.remove(actionsJouees.size()-1);
			}
			return beta;
		}

	}
	
	private int utilite(GameState state) {
		Player winner = state.getWinner();
		if (winner == null) {
			return 0;
		}
		else if (winner instanceof AbstractAI && ((AbstractAI) winner).getPlayerId() == this.getPlayerId()) {
			return maxUtility;
		}
		else {
			return minUtility;
		}
	}
	
	public static int L1(int x1, int y1, int x2, int y2) {
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
	
	private int heuristique(GameState state) {
		Player winner = state.getWinner();
		if (winner == null && !state.gameIsOver()) {
			Player player1 = state.getPlayers().get(0);
			Player player2 = state.getPlayers().get(1);
			return maxUtility-L1(player1.getCell().getX(),
					player1.getCell().getY(),
					player2.getCell().getX(),
					player2.getCell().getY());
		}
		if (winner == null && state.gameIsOver()) {
			return 0;
		}
		else if (winner instanceof AbstractAI && ((AbstractAI) winner).getPlayerId() == this.getPlayerId()) {
			return maxUtility;
		}
		else {
			return minUtility;
		}
	}

}
