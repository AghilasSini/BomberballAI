package com.glhf.bomberball.ai.papa;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;



public class AlphaBeta extends AbstractAI {



    GameState etatInit= null;

    static int PROFONDEUR_MAX = 10;


    public AlphaBeta(GameConfig config, String player_skin,int playerId) {
        super(config, player_skin, "AlphaBeta", playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        etatInit = gameState;
        memorizedAction=null;
        System.out.println("<<<<<<<>>>>>>>>>>>>"+etatInit);
        alphaBeta(etatInit, -9999,+9999, 0);
        return memorizedAction;
    }




    ///TODO : remove LOOP
    public int alphaBeta(GameState e, int alpha, int beta,int prof){


        if(isTerminal(e)){
            return utilite(e,prof);
        }else{// si l'état n'est pas terminal
            if(isTypeMax(e)){
                //System.out.println("le joueur est "+e.getIdJoueurCourant());
                for(int j=0 ; (j<e.getAllPossibleActions().size()) && (alpha < beta) && prof<PROFONDEUR_MAX ; j++) {


                    GameState nouvelEtat = e.clone();
                    nouvelEtat.apply(nouvelEtat.getAllPossibleActions().get(j));

                    //nouvelEtat.endTurn();


                    int newAlpha = Math.max(alphaBeta(nouvelEtat, alpha, beta, prof+1), alpha);
                    //System.out.println("new alpha = " + newAlpha + " , alpha = " + alpha);
                    if (newAlpha > alpha) {
                        alpha = newAlpha;
                        if(e.equals(etatInit)){
                            memorizedAction= e.getAllPossibleActions().get(j);

                        }


                    }


                }
                return alpha;
            }else{
                if(isTypeMin(e)){
                    for(int j=0 ; j<e.getAllPossibleActions().size() && (alpha<beta) && prof<PROFONDEUR_MAX; j++){
                        GameState nouvelEtat = e.clone();

                        nouvelEtat.apply(nouvelEtat.getAllPossibleActions().get(j));
                        //nouvelEtat.endTurn();


                        beta  = Math.min(alphaBeta(nouvelEtat, alpha, beta, prof+1),beta);
                    }
                    return beta;
                }
            }
        }

        return -12; // inutile en réalité
    }

    private boolean isTerminal(GameState e) {
        return e.gameIsOver();
    }


    private boolean isTypeMin(GameState e) {
        return !(e.getCurrentPlayerId()== this.getPlayerId());
    }

    private boolean isTypeMax(GameState e) {

        return e.getCurrentPlayerId()== this.getPlayerId();
    }


    private int utilite(GameState e, int prof){
        Player winner = e.getWinner();
        if(e.getWinner()!=null){
            if(winner.getPlayerId() == this.getPlayerId()) {
                return 200;
            }else{
                return -200;
            }
        }else{
            return -8000;
        }

    }

}
