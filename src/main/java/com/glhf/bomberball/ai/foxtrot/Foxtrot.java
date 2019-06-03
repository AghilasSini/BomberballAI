package com.glhf.bomberball.ai.foxtrot;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.gameobject.Wall;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.*;


public class Foxtrot extends AbstractAI {

    private boolean firstRound = true;
    private int maxProfAbs = 100;
    private int maxProf = 0;
    private GameState etatAct;
    private Action bestAction;
    private boolean endDijkstra = false;


    public Foxtrot(GameConfig config, String player_skin, int playerId) {
        super(config, player_skin, "Foxtrot", playerId);
    }

    @Override
    public Action choosedAction(GameState state) {
        firstRound = true;
        etatAct = state;
        /*Boucle pour l'approfondissement itératif*/
        for (int i=1;i<this.maxProfAbs;i++) {
            this.maxProf = i;
            double res = rechercheSolution(state,-1000,1000,0);
            if (res==1) {
                i = this.maxProfAbs;
            }
            this.memorizedAction = this.bestAction;
        }
        System.out.println("test"+this.memorizedAction);
        return this.bestAction;
        //System.out.println("Action : "+bestAction.toString());
        //return bestAction;
    }


    /**
     * AlphaBeta
     */
    public double rechercheSolution(GameState etat,double a,double b,int prof) {
        if (etat.gameIsOver()) { //n est terminal aka si n mène à une fin
            Player winner = etat.getWinner();
            if (winner == null) {
                return 0;
            }
            else if (winner.getPlayerId() == this.getPlayerId()) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (prof>=maxProf) {
            double heuristique = heuristique(etat);
            return heuristique;
        }
        else if (etat.getCurrentPlayerId()==this.getPlayerId()) { //n est de type Max then
            //soient {f1,....,fk} les fils de n
            List<Action> actions = etat.getAllPossibleActions();
            int j = 0;
            int k = actions.size();
            while(j<k&&a<b) {
                GameState newEtat = etat.clone();
                newEtat.apply(actions.get(j));

                if (etat.equals(etatAct)) {
                    double result = rechercheSolution(newEtat,a,b,prof+1);
                    if (a<result){
                        a = result;
                        bestAction = actions.get(j);
                    }
                }
                else {
                    a = Math.max(a,rechercheSolution(newEtat,a,b,prof+1));
                }
                j++;

            }
            return a;
        }
        else { //n est de type Min then
            List<Action> actions = etat.getAllPossibleActions();
            int k = actions.size();
            int j = 0;
            while(j<k&&a<b) {
                GameState newEtat = etat.clone();
                newEtat.apply(actions.get(j));
                b = Math.min(b,rechercheSolution(newEtat,a,b,prof+1));
                j++;
            }
            return b;
        }
    }

    /**
     * L'heuristique est composée de deux fonctions, une qui prend en compte la distance et une seconde qui tient compte du nombre d'actions possibles
     */
    private double heuristique(GameState state) {
        return heuristiqueComposite(state);
    }

    /**
     * Heurisique qui juge que si l'IA se rapproche, alors le coût est meilleur
     */
    private double heuristiqueSimple(GameState state) {
        Player p = state.getCurrentPlayer();
        Player pEnemy;
        int x = p.getX();
        int y = p.getY();
        double sum_distance = 10000;
        List<Player> playersList = state.getPlayers();
        Iterator<Player> it = playersList.iterator();
        while(it.hasNext()) {
            pEnemy = it.next();

            if (pEnemy.getPlayerId()!=p.getPlayerId()) {
                int xE = pEnemy.getX();
                int yE = pEnemy.getY();
                //sum_distance = distance_case_a(state,x,y,xE,yE);
                sum_distance = Math.min(sum_distance,Math.sqrt(Math.pow(xE-x,2)+Math.pow(yE-y,2)));
            }
        }
        Maze m = state.getMaze();
        double diagMaze = Math.sqrt(m.getHeight()*m.getHeight()+m.getWidth()*m.getWidth());
        double val = (sum_distance/(playersList.size()-1));
        return (diagMaze-val)/diagMaze;
    }

    /**
     * Heuristique qui prend en compte le nombre d'actions possibles, plus ce nombre est élevé, plus l'heuristique est grande
     */
    private double heuristiqueAction(GameState state) {
        double val = ((double) state.getAllPossibleActions().size())/17; //17= nb max actions possibles
        return val;
    }
    private double heuristiqueComposite(GameState state) {
        return (heuristiqueSimple(state)*10+heuristiqueAction(state))/11;
    }
    private int distance_case_a(GameState state,int x1,int y1,int x2, int y2) {
        Maze m = state.getMaze();
        Set<AStarCell> closedSet = new HashSet<>();
        SortedSet<AStarCell> openSet = new TreeSet<>();
        openSet.add(new AStarCell(x1,y1,0,distance_direct(x1,y1,x2,y2),null));
        while (!openSet.isEmpty()) {
            AStarCell u = openSet.first();
            openSet.remove(u);
            if (u.getX()==x2&&u.getY()==y2) {
                return u.getCout();
            }
            else {
                Cell c = m.getCellAt(u.getX(),u.getY());
                Iterator<Cell> listC = c.getAdjacentCells().iterator();
                Cell newC;
                while(listC.hasNext()) {
                    newC = listC.next();
                    if (newC!=null) {
                        addNode(newC,x2,y2,openSet,closedSet,u);
                    }
                }
            }
            closedSet.add(u);
        }
        return -1;
    }

    private void addNode(Cell c, int x2, int y2, SortedSet<AStarCell> openSet, Set<AStarCell> closedSet, AStarCell u) {
        int x = c.getX();
        int y = c.getY();
        boolean test = false;
            int cout = u.getCout()+1;
            double heuristique = cout + distance_direct(x,y,x2,y2);
            AStarCell n = new AStarCell(x,y,cout,heuristique,u);
            ArrayList<GameObject> objects = c.getGameObjects();
            for (int i = 0; i<objects.size();i++) {
                if (objects.get(i) instanceof Wall) {
                    test = true;
                }
            }
            if (test) {
                if (!closedSet.contains(n)) {
                    if (!openSet.headSet(n).contains(n)) {
                        openSet.add(n);
                    }
                }
            }

    }

    private double distance_direct(int x1,int y1,int x2, int y2) {
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }
    public void joueurSuivant(GameState etat) {

    }
}
