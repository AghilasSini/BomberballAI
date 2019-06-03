package com.glhf.bomberball.ai.hotel;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.utils.Action;

public class StateAndTransition {
    private GameState state;
    private Action action;

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public StateAndTransition(GameState state, Action transition){
        this.state = state;
        this.action = transition;
    }
}
