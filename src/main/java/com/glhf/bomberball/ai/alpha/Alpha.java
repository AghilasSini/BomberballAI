package com.glhf.bomberball.ai.alpha;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.gameobject.Wall;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;


import org.lwjgl.Sys;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.List;
import java.util.Random;


/**
 * Implémentation d'un joueur artificiel qui joue de manière aléatoire
 */
public class Alpha extends AbstractAI{


    int alpha=-1000;

    int beta=1000;

    int maxUtility= 10000;

    int minUtility=-10000;


    public Alpha(GameConfig config,String player_skin,int playerId) {
        super(config,"wizzard_m","Alpha",playerId);
    }


    public Action choosedAction(GameState etat) {
        int gain_max=-1000;
        Action Action_play=null;
        for (int j = 0; j < etat.getAllPossibleActions().size(); j++) {
            GameState etat_temp = etat.clone();
            etat_temp.apply(etat.getAllPossibleActions().get(j));
            int gain = alphabeta(etat_temp, alpha, beta,etat.getCurrentPlayerId(),5);
            if (gain > gain_max) {
                gain_max = gain;
                Action_play = etat.getAllPossibleActions().get(j);
                this.setMemorizedAction(Action_play);
            }
        }
        return this.memorizedAction;
    }

    /**
     *
     *
     * @param etat État actuel de la partie
     * @return le gain
     */

    public int alphabeta(GameState etat,int alpha,int beta,int id,int profondeur) {

        if (etat.getAllPossibleActions().isEmpty()) {
            return utilite(etat, profondeur);
        }
        if (!etat.gameIsOver()){
            System.out.println("TEST; "+etat.getAllPossibleActions().toString());
            if (profondeur > 0) {
                System.out.println("Profondeur : "+profondeur);
                int j = 0;
                if (etat.getCurrentPlayerId() == id) {
                    while (j < etat.getAllPossibleActions().size() && alpha < beta) {
                        GameState etat_temp = etat.clone();
                        etat_temp.apply(etat.getAllPossibleActions().get(j));
                        alpha = Math.max(alpha, alphabeta(etat_temp, alpha, beta, id, profondeur - 1));
                        j++;
                    }
                    return alpha;
                } else {
                    while (j < etat.getAllPossibleActions().size() && alpha < beta) {
                        GameState etat_temp = etat.clone();
                        etat_temp.apply(etat.getAllPossibleActions().get(j));
                        beta = Math.min(beta, alphabeta(etat_temp, alpha, beta, id, profondeur - 1));
                        j++;
                    }
                    return beta;
                }

            } else {
                System.out.println("Profondeur = 0");
                return utilite(etat, profondeur);
                //return heuristique(etat, id);
            }
        } else {
            return utilite(etat, profondeur);
        }
    }

    private int distance(Cell cell1, Cell cell2){
        return Math.abs((cell2.getX()-cell1.getX())+(cell2.getY()-cell1.getY()));
    }

    private int nbWalls(GameState state){
        Maze maze = state.getMaze();
        int largeur = maze.getWidth();
        int longueur = maze.getHeight();
        int nbWalls = 0;
        int i,j;
        for(i = 0; i < largeur; i++){
            for(j = 0; j < longueur; j++){
                if(maze.getCellAt(i,j).hasInstanceOf(Wall.class)){
                    nbWalls++;
                }
            }
        }
        return nbWalls-(largeur*2+longueur*2);
    }


    private int utilite(GameState state, int profondeur) {
        Player winner = state.getWinner();
        System.out.println("Winner :"+winner);
        if (state.gameIsOver() && winner == null) {
            System.out.println("Egalité suicide");
            return minUtility/2;
        }
        else if(!state.gameIsOver() && winner == null){
            System.out.println("Mon IA nettoie le plateau");
            return -distance(state.getCurrentPlayer().getCell(), state.getPlayers().get((state.getCurrentPlayerId()+1)%2).getCell())-nbWalls(state)*5;
        }

        else if ((winner).getPlayerId() == this.getPlayerId()) {
            System.out.println("Mon IA est gagnante, utilité : "+(maxUtility+profondeur));
            return maxUtility - profondeur;
        }
        else {
            System.out.println("Mon IA est perdante, utilité : "+(minUtility-profondeur));
            return minUtility + profondeur;
        }
    }




}


