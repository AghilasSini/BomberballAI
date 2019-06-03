package com.glhf.bomberball.ai.juliett;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.DestructibleWall;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import java.util.List;

/** Classe IA, version 3 */
/* Pour le jeu BomberBall */

/* Implémentation de l'algorithme alpha-béta en version itérative et heuristique améliorée*/

public class IAImplementation3 extends AbstractAI {

    private int maxUtility = 10000;
    private int minUtility = -10000;
    private int alpha = -10000;
    private int beta = 10000;

    private int meilleurFils;

    public IAImplementation3(GameConfig config, String player_skin, int playerId) {
        super(config, player_skin, "CustomAI5", playerId);
    }

    public Action choosedAction(GameState gameState) {
        approfondissementIteratif(gameState,0);
        return null; //Pour qu'on choisisse l'action mémorisée dans la classe GameMultiScreen.
    }

    public int alphabeta(GameState etat, int alpha, int beta, int numero, int n) {
        //if l'état est terminal then
        if (etat.gameIsOver()) {
            return calcHeuristic(etat);

        //si profondeur max atteinte
        } else if (numero >= n) {
            return calcHeuristic(etat);

        } else {
            List<Action> actionsPossibles = etat.getAllPossibleActions();
            if (etat.getCurrentPlayerId() == this.getPlayerId()) {
                //if n est de type Max then
                for(int k = 0; k < actionsPossibles.size() && alpha<beta; k++) {
                    GameState tmp = etat.clone();
                    tmp.apply(actionsPossibles.get(k)); //passe automatiquement au joueur suivant
                    int alphaTemp = Math.max(alpha, alphabeta(tmp, alpha, beta, numero + 1, n));
                    if (alphaTemp > alpha){
                        alpha = alphaTemp;
                        if(numero == 0){ //on ne considère que les meilleurs fils de l'action immédiate, celle de la première profondeur.
                            meilleurFils = k;
                            Action a = actionsPossibles.get(meilleurFils); //meilleurFils obtenu grâce à la recherche dans alphaBeta.
                            memorizeAction(a);
                        }
                    }
                }
                return (alpha);
            } else {
                for(int k = 0; k < actionsPossibles.size() && alpha<beta; k++) {
                    GameState tmp = etat.clone();
                    tmp.apply(actionsPossibles.get(k));
                    beta = Math.min(beta, alphabeta(tmp, alpha, beta, numero + 1, n));
                }
                return (beta);
            }
        }
    }

    private void approfondissementIteratif(GameState etat, int n){
        while(true){
            alphabeta(etat, alpha, beta, 0, n);  //permet d'obtenir le meilleur fils à une profondeur donnée.
            n++;
        }
    }

    private int calcHeuristic(GameState etat) {

        if (etat.gameIsOver()){
            Player winner = etat.getWinner();
            if (winner == null) {
                return -5000;
            }
            else if (winner.getPlayerId() == this.getPlayerId()) {
                return maxUtility;

            }
            else {
                return minUtility;
            }
        }
        else {

            int heuristique;

            //Diminution de nombre de caisses destructibles sur la map.
            int NbCasesDestructibles = getNbCasesDestructibles(etat);
            int heurisCaisse = NbCasesDestructibles*20;
            heuristique = 5000 - heurisCaisse;

            //Augmentation de l'utilité si les 2 joueurs sont prôches
            // => OBJECTIF : Rapprocher l'IA du joueur adverse
            Player player0 = etat.getPlayers().get(0);
            Player player1 = etat.getPlayers().get(1);
            int distance = (int) (player0.getCell().distanceTo(player1.getCell()) * 50);
            heuristique -= distance;
            return heuristique;
        }
    }

    //Fonction qui permet d'obtenir le nombre de cases destructibles sur la map.
    private int getNbCasesDestructibles(GameState etat){
        int Nb = 0;
        Maze laby = etat.getMaze();
        Cell[][] cases = laby.getCells();
        for (Cell[] tC : cases){
            for(Cell c : tC){
                for(GameObject o : c.getGameObjects()){
                    if (o instanceof DestructibleWall){
                        Nb++;
                    }
                }
            }

        }
        return Nb;
    }


}
