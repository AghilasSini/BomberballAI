package com.glhf.bomberball.ai;


import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

public abstract class AbstractAI extends Player {
	protected String name;
	protected int playerId;

	public static int TIME_TO_THINK=2000;
	protected Action memorizedAction;
	
	
	
	
	
	
	public AbstractAI(GameConfig config,String player_skin,String name,int playerId) {

		super(player_skin, config.player_life, config.initial_player_moves, config.initial_bomb_number, config.initial_bomb_range);
		this.name= name;
		this.playerId=playerId;
		// TODO Auto-generated constructor stub
	}
	
	
	


	public void setMemorizedAction(Action memorizedAction) {
		
		this.memorizedAction = memorizedAction;
	}

	
	public Action getMemorizedAction() {
		return memorizedAction;
		
	}
	public final void memorizeAction(Action action) {
		
	       if (action != null) {
	           // this.memorizedAction = (Action) action.clone();
	            System.out.println("##############################");
	            System.out.println("Memorized Action :" + memorizedAction.toString());
	            System.out.println("##############################");
	        }
	        else {
	        	this.memorizedAction = action;
	        }
		
		
		
		
	}
	
	
	public String getPlayerName() {
		return name;
	}
	
	
	public int getPlayerId() {
		return playerId;
	}





	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public abstract Action choosedAction(GameState gameState);
	
}
