package com.glhf.bomberball.ai;

import java.util.List;
import java.util.Random;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.utils.Action;




public class RandomAI extends AbstractAI{


	public RandomAI(GameConfig config,String player_skin,int playerId) {
		super(config,"mort","RandomAI",playerId);
	}

	@Override
	public Action choosedAction(GameState gameState) {
		Random rand = new Random();
		
		List<Action> possibleActions= gameState.getAllPossibleActions();
		
		int actionIndex=rand.nextInt(possibleActions.size());
		return  possibleActions.get(actionIndex);
//		return  Action.ENDTURN;
	}
	

}
