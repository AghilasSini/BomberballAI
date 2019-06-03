package com.glhf.bomberball.ai.papa;

import com.glhf.bomberball.utils.Action;

import java.util.Stack;

public class ActionNode {

    Stack<Action> actions;
    int heuristique;
    boolean haveAncestorsDroppedBomb;

    public ActionNode(){
        actions= new Stack<>();
        heuristique=-10000;
        haveAncestorsDroppedBomb=false;
    }

    public ActionNode clone(){
        ActionNode aN = new ActionNode();
        aN.actions = new Stack<>();
        aN.heuristique = this.heuristique;
        aN.haveAncestorsDroppedBomb = this.haveAncestorsDroppedBomb;
        return  aN;

    }


    public String toString(){

        return "******Actions : "+ actions+ " \nH : "+heuristique+"**********";
    }
}
