package com.glhf.bomberball.ai.juliett;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.List;

/** Classe IA, version 1 */
/* Pour le jeu BomberBall */

/* Implémentation de l'algorithme alpha-béta en version récursive avec limitation de profondeur et sans heuristique*/

public class IAImplementation extends AbstractAI {

    private int maxUtility = 1000;
    private int minUtility = -1000;
    private int alpha = -1000;
    private int beta = 1000;

    private int meilleurFils;

    private int profondeur = 6;


    //Constructeur
    public IAImplementation(GameConfig config, String player_skin, int playerId) {
        super(config, player_skin, "CustomAI", playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        alphabeta(gameState, alpha, beta, 0);
        return gameState.getAllPossibleActions().get(meilleurFils);
    }

    public int alphabeta(GameState etat, int alpha, int beta, int numero) {
        //if n est terminal then
        if (etat.gameIsOver()) {
            return utilite(etat);

        //si profondeur max atteinte
        } else if (numero >= profondeur) {
            return calcHeuristic(etat);

        } else {
            List<Action> actionsPossibles = etat.getAllPossibleActions();
            if (etat.getCurrentPlayerId() == this.getPlayerId()) {
                //if n est de type Max then
                for(int k = 0; k < actionsPossibles.size() && alpha<beta; k++) {
                    GameState tmp = etat.clone();
                    tmp.apply(actionsPossibles.get(k)); //passe automatiquement au joueur suivant
                    int alphaTemp = Math.max(alpha, alphabeta(tmp, alpha, beta, numero + 1));
                    if (alphaTemp > alpha){
                        alpha = alphaTemp;
                        if(numero == 0){
                            meilleurFils = k;
                        }
                    }
                }
                return (alpha);
            } else {
                for(int k = 0; k < actionsPossibles.size() && alpha<beta; k++) {
                    GameState tmp = etat.clone();
                    tmp.apply(actionsPossibles.get(k));
                    beta = Math.min(beta, alphabeta(tmp, alpha, beta, numero + 1));
                }
                return (beta);
            }
        }
    }

    private int calcHeuristic(GameState etat) {
        return 0;
    }

    private int utilite(GameState state) {
        Player winner = state.getWinner();
        if (winner == null) {
            return 0;
        }
        else if (winner.getPlayerId() == this.getPlayerId()) {
            return maxUtility;
        }
        else {
            return minUtility;
        }
    }

}
