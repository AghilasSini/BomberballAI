package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;

public class VanillaAI extends AbstractAI{
	
	

	public VanillaAI(GameConfig config,String player_skin,int playerId) {
		super(config,"skelet","VanillaAI",playerId);
	}

	@Override
	public Action choosedAction(GameState gameState) {
		Random rand = new Random();
		List<Action> possibleActions= gameState.getAllPossibleActions();
		List<Action> moveActions = new LinkedList<Action>();
		for (Action a : possibleActions) {
			if (a == Action.MOVE_UP
					|| a == Action.MOVE_DOWN
					|| a == Action.MOVE_LEFT
					|| a == Action.MOVE_RIGHT) {
				moveActions.add(a);
			}
		}
		if (moveActions.isEmpty()) {
			moveActions.add(Action.ENDTURN);
		}
		int actionIndex=rand.nextInt(moveActions.size());
//		System.out.println(possibleActions);
		return  moveActions.get(actionIndex);
	}
	

}
