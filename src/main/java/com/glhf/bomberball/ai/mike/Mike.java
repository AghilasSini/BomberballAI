package com.glhf.bomberball.ai.mike;

import com.glhf.bomberball.gameobject.DestructibleWall;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import java.util.*;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;

import static com.glhf.bomberball.utils.Action.*;
import static java.lang.Math.abs;

public class Mike extends AbstractAI {

    int alpha = -1000;
    int beta = 1000;
    private int distance;

    public Mike(GameConfig config, String player_skin, int playerId) {
        super(config, "wizzard_m", "Mike", playerId);
        this.distance = 0;
    }


    public Action choosedAction(GameState etat)  {
        float maximum = -1000;
        Action jouer = null;

        distance = distManhattan(etat);
        for (int i = 0; i < etat.getAllPossibleActions().size(); i++) {
            GameState tempo = etat.clone();
            tempo.apply(etat.getAllPossibleActions().get(i));
            float gain = alphabetaiteratif(tempo, tempo.getCurrentPlayerId(), alpha, beta, 5);
            System.out.println(gain + "  " + maximum);
            if (gain > maximum) {
                maximum = gain;
                jouer = etat.getAllPossibleActions().get(i);
                //setMemorizedAction(jouer);
            }
        }
        if (getMemorizedAction() != null) {
            return getMemorizedAction();
        } else {
            return jouer;
        }
    }

    public float alphabetaiteratif(GameState etat, int id, float alpha, float beta, int profondeur) {
        if (etat.gameIsOver()) {
            return utilite_mort(etat, etat.getCurrentPlayerId());
        }
        if (profondeur == 0) {
            return utilite(etat, etat.getCurrentPlayerId());
        }
        List<Action> actions = etat.getAllPossibleActions();
        Iterator<Action> bouclea = actions.iterator();
        if (etat.getCurrentPlayerId() == id) {
            while (bouclea.hasNext() && alpha < beta) {
                GameState coup = etat.clone();
                coup.apply(bouclea.next());
                float alphatemp = Math.max(alpha, alphabetaiteratif(coup, id, alpha, beta, profondeur - 1));
                if (alphatemp > alpha) {
                    alpha = alphatemp;
                }
            }
            return alpha;
        } else {
            while (bouclea.hasNext() && alpha < beta) {
                GameState coup = etat.clone();
                coup.apply(bouclea.next());
                beta = Math.min(beta, alphabetaiteratif(coup, id, alpha, beta, profondeur - 1));
            }
            return beta;
        }
    }

    private int getNbCasesDestructibles(GameState etat) { //Permet d'obtenir le nombre de blocs destructibles sur la carte
        int Nb = 0;
        Maze maze = etat.getMaze();
        Cell[][] cases = maze.getCells();
        for (Cell[] tC : cases) {
            for (Cell c : tC) {
                for (GameObject o : c.getGameObjects()) {
                    if (o instanceof DestructibleWall) {
                        Nb++;
                    }
                }
            }
        }
        return Nb;
    }

    public int distManhattan(GameState gameState) {  //distanceTo de la classe Cell
        Maze maze = gameState.getMaze();
        ArrayList<Player> playerArrayList = (ArrayList<Player>) maze.getPlayers();
        Player p1 = playerArrayList.get(0);
        Player p2 = playerArrayList.get(1);
        Cell cellP1 = p1.getCell();
        Cell cellP2 = p2.getCell();
        return abs(cellP1.getX() - cellP2.getX()) + abs(cellP1.getY() - cellP2.getY());
    }

    public int utilite_mort(GameState gameState, int id) {
        Player winner = gameState.getWinner();
        if (winner == null) {
            return 0;
        } else if (winner.getPlayerId() == this.getPlayerId()) {
            return 10000;
        } else {
            return -100000;
        }
    }

    public int utilite(GameState gameState, int id) {
        int heuristique = 1500;
        //Blocs destructibles
        int NbCasesDestructibles = getNbCasesDestructibles(gameState);
        int distanceTemp = distManhattan(gameState);
        heuristique -= NbCasesDestructibles * 10;

        //Distance entre joueurs
        if(this.distance > distanceTemp) {
            heuristique += heuristique * (distance-distanceTemp);
        }
        return heuristique;
    }
}
