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


/** Classe IA, version 4 - NON FONCTIONNEL */
/* Pour le jeu BomberBall */

/* Implémentation de l'algorithme alpha-béta en version itérative et heuristique améliorée*/
/* Ajout de coefficients pour un contrôle plus simple de l'heuristique*/

public class AlphaBetaJuliett extends AbstractAI {

    private final int maxUtility = 100000;
    private final int minUtility = -100000;
    private final int egalite = -50000;
    private final int alpha = -100000;
    private final int beta = 100000;

    private int meilleurFils;

    private boolean isFirstRun1 = true;
    private boolean isFirstRun2 = true;
    private int nbCasesDestructiblesDebut;
    private double distanceDebut;

    public AlphaBetaJuliett(GameConfig config, String player_skin, int playerId) {
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
                return egalite;
            }
            else if (winner.getPlayerId() == this.getPlayerId()) {
                System.out.println("--GAGNE : " + maxUtility);
                return maxUtility;

            }
            else {
                System.out.println("--PERDU : " + minUtility);
                return minUtility;
            }
        }
        else {

            int heuristique;
            double coefCaisses = 1;
            double coefDistance = 0.5;

            /* PARAMETRE 1 : Nb de caisses destructibles */
            int heurisCaisse = (maxUtility/2) -  ((maxUtility/2) * getNbCasesDestructibles(etat)) / nbCasesDestructiblesDebut;
            System.out.println("---------------heurisCaisse = "+ heurisCaisse);

            /* PARAMETRE 2 : Distance entre les 2 joueurs */
            Player player0 = etat.getPlayers().get(0);
            Player player1 = etat.getPlayers().get(1);
            if(isFirstRun2){
                distanceDebut = player0.getCell().distanceTo(player1.getCell());
                isFirstRun2 = false;
            }
            int heurisDistance =  (maxUtility/2) - (int) (( (maxUtility/2) * player0.getCell().distanceTo(player1.getCell()) ) / distanceDebut);
            System.out.println("---------------heurisDistance = "+ heurisDistance);

            /* RESULTAT : Heuristique finale */
            heuristique = (int) (heurisCaisse*coefCaisses + heurisDistance*coefDistance);

            System.out.println("===============HEURISTIQUE = " + heuristique);

            return heuristique;
        }
    }

    //Méthode qui permet d'obtenir le nombre de cases destructibles sur la map.
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

        if(isFirstRun1){
            nbCasesDestructiblesDebut = Nb;
            isFirstRun1 = false;
        }

        return Nb;
    }


}
