package com.glhf.bomberball.ai.papa;


import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.BonusWall;
import com.glhf.bomberball.gameobject.DestructibleWall;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.utils.Action;



public class Papa extends AbstractAI {



    GameState etatInit= null;

    int PROFONDEUR_MAX ;
    boolean flag=true;
    Action bestAction;


    public Papa(GameConfig config, String player_skin,int playerId) {
        super(config, player_skin, "Papa", playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        etatInit = gameState;
        memorizedAction=null;
        PROFONDEUR_MAX=1;
        while(flag){
            alphaBeta(etatInit, -9999,+9999, 0);
            memorizedAction = bestAction;
            PROFONDEUR_MAX++;
        }

        return memorizedAction;
    }



    /**
     *
     * Adapted alpha-beta with heurisctics
     *
     * */
    public int alphaBeta(GameState e, int alpha, int beta,int prof){


        if(isTerminal(e)){
            return utilite(e);
        }
        else if(prof == PROFONDEUR_MAX){
            return heuristic(e);
        }else{// si l'état n'est pas terminal
            if(isTypeMax(e)){
                for(int j=0 ; (j<e.getAllPossibleActions().size()) && (alpha < beta) && prof<PROFONDEUR_MAX ; j++) {

                    GameState nouvelEtat = e.clone();

                    nouvelEtat.apply(nouvelEtat.getAllPossibleActions().get(j));

                    int newAlpha = Math.max(alphaBeta(nouvelEtat, alpha, beta, prof+1), alpha);
                    if (newAlpha > alpha) {
                        alpha = newAlpha;
                        if(e.equals(etatInit)){
                            bestAction= e.getAllPossibleActions().get(j);

                        }


                    }


                }
                return alpha;
            }else{
                if(isTypeMin(e)){
                    for(int j=0 ; j<e.getAllPossibleActions().size() && (alpha<beta) && prof<PROFONDEUR_MAX; j++){
                        GameState nouvelEtat = e.clone();

                        nouvelEtat.apply(nouvelEtat.getAllPossibleActions().get(j));

                        beta  = Math.min(alphaBeta(nouvelEtat, alpha, beta, prof+1),beta);
                    }
                    return beta;
                }
            }
        }

        return -12; // inutile en réalité
    }


    /**
     *
     * Calculate an heuristic for a state.
     *
     * */
    private int heuristic(GameState e) {

        int nb_blocs=0;
        int nb_bonusWall=0;
        int nb_bonusJoueur = e.getPlayers().get(this.getPlayerId()).bonus_bomb_range
                +e.getPlayers().get(this.getPlayerId()).bonus_bomb_number
                +e.getPlayers().get(this.getPlayerId()).bonus_moves;

        Maze maze = e.getMaze();
        for(int i=0;i<maze.getWidth();i++){
            for(int j=0;j<maze.getHeight();j++){
                if(maze.getCells()[i][j].hasInstanceOf(DestructibleWall.class)){
                    nb_blocs++;
                }else if(maze.getCells()[i][j].hasInstanceOf(BonusWall.class)){
                    nb_bonusWall++;
                }
            }
        }
        int h = (nb_bonusJoueur*30)-nb_bonusWall*2-(nb_blocs);

        return (h);

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


    /**
     *
     * Give the utility of a terminal state.
     *
     * */
    private int utilite(GameState e){
        Player winner = e.getWinner();
        if(e.getWinner()!=null){
            if(winner.getPlayerId() == this.getPlayerId()) {
                return 1000;
            }else{
                return -1000;
            }
        }else{
            return -100;
        }

    }

}
