package com.glhf.bomberball.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.utils.Action;




public class RandomAI extends AbstractAI{


	public RandomAI(GameConfig config,String player_skin,int playerId) {
		super(config,player_skin,"RandomAI",playerId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Action choosedAction(GameState gameState) {
		Random rand = new Random();
		List<Action> possibleActions= gameState.getAllPossibleActions();
		int actionIndex=rand.nextInt(possibleActions.size());
		System.out.println(possibleActions.get(actionIndex));
		return  possibleActions.get(actionIndex);
	}

	


}
