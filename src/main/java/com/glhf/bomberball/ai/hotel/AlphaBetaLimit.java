package com.glhf.bomberball.ai.hotel;

import com.badlogic.gdx.Game;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaLimit extends AbstractAI{

    public AlphaBetaLimit(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"AlphaBetaLimit",playerId);
        // TODO Auto-generated constructor stub
    }

    /**
     * hat methods gives the action the current player has to do according to the heuristic returned by alphabeta
     * @param state
     * @return
     */
    @Override
    public Action choosedAction(GameState state) {
        return premier(state, -1000, 1000);
    }

    /**
     * That methods gives the action the current player has to do according to the heuristic returned by alphabeta
     * @param state
     * @param alpha
     * @param beta
     * @return
     */
    public Action premier(GameState state, int alpha, int beta){
        List<Action> sons = state.getAllPossibleActions();
        int i = 0;
        Action best = null;
        int tmp;
        int depth = 2;

        while(i < sons.size() && alpha < beta){
            GameState state1 = state.clone();
            state1.apply(sons.get(i));
            tmp = alpha;
            alpha = max(alpha , alphaBetaLimit(state1, alpha, beta, depth));
            if(alpha > tmp){
                best = sons.get(i);
            }
            i++;
        }
        return best;
    }

    /**
     * That method tells if the state considered is "game finished"
     * @param state
     * @return
     */
    public boolean terminal(GameState state){
        return state.gameIsOver();
    }

    /**
     * Stupid heuristic for now that only returns 0
     * @return
     */
    public int heuristic(GameState state){
        ConvDescript descriptor = new ConvDescript(state);
        return 100*descriptor.distance_joueur() + 500*descriptor.canPoseBombAndStillBeSafe() + 10*descriptor.scoreSpec() + 15*descriptor.nbEnemiesAlive() + 50*descriptor.distBonus() + 50*descriptor.isSafe();
    }

    /**
     * This method gives the score given to a state, if the considered AI wins, then score = 1000, if the other AI wins then score = -1000
     * @param state
     * @return
     */
    public int utilite(GameState state){
        int id = getPlayerId();
        boolean allDead = true;
        // si l'ia considérée est morte
        if(!state.getPlayers().get(id).isAlive()){
            //on vérifie si les autres ia ont été tuée
            for(Player p : state.getPlayers()){
                if(p.isAlive()){
                    allDead = false;
                }
            }
            // si toutes les IA sont mortes, égalité
            if(allDead){
                return 0;
            }
            else{
                // l'IA considérée est perdante
                return -1000;
            }

        }
        else{
            // IA considérée a gagné
            return 1000;
        }
    }

    /**
     * Limited depth alpha beta algorithm
     * @param state
     * @param alpha
     * @param beta
     * @param depth
     * @return
     */
    public int alphaBetaLimit(GameState state, int alpha, int beta, int depth){
        if(terminal(state)){
            return utilite(state);
        }
        // if the max depth has been reached then the score of the state is given by the heuristic
        else if(depth == 0){
            return heuristic(state);
        }
        else if(state.getCurrentPlayer().getPlayerId() == getPlayerId()){
            List<Action> sons = state.getAllPossibleActions();
            int i = 0;
            while(i < sons.size() && alpha < beta){
                GameState state1 = state.clone();
                state1.apply(sons.get(i));
                alpha = max(alpha , alphaBetaLimit(state1, alpha, beta, depth-1));
                i++;
             //   System.out.println("alpha= " + alpha);
            }
            return alpha;
        }
        else{
            List<Action> sons = state.getAllPossibleActions();
            int i = 0;
            while(i < sons.size() && alpha < beta){
                GameState state1 = state.clone();
                state1.apply(sons.get(i));
                beta = min(beta , alphaBetaLimit(state1, alpha, beta, depth-1));
                i++;
               // System.out.println("beta= "+ beta);
            }
            return beta;
        }
    }

    /**
     * max methods that return the greatest of the 2 given numbers
     * @param x
     * @param y
     * @return
     */
    public int max(int x,int y){
        if (x>y){
            return x;
        }
        else{
            return y;
        }
    }

    /**
     * max methods that return the smallest of the 2 given numbers
     * @param x
     * @param y
     * @return
     */
    public int min(int x,int y){
        if(x<y){
            return x;
        }
        else{
            return y;
        }
    }
}
