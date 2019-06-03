package com.glhf.bomberball.ai.charlie;

import com.badlogic.gdx.Game;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import org.lwjgl.Sys;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;


/**
 * Implémentation d'un joueur artificiel qui joue de manière aléatoire
 */
public class Charlie extends AbstractAI{

    private GameState actuel;  //stockage de l'etat actuel
    private Action coup;       //stockage du meilleur coup
    private int maxprof=100;    // profondeur "maximum" valeur arbitrairement grande en réalité on choisit un temps et on explore le plus possible pendant ce temps

    private int profID;         //stockage de la profondeur max pour l'approfondissement itératif
    private int BM =0;          //stocke le nombre de bonus moves du player
    private int BR =0;          // "" bonus range
    private int BN =0;          // "" bonus number bomb
    private int AB = 0;         // stock le score de la derniere valeur renvoyée par l'alphabeta à la profondeur précédente
    private int bonusWall = 10000;

    public Charlie(GameMultiConfig config, String player_skin, int playerId) {
        super(config,player_skin,"Charlie",playerId);
    }

    /**
     * Choisit une action au hasard
     *
     * @param etat État actuel de la partie
     * @return une action
     */
    @Override
    public Action choosedAction(GameState etat) {

        GameState e = etat.clone();
        actuel = e;

        //reset des valeurs utiles à chaque nouveau coup à jouer
        AB = 0;
        bonusWall = 10000;
        BM = etat.getCurrentPlayer().bonus_moves;
        BR = etat.getCurrentPlayer().bonus_bomb_range;
        BN = etat.getCurrentPlayer().bonus_bomb_number;
        bonusWall = NBdestrubloc(etat.getMaze());

        //alphabeta avec approf itératif
        for (int i=0; i<=maxprof-1; i++){
            profID = i+1;
            int test = alphabeta(e, -1000, 1000,0);
            if (test >= AB){
                memorizedAction = coup;
                System.out.println("coup mémorisé " + memorizedAction + "  " + test);
                AB = test;
            }
        }
        return coup;
    }

    private int util(GameState e, int prof){
        Player winner=e.getWinner();
        if (winner == null){
            return 0-prof;
        }
        else if (winner.getPlayerId()==this.getPlayerId()) {
            return 500-prof;
        }
        else {
            return -500+prof;
        }
    }

    private int heuristique(GameState e, int prof){

            //affectation des player
            Player me = null, enemy = null;
            List<Player> listPlayers = e.getPlayers();
            for (Player p : listPlayers) {
                if (p.getPlayerId() == this.getPlayerId()) {
                    me = p;
                } else {
                    enemy = p;
                }
            }

            //partie heuristique liée à la distance entre les joueurs
            int dist = 0;
            int distTmp = 0;
            int meX = me.getX();
            int meY = me.getY();
            int enemyX = enemy.getX();
            int enemyY = enemy.getY();
            distTmp = distance(meX,enemyX,meY,enemyY); //distance entre le player et l'adversaire
            dist = 15 - distTmp; //plus on est près de l'adversaire meilleur est la valeur de ce coup

            //partie heuristique liée aux bonus potentiellement prenable par le player
            int valBonus = 0;
            if (me.bonus_moves > BM){
                valBonus += 70-prof;
            }
            else if (me.bonus_bomb_range > BR){
                valBonus += 50-prof;
            }
            else if (me.bonus_bomb_number > BN){
                valBonus += 30-prof;
            }

            //partie heuristique liée au nombre de murs potentiellement destructibles
            int Wall = 0;
            if (me.getNumberMoveRemaining()>2){
                Wall = 5 * (bonusWall - NBdestrubloc(e.getMaze()));
            }

            return dist+valBonus+Wall;
    }

    private int distance(int x1, int x2, int y1, int y2){
        double dist = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        return (int) Math.round(dist);
    }

    private int alphabeta(GameState e, int alpha, int beta, int prof){
        if (e.gameIsOver()){
            return util(e,prof);
        }else if (prof > profID){
            return heuristique(e, prof);
        }
        else{
            if (e.getCurrentPlayerId() == this.getPlayerId()){
                List<Action> la=e.getAllPossibleActions();
                int size = la.size();
                int j =0;
                while (j<size && alpha<beta) {
                    GameState n = e.clone();
                    n.apply(la.get(j));
                    if (e.equals(actuel)){
                        int tmp = alphabeta(n, alpha, beta, prof+1);
                        if (alpha < tmp) {
                            alpha = tmp;
                            coup = la.get(j);
                        }
                    }
                    else {
                        alpha = max (alpha, alphabeta(n,alpha, beta,prof+1));
                    }
                    j++;
                }
                return alpha;
            }
            else{
                List<Action> la=e.getAllPossibleActions();
                int size = la.size();
                int j =0;
                while (j<size && alpha<beta) {
                    GameState n = e.clone();
                    n.apply(la.get(j));
                    beta = min(alphabeta(n, alpha, beta, prof+1),beta);
                    j++;
                }
                return beta;
            }
        }
    }

    public int NBdestrubloc (Maze maze) {
        int nb = 0;
        Cell[][] cells= maze.getCells();
        for (Cell[] cL: cells) {
            for (Cell c: cL) {
                List<GameObject> objects = c.getGameObjects();
                for (GameObject o: objects) {
                    if (o instanceof DestructibleWall){
                        nb = nb + 1;
                    }
                }
            }
        }
        return nb;
    }
}