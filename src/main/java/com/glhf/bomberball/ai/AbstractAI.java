package com.glhf.bomberball.ai;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

public abstract class AbstractAI extends Player {
	protected String name;

	public static int TIME_TO_THINK = 1000;
	protected Action memorizedAction;

	public AbstractAI(GameConfig config, String player_skin, String name, int playerId) {

		super(player_skin, config.player_life, config.initial_player_moves,config.initial_bomb_number,
				config.initial_bomb_range);
		this.name = name;
		this.player_id = playerId;
		// TODO Auto-generated constructor stub
	}

	public void setMemorizedAction(Action memorizedAction) {

		this.memorizedAction = memorizedAction;
	}

	public Action getMemorizedAction() {
		return memorizedAction;

	}

	public final void memorizeAction(Action action) {

		this.memorizedAction = action;

	}

	public String getPlayerName() {
		return name;
	}

	public abstract Action choosedAction(GameState gameState);

	@Override
	public String toString() {
		return "AbstractAI [name=" + name + ", player_id=" + player_id + "]";
	}

}
