package com.glhf.bomberball.ai.hotel;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.List;

public class AlphaBetaID extends AbstractAI {

    public AlphaBetaID(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"AlphaBetaID",playerId);
        // TODO Auto-generated constructor stub
    }

    /**
     * hat methods gives the action the current player has to do according to the heuristic returned by alphabeta
     * @param state
     * @return
     */
    @Override
    public Action choosedAction(GameState state) {
        ID(state,-1000,1000);
        return null;
    }

    /**
     * That methods gives the action the current player has to do according to the heuristic returned by alphabeta
     * @param state
     * @param alpha
     * @param beta
     * @return
     */
    public Action premier(GameState state, int alpha, int beta,int depth){
        List<Action> sons = state.getAllPossibleActions();
        int i = 0;
        Action best = null;
        int tmp;
        while(i < sons.size() && alpha < beta){
            GameState state1 = state.clone();
            state1.apply(sons.get(i));
            tmp = alpha;
            alpha = max(alpha , alphaBetaID(state1, alpha, beta, depth));
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
    public int alphaBetaID(GameState state, int alpha, int beta, int depth){
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
                alpha = max(alpha , alphaBetaID(state1, alpha, beta, depth-1));
                i++;
            //    System.out.println("alpha= " + alpha);
            }
            return alpha;
        }
        else{
            List<Action> sons = state.getAllPossibleActions();
            int i = 0;
            while(i < sons.size() && alpha < beta){
                GameState state1 = state.clone();
                state1.apply(sons.get(i));
                beta = min(beta , alphaBetaID(state1, alpha, beta, depth-1));
                i++;
              //  System.out.println("beta= "+ beta);
            }
            return beta;
        }
    }

    /**
     * Fonction initiale permettant de calculer l'action à faire profondeur par profondeur
     * @param state un état
     * @param alpha alpha
     * @param beta beta
     */
    public void ID(GameState state,int alpha,int beta){
        int profondeur=1;
        Action a;
        while (true){
            a=premier(state, alpha, beta,profondeur);
            this.setMemorizedAction(a);
            System.out.println("Profondeur="+profondeur);
            profondeur++;
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
