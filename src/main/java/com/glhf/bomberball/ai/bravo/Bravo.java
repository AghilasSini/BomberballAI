package com.glhf.bomberball.ai.bravo;

import java.util.*;

import static com.glhf.bomberball.utils.Constants.MAX_DEPTH;
import static java.lang.Thread.sleep;
import static sun.audio.AudioPlayer.player;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.Node;
import org.lwjgl.Sys;

public class Bravo extends AbstractAI {
    public double alpha;
    public double beta;
    public int n;
    public int actual_move;
    public ArrayList<Directions> way;
    //public List<Cell> visite = new ArrayList<>();
    public Bravo(GameConfig config,String player_skin,int playerId) {
        super(config,player_skin,"Bravo",playerId);
        alpha=Double.NEGATIVE_INFINITY;
        beta=Double.POSITIVE_INFINITY;
        n=-1;
    }






    public Action choosedAction(GameState etat){

        List<Action> actionsPossibles = etat.getAllPossibleActions();
        int indiceAction=0;
        double res ;
        double MaxCur = 0 ;
        List<Cell> lc = new ArrayList<>();
        double OldMax=Double.NEGATIVE_INFINITY;

        for (int prof=1;prof<150;prof++) {
            //System.out.println("nouvelle boucle");
            MaxCur=Double.NEGATIVE_INFINITY;
            indiceAction=0;
            for (int j = 0; j < actionsPossibles.size(); j++) {
                //System.out.println(j);
                GameState tmp = etat.clone();
                tmp.apply(actionsPossibles.get(j));
                res = /*iterative_deepening(20,tmp,alpha,beta);*/alphabeta(tmp, alpha, beta, prof);
                //System.out.println("res :"+res);
                if (res == Double.POSITIVE_INFINITY) {
                    indiceAction = j;
                    this.setMemorizedAction(actionsPossibles.get(j));
                    j = actionsPossibles.size();
                    MaxCur = res;
                    //System.out.println("on a eu un 100");
                } else if (res > MaxCur) {

                    indiceAction = j;
                    MaxCur = res;
                    //System.out.println("res :"+res+"maxcur :"+MaxCur);

                }

            }
            //System.out.println(prof);
            //System.out.println(MaxCur);
            //System.out.println("---------");

            if(!(this.moves_remaining == this.initial_moves+this.bonus_moves && actionsPossibles.get(indiceAction) == Action.ENDTURN)) {
                if (OldMax <= MaxCur) {
                    System.out.println("oldmax :"+OldMax+"maxcur :"+MaxCur);
                    OldMax = MaxCur;
                    this.setMemorizedAction(actionsPossibles.get(indiceAction));
                }//lc = getPlayersPosition(etat);
                //System.out.println("pos :"+lc.toString());
            }
        }

        System.out.println("res :"+MaxCur);
        //System.out.println(actionsPossibles.get(indiceAction));
        //visite.add(this.getNotreIAPosition(etat));

        return actionsPossibles.get(indiceAction); //(vaut 100, 0 ou -100 -> pas un indice)
    }


    public double utilite ( GameState etat){
        Player winner=etat.getWinner();
        if (winner==null){
           // if (etat.gameIsOver()){
                return 0;
            //}else{
               // return heur(etat);
            //}
        }else if (/*winner instanceof AbstractAI && */winner.getPlayerId()==this.getPlayerId()){
            return Double.POSITIVE_INFINITY;
        }else{
            return Double.NEGATIVE_INFINITY;
        }
        //return 0;
    }



    public double alphabeta(GameState etat,double alpha,double beta,int prof) {
        if (etat.gameIsOver()){
            return utilite(etat);
        }
        else if(prof == 0){

            return heuristique(etat);
        }
        else if (etat.getCurrentPlayerId() == this.getPlayerId()) {
            List<Action> E=etat.getAllPossibleActions();
            for (int j=0; j < E.size(); j++) {
                GameState tmp = etat.clone();
                tmp.apply(E.get(j));

                //System.out.println("Alpha vaut : "+alpha+" ; pré-modif.");
                alpha = Math.max(alpha, alphabeta(tmp, alpha, beta,prof-1));
                //System.out.println("Alpha vaut : "+alpha+" ; post-modif.");
                if(alpha >= beta){ j = E.size();}
            }
            return alpha;
        }
        else/*(n.getIdJoueurCourant() == 1)*/{
            List<Action> E=etat.getAllPossibleActions();
            for (int j = 0; j < E.size(); j++) {
                GameState tmp = etat.clone();
                tmp.apply(E.get(j));

                //System.out.println("Beta vaut : "+beta+" ; pré-modif.");
                beta = Math.min(beta, alphabeta(tmp, alpha, beta,prof-1));
                //System.out.println("Beta vaut : "+beta+" ; post-modif.");
                if(alpha>=beta){j = E.size();}
            }
            return beta;
        }

    }


    public int heuristique(GameState etat){
        int h=0;
        GameState tmp = etat.clone();

        for(int i = 0; i < etat.getMaze().getWidth(); i++){
            for(int j = 0 ; j < etat.getMaze().getHeight(); j++){
                
                if (etat.getMaze().getCellAt(i,j).hasInstanceOf(Bomb.class)){// si l'ia peut casser un bloc, c'est mieux
                    tmp.endTurn();
     
                    if ((nbBloc(tmp) < nbBloc(etat)) && (tmp.getWinner() == null)){
                        h += 100*(nbBloc(etat) - nbBloc(tmp));
                        
                    }
                }
           
            
                if(etat.getMaze().getCellAt(i,j).hasInstanceOf(Player.class)){

                    tmp.endTurn();
                    tmp.endTurn();
                 
                    if(this.moves_remaining == 0){
                
                        h += this.initial_moves+this.bonus_moves-this.moves_remaining;
                    }
                }
                /*if(etat.getMaze().getCellAt(i,j).hasInstanceOf(Player.class)){ // l'ia essaie d'aller chercher des bonus (ne fonctionne pas
                    tmp.endTurn();
                    Player test = tmp.getPlayers().get(0);
                    if(this.getPlayerId() == test.getPlayerId()){
                        System.out.println("ok");
                    }else{
                        test = tmp.getPlayers().get(1);
                    }
                    if(etat.getCurrentPlayer().bonus_moves+etat.getCurrentPlayer().bonus_bomb_range+etat.getCurrentPlayer().bonus_bomb_number < test.bonus_bomb_range+test.bonus_moves+test.bonus_bomb_number){
                        h+=500*((test.bonus_bomb_range+test.bonus_moves+test.bonus_bomb_number)-(etat.getCurrentPlayer().bonus_moves+etat.getCurrentPlayer().bonus_bomb_range+etat.getCurrentPlayer().bonus_bomb_number ));
                    }
                }*/
                /*if(etat.getMaze().getCellAt(i,j).hasInstanceOf(Player.class)){ // l'ia essaie de se rapprocher de l'ennemi (ne fonctionne pas)
                   tmp.endTurn();
                   Player notreIA = tmp.getPlayers().get(0);
                   Player ennemy = tmp.getPlayers().get(1);
                   if(nbBloc(tmp) == nbBloc(etat)){
                       if((notreIA.getX() - ennemy.getX() > 0) || (notreIA.getY() - ennemy.getY() > 0)){
                           h+= 100;
                       }
                   }
                }*/




            }
        }

        return h;
    }
    public int nbBloc(GameState etat){
        int k = 0;
        for(int i = 0; i < etat.getMaze().getWidth();i++){
            for(int j = 0 ; j < etat.getMaze().getHeight(); j++ ){
                if(etat.getMaze().getCellAt(i,j).hasInstanceOf(DestructibleWall.class) || etat.getMaze().getCellAt(i,j).hasInstanceOf(BonusWall.class)){
                    k++;
                }
            }
        }return k;
    }



}
