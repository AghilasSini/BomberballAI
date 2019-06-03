package com.glhf.bomberball.ai.hotel;

import com.badlogic.gdx.Game;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConvDescript {

    private GameState state;

    public ConvDescript(GameState state){
        this.state = state;
    }

    /**
     * Gives the number of ennemies alive, it produces a score that describes the state
     * @return
     */
    public int nbEnemiesAlive(){
        return state.getPlayers().size()-1;
    }


    public int distance_joueur(){
        Maze maze= state.getMaze();
        Cell tab[][]=maze.getCells();
        LinkedList<Cell> joueurs=new LinkedList<Cell>();
        Player actuel= state.getCurrentPlayer();
        for(int i=0;i<tab.length;i++){
            for(int j=0;j<tab[0].length;j++){
                ArrayList<GameObject> gameObject=tab[i][j].getGameObjects();
                for(GameObject object: gameObject){
                    if(object instanceof Player){
                        joueurs.add(tab[i][j]);
                    }
                }
            }
        }
        Cell current_player=null;
        for(int i=0;i<joueurs.size();i++){
            ArrayList<GameObject> gameObject=joueurs.get(i).getGameObjects();
            for(GameObject object: gameObject){
                if(object instanceof Player){
                    if(object.equals(actuel)){
                        current_player=joueurs.get(i);
                    }
                }
            }
        }
        LinkedList<Integer> distance=new LinkedList<Integer>();
        joueurs.remove(current_player);
        for (Cell c: joueurs){
            distance.add(Math.abs(c.getX()-current_player.getX())+Math.abs(c.getY()-current_player.getY()));
        }
        int min=999999999;
        for(Integer integer: distance){
            if(integer<min){
                min=integer;
            }
        }
        return min;
    }

    /**
     * Gives the minimum Manhattan between the current player and a bonus or a wallbonus
     * @return
     */
    public int distBonus(){
        Player currentplayer = state.getCurrentPlayer();
        Maze maze = state.getMaze();
        Cell[][] tabcells = maze.getCells();
        ArrayList<GameObject> listObjectsOnCurrentCell;
        GameObject objectOnCell;
        int distTmp;
        int dist= tabcells.length+tabcells[0].length;
        for(int i=0;i<tabcells.length;i++){
            for(int j=0;j<tabcells[0].length;j++){
                listObjectsOnCurrentCell = tabcells[i][j].getGameObjects();
                for(GameObject object: listObjectsOnCurrentCell){
                    if(object instanceof Bonus || object instanceof BonusWall){
                        distTmp = manhattan(object,currentplayer);
                        if(distTmp<dist){
                            dist = distTmp;
                        }
                    }
                }
            }
        }
        return dist;
    }

    /**
     * give the diffrence between the number of bonus that owns the current player and that quantity for the other players
     * @return
     */
    public int scoreSpec(){
        List<Player> players = state.getPlayers();
        int currentBonusScore = 0;
        int otherBonus = 0;
        for(int i = 0; i < players.size(); i++){
            if( i == state.getCurrentPlayerId()){
                currentBonusScore += bonusScore(players.get(i));
            }
            else{
                otherBonus += bonusScore(players.get(i));
            }
        }
        return currentBonusScore-otherBonus;
    }

    /**
     * gives the sum of the bonus that the player owns
     * @param p
     * @return
     */
    public int bonusScore(Player p){
        int score = p.getBombRange() + p.getNumberBombRemaining() + p.getNumberMoveRemaining();
        return score;
    }

    /**
     * returns how many ennemy the current player is not safe from
     * @return
     */
    public int isSafe(){
        int nbEnnemies = 0;
        List<Player> players = state.getPlayers();
        Player currentPlayer = state.getCurrentPlayer();
        for(Player p : players){
            if(!p.equals(currentPlayer)) {
                nbEnnemies += currentPlayerReachable(state.getCurrentPlayer(), p);
                //nbEnnemies += manhattan(currentPlayer, p); //naive version
            }
        }
        return nbEnnemies;
    }

    /**
     * this methods returns -1 if the current player is not safe from the considered enemy and 1 otherwise
     * @param o1
     * @param o2
     * @return
     */
    public int currentPlayerReachable(Player o1, Player o2){
        int range = o2.getNumberMoveRemaining()+o2.getBombRange();
        ArrayList<Cell> reachableCells = new ArrayList<Cell>();
        reachableCells = getReacheableCellsInRange(o2.getCell(), range);
        // the current player is not safe from the enemy considered
        if(reachableCells.contains(o1.getCell())){
            return -1;
        }
        // the current player is safe from the enemy considered
        else {
            return 1;
        }
    }

    /**
     * that methods give all the cells reachable from the origin within the range given
     * @param cell_origin
     * @param range
     * @return
     */
    public static ArrayList<Cell> getReacheableCellsInRange(Cell cell_origin, int range) {
        ArrayList<Cell> cells = new ArrayList<>();
        LinkedList<Cell> active_queue = new LinkedList<>();
        LinkedList<Cell> inactive_queue = new LinkedList<>();
        int depth = 0;
        cells.add(cell_origin);
        active_queue.add(cell_origin);
        // Invariant : Distance to all cells in the active queue is depth
        while (depth < range) {
            while (!active_queue.isEmpty()) {
                Cell c = active_queue.poll();
                for (Cell other : c.getAdjacentCells()) {
                    if(other!=null){
                        if (!cells.contains(other) && other.isWalkable()) {
                            inactive_queue.add(other);
                            cells.add(other);
                        }
                    }
                }
            }
            depth++;

            active_queue = inactive_queue;
            inactive_queue = new LinkedList<>();
        }
        return cells;
    }

    /**
     * returns 1 if the currentplayer can place a bomb to kill an enemy and return to safety, 0 otherwise
     * @return
     */
    public int canPoseBombAndStillBeSafe() {
        Player currentPlayer = state.getCurrentPlayer();
        List<Player> players = state.getPlayers();
        Maze maze = state.getMaze();
        Cell[][] tabcells = maze.getCells();
        for (Player p : players) {
            if (!p.equals(currentPlayer)) {
                if (canBombThisEnemyAndRunSafe(currentPlayer,p)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * returns true if the player p1 can bomb the player p2 and run back to safety, false otherwise
     * @returns
     */
    public boolean canBombThisEnemyAndRunSafe(Player p1, Player p2){
        Maze maze = state.getMaze();
        Cell[][] tabcells = maze.getCells();
        //Cells in range of p1 corresponds to the cells p1 can put a bomb on associated with the number of moves needed
        //to go to the cell where p1 would be when putting the bomb
        ArrayList<Journey> journeysinrangeofp1 = new ArrayList<Journey>();
        //Available moves to flee represents how many moves p1 would still have to run away from its bomb
        int availablemovestoflee;
        //Available cells to flee represents the cells p1 can run to after putting the bomb
        List<Cell> availablecellstoflee = new ArrayList<>();
        List<Cell> bombingavailableslots = new ArrayList<Cell>();
        //Bombing available slots corresponds to the cells where a bomb could be placed to kill p2
        for(int i = 0; i<=p1.getBombRange();i++){
            if((p2.getX()-i)>=0){
                bombingavailableslots.add(tabcells[p2.getX()-i][p2.getY()]);
            }
            if((p2.getX()+i)<maze.getWidth()){
                bombingavailableslots.add(tabcells[p2.getX()+i][p2.getY()]);
            }
            if((p2.getY()-i)>=0){
                bombingavailableslots.add(tabcells[p2.getX()][p2.getY()-i]);
            }
            if((p2.getY()+i)<maze.getHeight()){
                bombingavailableslots.add(tabcells[p2.getX()][p2.getY()+i]);
            }
        }
        journeysinrangeofp1 = getReacheableCellsInRangeWithPath(tabcells[p1.getX()][p1.getY()],p1.getNumberMoveRemaining()+1);
        for(Journey j:journeysinrangeofp1){
            if(bombingavailableslots.contains(j.getDestinationcell())){
                availablemovestoflee = p1.getNumberMoveRemaining() - j.getNbmoves();
                availablecellstoflee = getReacheableCellsInRange(j.getDestinationcell(), availablemovestoflee);
                for(Cell c: availablecellstoflee){
                    if(wouldBeSafeFromBomb(p1,j.getDestinationcell(),c)){
                        if(isSafeFromAllPlayersExcept(c,p1,p2)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * returns true if a player would be safe on the cell refugecell from the bomb
     * that playerposingbomb put on cell cellbomb, false otherwise
     * @returns
     */
    public boolean wouldBeSafeFromBomb(Player playerposingbomb, Cell cellbomb, Cell refugecell){
        Maze maze = state.getMaze();
        Cell[][] tabcells = maze.getCells();
        int rangebomb = playerposingbomb.getBombRange();
        int x = cellbomb.getX();
        int y = cellbomb.getY();
        int i = 0;
        for(i=0;i<=rangebomb;i++){
            if((x-i>=0)){
                if(tabcells[x-i][y].equals(refugecell)){
                    return false;
                }
            }
            if((x+i)<maze.getWidth()){
                if(tabcells[x+i][y].equals(refugecell)){
                    return false;
                }
            }
            if((y-i)>=0){
                if(tabcells[x][y-i].equals(refugecell)){
                    return false;
                }
            }
            if((y+i)<maze.getHeight()) {
                if(tabcells[x][y+i].equals(refugecell)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * returns true if a player p1 would be safe in cell refugecell from all players except from the player p2,
     * false otherwise
     * @returns
     */
    public boolean isSafeFromAllPlayersExcept(Cell refugecell, Player p1, Player p2){
        ArrayList<Cell> reachableCells = new ArrayList<Cell>();
        int range;
        List<Player> players = state.getPlayers();
        for(Player p: players){
            if(!p.equals(p1)&&!p.equals(p2)){
                range = p.getNumberMoveRemaining()+p.getBombRange();
                reachableCells = getReacheableCellsInRange(p.getCell(), range);
                // the current player would not be safe from p
                if(reachableCells.contains(refugecell)){
                    return false;
                }
            }
        }
        // the current player would be safe on refugecell from all enemies except from p2
        return true;
    }

    /**
     * returns the Manhattan distance between two Gameobjects
     * @returns
     */
    public int manhattan(GameObject o1, GameObject o2){
        return Math.abs(o1.getX()-o2.getX()) + Math.abs(o1.getY()-o2.getY());
    }


    /**
     * that methods give all the cells reachable from the origin within the range given, attached with the number of
     * moves needed to go there
     * @param cell_origin
     * @param range
     * @return Journey ArrayList (a Journey object contains a destination cell and a number of moves to go there)
     */
    public static ArrayList<Journey> getReacheableCellsInRangeWithPath(Cell cell_origin, int range) {
        ArrayList<Journey> journeys = new ArrayList<>();
        ArrayList<Cell> cells = new ArrayList<>();
        LinkedList<Cell> active_queue = new LinkedList<>();
        LinkedList<Cell> inactive_queue = new LinkedList<>();
        int depth = 0;
        journeys.add(new Journey(depth,cell_origin));
        cells.add(cell_origin);
        active_queue.add(cell_origin);
        // Invariant : Distance to all cells in the active queue is depth
        while (depth < range) {
            while (!active_queue.isEmpty()) {
                Cell c = active_queue.poll();
                for (Cell other : c.getAdjacentCells()) {
                    if(other!=null){
                        if (!cells.contains(other) && other.isWalkable()) {
                            inactive_queue.add(other);
                            cells.add(other);
                            journeys.add(new Journey(depth-1,other));
                        }
                    }
                }
            }
            depth++;

            active_queue = inactive_queue;
            inactive_queue = new LinkedList<>();
        }
        return journeys;
    }
}

