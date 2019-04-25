package com.glhf.bomberball.ai;

import com.glhf.bomberball.utils.Action;

import java.util.List;
import java.util.Random;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;

public class VanillaAI extends AbstractAI{
	
	

	public VanillaAI(GameConfig config,String player_skin,int playerId) {
		super(config,player_skin,"VanillaAI",playerId);		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Action choosedAction(GameState gameState) {
		Random rand = new Random();
		List<Action> possibleActions= gameState.getAllPossibleActions();
		int actionIndex=rand.nextInt(possibleActions.size());
		return  possibleActions.get(actionIndex);
	}
	

}
