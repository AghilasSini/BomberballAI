/**
 * Classe implémentant une intelligence artificielle utilisant l'algorithme alpha-beta couplé à une heuristique
 */
package com.glhf.bomberball.ai.novembre;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;

import java.util.ArrayList;
import java.util.List;

public class November extends AbstractAI {

    private int Alpha;
    private int Beta;
    private List<Action> actionsPossibles;
    private MyArrayList<Action> actionsAEffectuer;
    private MyArrayList<MyArrayList<Action>> listeActionsPossibles;
    private boolean rechercheEffectuee=false;

    private double beginOfGameCoordinatesUsX;
    private double beginOfGameCoordinatesUsY;
    private double beginOfGameCoordinatesHimX;
    private double beginOfGameCoordinatesHimY;

    private double lastPositionStartTurnUsX;
    private double lastPositionStartTurnUsY;
    private double lastPositionStartTurnHimX;
    private double lastPositionStartTurnHimY;

    private boolean beginOfGameInitialized=false;

    private double beginOfTurnCoordinatesUsX;
    private double beginOfTurnCoordinatesUsY;
    private double beginOfTurnCoordinatesHimX;
    private double beginOfTurnCoordinatesHimY;

    private double distanceCoveredThisGameUs=0;
    private double distanceCoveredThisGameHim=0;

    private double closestDistanceEnnemyYet;

    private double maxDistanceOnMap;
    private int nombreToursMaximal;

    private GameState dummyState;
    private  boolean dummyStateInitialized=false;
    private final int maxProfondeur = 10;

    private double finalscore=-1;

    public November(GameConfig config, String player_skin, int playerId) {

        super(config,player_skin,"November",playerId);
        this.Alpha = -2147483646;
        this.Beta = 2147483646;
        this.actionsAEffectuer = new MyArrayList<>();
    }

    /**
     * Permet de récupérer l'action mémorisée et d'arreter la recherche
     * @return Action :  l'action mémorisée
     */
    @Override
    public Action getMemorizedAction(){
        rechercheEffectuee=false;
        return memorizedAction;
    }


    /**
     * @param gameState :  l'état courant de la partie
     * @return Action : l'action a effectuer par l'intelligence artificielle
     */
    @Override
    public Action choosedAction(GameState gameState) {

        beginOfTurnCoordinatesUsX = gameState.getPlayers().get(this.getPlayerId()).getX();
        beginOfTurnCoordinatesUsY = gameState.getPlayers().get(this.getPlayerId()).getY();
        beginOfTurnCoordinatesHimX = gameState.getPlayers().get((this.getPlayerId()+1)%2).getX();
        beginOfTurnCoordinatesHimY = gameState.getPlayers().get((this.getPlayerId()+1)%2).getY();

        //calcul des différents paramètres nécessaires à l'heuristique : distance parcourue par les joueurs, positions des joueurs, distance maximale sur la carte
        if(!beginOfGameInitialized){// si début de partie
            beginOfGameCoordinatesUsX=beginOfTurnCoordinatesUsX;
            beginOfGameCoordinatesUsY=beginOfTurnCoordinatesUsY;
            beginOfGameCoordinatesHimX=beginOfTurnCoordinatesHimX;
            beginOfGameCoordinatesHimY=beginOfTurnCoordinatesHimY;

            lastPositionStartTurnUsX = beginOfTurnCoordinatesUsX;
            lastPositionStartTurnUsY = beginOfTurnCoordinatesUsY;
            lastPositionStartTurnHimX = beginOfTurnCoordinatesHimX;
            lastPositionStartTurnHimY = beginOfTurnCoordinatesHimY;
            maxDistanceOnMap = distanceBetweenCoordinates(0,0,gameState.getMaze().getHeight(),gameState.getMaze().getWidth());
            closestDistanceEnnemyYet = maxDistanceOnMap;
            //System.out.println("max distance on map "+maxDistanceOnMap);
            nombreToursMaximal = NumberTurn.getInstance().getNbTurn();

            beginOfGameInitialized=true;
        }else{
            Player us = gameState.getPlayers().get(this.getPlayerId());
            Player him = gameState.getPlayers().get((this.getPlayerId()+1)%2);
            distanceCoveredThisGameUs += distanceBetweenCoordinates(lastPositionStartTurnUsX,us.getX(),lastPositionStartTurnUsY,us.getY());
            distanceCoveredThisGameHim += distanceBetweenCoordinates(lastPositionStartTurnHimX,him.getX(),lastPositionStartTurnHimY,him.getY());

            lastPositionStartTurnUsX = beginOfTurnCoordinatesUsX;
            lastPositionStartTurnUsY = beginOfTurnCoordinatesUsY;
            lastPositionStartTurnHimX = beginOfTurnCoordinatesHimX;
            lastPositionStartTurnHimY = beginOfTurnCoordinatesHimY;
        }

        if(rechercheEffectuee && actionsAEffectuer.size()<=0){
            rechercheEffectuee=false;
        }

        if(!rechercheEffectuee){ // si l'on a pas encore effectué de recherche, alors l'on recherche les possibles actions à effectuer
            MyArrayList<Action> actionsATester;
            actionsPossibles=gameState.getAllPossibleActions();
            double bestAlpha = this.Alpha;
            int i =0;
            try{
                for(int j=2;j<10;j++){ // appel à alpha-beta
                    //System.out.println("search maxrecursion = "+j);
                    String branch = Integer.toString(i);
                    GameState state = gameState.clone();
                    actionsATester = new MyArrayList<Action>();
                    AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,j,actionsATester,branch,true);

                    if(returnObj.score>bestAlpha){ //on mémorise la meilleure action à effectuer
                        //System.out.println("old score "+bestAlpha+" new score "+returnObj.score);
                        bestAlpha = returnObj.score;
                        this.setMemorizedAction(returnObj.actions.get(0));
                        actionsAEffectuer.clear();
                        actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                        finalscore=returnObj.score;
                        //System.out.println("message : "+returnObj.message);
                    }
                    i++;
                }
                rechercheEffectuee=true;
            }catch (IndexOutOfBoundsException e){
                System.out.println("liste des actions possibles vide");
            }
        }

        //on récupère et on retourne la prochaine action à effectuer
        if (actionsAEffectuer.size()>0){
            Action actionRetournee = actionsAEffectuer.get(0);
            actionsAEffectuer.remove(0);
            if(actionRetournee==Action.ENDTURN){ // si la prochaine action à effectuer est une fin de tour, on supprime les suivantes car ce ne sera plus à nous de jouer
                actionsAEffectuer.clear();
            }
            //System.out.println("le coup choisi est : "+this.actionToString(actionRetournee));
            //System.out.println("le score espéré était "+finalscore);
            return actionRetournee;
        }
        rechercheEffectuee=false;
        actionsAEffectuer.clear();
        return Action.ENDTURN;
    }

    /**
     * @param alpha
     * @param beta
     * @param state, état sur lequel on applique notre algorithme
     * @param leveOfRecursion, niveau de profondeur d'exploration
     * @param maxRecursion, niveau maximal de profondeur d'exploration autorisé
     * @param actions : liste d'actions
     * @param branch, branche de l'arbre que l'on explore
     * @param onJoueVraiment : indique si c'est à notre tour de jouer
     * @return AlphaBetaReturnObj contenant un score, une liste d'actions à effectuer
     */
    private AlphaBetaReturnObj alphaBeta(double alpha, double beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions, String branch,boolean onJoueVraiment) {

        String message="";
        GameState newState;
        MyArrayList<Action> actionsToReturn = actions.clone();
        double foundAlpha=alpha;
        double foundBeta=beta;
        MyArrayList<Action> actions1;
        if(onJoueVraiment){
            if(this.getPlayerId()!=state.getCurrentPlayerId()){
                onJoueVraiment=false;
            }
        }

        // si l'on a dépassé la profondeur maximale autorisée, on choisit de terminer le tour
        if(leveOfRecursion > maxRecursion){

            GameState tryState = state.clone();
            tryState.apply(Action.ENDTURN);
            Player winner;
            actions1 = actions.clone();

            //si la partie est terminée suite à cette action : verification de victoire/defaite et mise à jour éventuelle de alpha/beta
            if(tryState.gameIsOver()){
                winner = tryState.getWinner();
                checkGameResultReturnObject returnObject = checkGameResult(winner,state,leveOfRecursion,foundAlpha,foundBeta,actionsToReturn,actions1,message);
                foundAlpha=returnObject.alpha;
                foundBeta=returnObject.beta;
                actionsToReturn=returnObject.actions;
                message=returnObject.message;

                if(tryState.getCurrentPlayerId() == this.getPlayerId()){ // our turn
                     return new AlphaBetaReturnObj(foundAlpha,actionsToReturn,message);
                } else { //enemy turn
                    return new AlphaBetaReturnObj(foundBeta,actionsToReturn,message);
                }
            }else{ // sinon on retourne l'heuristique
                AlphaBetaReturnObj ret;
                double score=0;

                if(this.getPlayerId()==state.getCurrentPlayerId()){ //calcul des différents paramètres nécessaires à notre heuristique, si c'est à nous de jouer

                    initializeDummyState(state);
                    double distFromEnnemy = this.distanceBetweenPlayers(state);

                    // si le nombre de tours restants est trop faible, on favorise le fait de se rapprocher du joueur ennemi
                    // si la distance à l'ennemi est suffisamment faible, on favorise également le fait de se rapprocher du joueur ennemi
                    if(state.getRemainingTurns() < 26 ||distFromEnnemy<8 || distFromEnnemy<closestDistanceEnnemyYet){
                        double scoreDistEnemy=0;
                        if(distFromEnnemy<closestDistanceEnnemyYet){
                            scoreDistEnemy=100;
                            closestDistanceEnnemyYet=distFromEnnemy;
                        }
                        double relativeDistFromEnnemy = distFromEnnemy/maxDistanceOnMap;
                        scoreDistEnemy += -0.5*NumberTurn.getInstance().getNbTurn()*relativeDistFromEnnemy;
                        score+=scoreDistEnemy;
                        message +="score dist enemy : "+scoreDistEnemy;
                    }

                    double distanceThisTurn = distanceFromBeginOfTurnPos(state.getCurrentPlayer());

                    double walkableScore = maxProfondeur *2-walkableDistanceToPlayer(state.getPlayers().get(state.getCurrentPlayerId()),state.getPlayers().get((state.getCurrentPlayerId()+1)%2),dummyState);

                    double relativedistanceThisTurn = distanceThisTurn;

                    //double scoreGoodBombs = walkableDistanceFromBombToPlayer(state.getPlayers().get((this.getPlayerId()+1)%2),state);

                    int explode = toBeDestroyedWalls(state);

                    score += relativedistanceThisTurn+explode;
                    message += " score dist this turn : "+relativedistanceThisTurn+" score explode : "+explode;
                    if(walkableScore>-1000000){
                        double walkableScoreScore = 4*walkableScore*(1+0.05*NumberTurn.getInstance().getNbTurn());
                        score+= walkableScoreScore;
                        message+=" score walkable dist : "+walkableScoreScore;
                    }

                }else{ // on suppose que l'adversaire joue de manière "constante"
                    score=2;
                }

                if(state.getCurrentPlayerId()==this.getPlayerId()){ // on renvoie le score calculé par l'heuristique et la liste des actions associées
                    ret = new AlphaBetaReturnObj(score,actions,"max level from us, score = "+message);
                }else{
                    ret = new AlphaBetaReturnObj(-score,actions,"max level from other");
                }
                return ret;
            }
        }

        // exploration de l'arbre des actions possibles, algorithme alpha beta standard
        List<Action> possibleActions=null;
        possibleActions = state.getAllPossibleActions(); // on récupère l'ensemble des actions possibles à partir de cet état

        for(int i = 0; i < possibleActions.size() && foundAlpha< foundBeta; i++){

            Action chosenAction = possibleActions.get(i);
            newState = state.clone();
            newState.apply(chosenAction);
            actions1 = actions.clone();

            if(onJoueVraiment){
                actions1.add(chosenAction);
            }

            Player winner; //on examine l'état de la partie après avoir joué l'action choisie
            if(newState.gameIsOver()){
                winner = newState.getWinner();
                checkGameResultReturnObject returnObject = checkGameResult(winner,newState,leveOfRecursion,foundAlpha,foundBeta,actionsToReturn,actions1,message);
                foundAlpha=returnObject.alpha;
                foundBeta=returnObject.beta;
                actionsToReturn=returnObject.actions;
                message=returnObject.message;

            }
            else{
                // on simule le reste des actions si l'action choisie précedemment n'a pas mené à une fin de partie
                AlphaBetaReturnObj returnObj = alphaBeta(foundAlpha, foundBeta,newState,leveOfRecursion+1,maxRecursion,actions1,branch+i,onJoueVraiment);

                if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
                    if(returnObj.score>foundAlpha){
                        foundAlpha=returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        message=returnObj.message;
                    }
                }else{  // noeud min
                    if(returnObj.score<foundBeta) {
                        foundBeta = returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        message=returnObj.message;
                    }
                }
            }
        }
        AlphaBetaReturnObj ret;
        if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
            ret = new AlphaBetaReturnObj(foundAlpha,actionsToReturn,message);
        }else { // noeud min
            ret = new AlphaBetaReturnObj(foundBeta, actionsToReturn,message);
        }
        return ret;
    }

    /**
     * @param a : une action
     * @return String : retourne l'action passée en paramètre sous forme de chaîne de caractères
     */
    public String actionToString(Action a){
        String ret="";
        switch (a){
            case ENDTURN:ret="ENDTURN";break;
            case MOVE_UP:ret="MOVE_UP";break;
            case DROP_BOMB:ret="DROP_BOMB";break;
            case MODE_BOMB:ret="MODE_BOMB";break;
            case MODE_MOVE:ret="MODE_MOVE";break;
            case MOVE_DOWN:ret="MOVE_DOWN";break;
            case MOVE_LEFT:ret="MOVE_LEFT";break;
            case MOVE_RIGHT:ret="MOVE_RIGHT";break;
            case NEXT_SCREEN:ret="NEXT_SCREEN";break;
            case DROP_BOMB_UP:ret="DROP_BOMB_UP";break;
            case MENU_GO_BACK:ret="MENU_GO_BACK";break;
            case DELETE_OBJECT:ret="DELETE_OBJECT";break;
            case DROP_BOMB_DOWN:ret="DROP_BOMB_DOWN";break;
            case DROP_BOMB_LEFT:ret="DROP_BOMB_LEFT";break;
            case DROP_BOMB_RIGHT:ret="DROP_BOMB_RIGHT";break;
            case DROP_SELECTED_OBJECT:ret="DROP_SELECTED_OBJECT";break;
        }
        return ret;
    }

    /**
     * Affiche la liste d'actions passée en paramètres dans le terminal
     * @param list : une liste d'actions
     */
    public void printActions(List<Action> list){
        System.out.println();
        for (Action action: list){
            System.out.print(" | "+actionToString(action)+" | ");
        }System.out.println();
    }

    /**
     * @param state : état courant
     * @return double : la distance entre les deux joueurs
     */
    public double distanceBetweenPlayers(GameState state){
        Player p1 = state.getPlayers().get(0);
        int x1 = p1.getX();
        int y1 = p1.getY();
        Player p2 = state.getPlayers().get(1);
        int x2 = p2.getX();
        int y2 = p2.getY();
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * @param player : un joueur
     * @return double :  la distance parcourue par ce joueur depuis le début de la partie
     */
    public double distanceFromBeginOfGamePos(Player player){
        double x1 = player.getX();
        double y1 = player.getY();
        double x2,y2;
        if(player.getPlayerId()==this.getPlayerId()){
            x2 = this.beginOfGameCoordinatesUsX;
            y2 = this.beginOfGameCoordinatesUsY;
        }else{
            x2 = this.beginOfGameCoordinatesHimX;
            y2 = this.beginOfGameCoordinatesHimY;
        }
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }


    /**
     * @param player : un joueur
     * @return double : la distance parcourue par ce joueur depuis le début du tour
     */
    public double distanceFromBeginOfTurnPos(Player player){
        double x1 = player.getX();
        double y1 = player.getY();
        double x2,y2;
        if(player.getPlayerId()==this.getPlayerId()) {
            x2 = beginOfTurnCoordinatesUsX;
            y2 = beginOfTurnCoordinatesUsY;
        }else{
            x2 = beginOfTurnCoordinatesHimX;
            y2 = beginOfTurnCoordinatesHimY;
        }
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * @param x1 : abscisse point 1
     * @param y1 : ordonnée point 1
     * @param x2 : abscisse point 2
     * @param y2 : ordonnée point 2
     * @return double : la distance entre le point 1 et le point 2
     */
    public double distanceBetweenCoordinates(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * @param player : un joueur
     * @return double : la distance totale parcourue pendant la partie par ce joueur
     */
    public double distanceCoveredThisGame(Player player){
        if(player.getPlayerId()==this.getPlayerId()){
            return distanceCoveredThisGameUs;
        }else{
            return distanceCoveredThisGameHim;
        }
    }

    /**
     * @param myPlayer : le joueur contrôlé par notre intelligence artificielle
     * @param Ennemy : le joueur ennemi
     * @param state : l'état courant
     * @return double : la longeur du chemin du joueur courant à l'ennemi
     */
    public double walkableDistanceToPlayer(Player myPlayer,Player Ennemy,GameState state){
        // uses dummyState
        MyArrayList<Player> players = new MyArrayList<Player>();
        myPlayer.setPlayerId(0);
        players.add(myPlayer);
        state.getMaze().setPlayers(players);
        state.setCurrentPlayerId(0);
        myPlayer=state.getCurrentPlayer();

        double walked = walkableDistanceToPlayer(myPlayer.getCell(),Ennemy,0,0,Directions.DOWN, maxProfondeur);
        return walked;
    }

    /**
     * @param Ennemy : le joueur ennemi
     * @param state : l'état courant
     * @return double
     */
    public double walkableDistanceFromBombToPlayer(Player Ennemy,GameState state){

        double ret=0;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell[] cell1:state.getMaze().getCells()){
            for (Cell cell:cell1){
                cells.add(cell);
            }
        }

        for (Cell cell:cells) {
            boolean containsBomb=false;
            for (GameObject object :cell.getGameObjects()){
                if(object instanceof Bomb){
                    containsBomb=true;
                }
            }
            if(containsBomb){
                ret+=walkableDistanceToPlayer(cell,Ennemy,0,0,Directions.DOWN, maxProfondeur);
            }
        }
        return ret;
    }

    /**
     * @param cell : la cellule courante
     * @param Ennemy : le joueur adverse
     * @param walked : la longueur du chemin courant
     * @param profondeur : profondeur d'exploration courante
     * @param forbiddenDirection : direction interdite dans l'exploration
     * @param limit : profondeur d'exploration limite
     * @return double : la longueur du chemin de la cellule courante à l'ennemi
     */
    public double walkableDistanceToPlayer(Cell cell, Player Ennemy, int walked,int profondeur, Directions forbiddenDirection,int limit){

        //on a trouvé le chemin jusqu'à l'ennemi
        if(cell.getX()==Ennemy.getX() && cell.getY()==Ennemy.getY()){
            return walked;
        }else if(profondeur>limit){ // on a atteint la profondeur d'exploration limite
            return 2147483646;
            //return 2* distanceBetweenCoordinates(cell.getX(),cell.getY(),Ennemy.getX(),Ennemy.getY());
        }

        double minFound=2147483646;
        double found;
        List<Cell> adjacentCells = cell.getAdjacentCells();

        // Right
        if(forbiddenDirection!=Directions.RIGHT || profondeur==0){
            if (adjacentCells.get(0) != null) {
                if (adjacentCells.get(0).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(0),Ennemy,walked+1,profondeur+1,Directions.RIGHT.opposite(),limit);
                    if(found<minFound){
                        minFound=found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(0))){ // on considère que détruire un objet destructible compte comme 2 actions -> walked + 2
                    found = walkableDistanceToPlayer(adjacentCells.get(0),Ennemy,walked+2,profondeur+1,Directions.RIGHT.opposite(),limit);
                    if(found<minFound){
                        minFound=found;
                    }
                }
            }
        }
        // Left
        if(forbiddenDirection!=Directions.LEFT || profondeur==0) {
            if (adjacentCells.get(2) != null) {
                if (adjacentCells.get(2).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(2), Ennemy,walked + 1, profondeur + 1, Directions.LEFT.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(2))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(2), Ennemy,walked + 2, profondeur + 1, Directions.LEFT.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        // Up
        if(forbiddenDirection!=Directions.UP || profondeur==0) {
            if (adjacentCells.get(1) != null) {
                if (adjacentCells.get(1).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(1), Ennemy, walked + 1, profondeur + 1, Directions.UP.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(1))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(1), Ennemy,walked + 2, profondeur + 1, Directions.UP.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        // Down
        if(forbiddenDirection!=Directions.DOWN || profondeur==0) {
            if (adjacentCells.get(3) != null) {
                if (adjacentCells.get(3).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(3), Ennemy, walked + 1, profondeur + 1, Directions.DOWN.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(3))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(3), Ennemy, walked + 2, profondeur + 1, Directions.DOWN.opposite(), limit);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        return minFound;
    }

    /**
     * Crée un nouvel état avec le maze de l'état passé en paramètre mais sans les joueurs
     * @param state : un état
     */
    public void initializeDummyState(GameState state){
        Maze maze = (Maze) state.getMaze().clone();

        //on enlève les players.
        maze.setPlayers(new ArrayList<Player>());

        dummyState = new GameState(maze,0,0);
    }

    /**
     * @param cell : la cellule courante
     * @return booléen : renvoie vrai si la cellule contient au moins un objet destructible
     */
    public boolean cellIsDestructible(Cell cell){
        boolean ret = false;
        for (GameObject object : cell.getGameObjects()){
                if(object instanceof DestructibleWall){
                    ret=true;
                }
        }
        return ret;
    }

    /**
     * @param state : l'état courant
     * @return ret : le nombre de murs que les bombes posées dans le GameState passé en paramètre vont détruire.
     */
    public int toBeDestroyedWalls(GameState state){
        int ret=0;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell[] cell1 : state.getMaze().getCells()){
            for (Cell cell:cell1){
                cells.add(cell);
            }
        }
        int retplus;
        Cell currentCell=null;
        for (Cell cell : cells) {
            for (GameObject object:cell.getGameObjects()){
                if(object instanceof Bomb){
                    retplus=0;
                    for (Directions dir:Directions.values()){
                        boolean continuer=true;
                        for (int l=0;l<initial_bomb_range && continuer;l++){
                            currentCell=cell.getAdjacentCell(dir);
                            if(currentCell!=null){
                                if (cellIsDestructible(currentCell)) {
                                    retplus=retplus+1;
                                    continuer = false;
                                }else if(!currentCell.isWalkable()){
                                    continuer=false;
                                }
                            }
                        }
                    }
                    if(retplus<=0){
                        ret=ret-1000;
                    }else{
                        ret=ret+retplus;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Examine la situation courante si la partie est terminée et met éventuellement à jour alpha ou beta avec une meilleure valeur
     * @param winner : vainqueur de la partie
     * @param state : état courant
     * @param leveOfRecursion : niveau maximal de profondeur
     * @param foundAlpha : meilleur alpha pour le moment
     * @param foundBeta : meilleur beta pour le moment
     * @param actionsToReturn : liste d'actions à renvoyer
     * @param actions1 : liste d'actions associées à l'état
     * @return checkGameResultReturnObject : un objet contenant tous les paramètres qui ont été passés à cette méthode (pour prendre en compte leur éventuelle modification)
     */
    public checkGameResultReturnObject checkGameResult(Player winner, GameState state, int leveOfRecursion, double foundAlpha, double foundBeta, List<Action> actionsToReturn, List<Action> actions1, String message){
        checkGameResultReturnObject ret;
        if(winner!=null){
            if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){ // on gagne et c'était notre tour de jouer
                int possibleScore = +2147483646 - 2*leveOfRecursion;
                if(possibleScore>foundAlpha){
                    actionsToReturn=actions1;
                    foundAlpha=possibleScore;
                    message="on gagne";
                }
            } else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){ // on perd et c'était notre tour de jouer
                int possibleScore = -2147483646 + 2*leveOfRecursion;
                if(possibleScore>foundAlpha){
                    actionsToReturn=actions1;
                    foundAlpha=possibleScore;
                    message="on perd";
                }
            }else if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){ // on gagne et c'était à l'adversaire de jouer
                int possibleScore = +2147483646 - 2*leveOfRecursion;
                if(possibleScore<foundBeta){
                    actionsToReturn=actions1;
                    foundBeta=possibleScore;
                    message="on gagne";
                }
            }else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){ // on perd et c'était à l'adversaire de jouer
                int possibleScore = - 2147483646 + 2*leveOfRecursion;
                if(possibleScore<foundBeta){
                    actionsToReturn=actions1;
                    foundBeta=possibleScore;
                    message="on perd";
                }
            }
        }else{ // égalité
            if(this.getPlayerId()==state.getCurrentPlayerId()){
                if(0>foundAlpha){
                    actionsToReturn=actions1;
                    foundAlpha=0;

                }
            }else{
                if(0<foundBeta){
                    actionsToReturn=actions1;
                    foundBeta=0;
                }
            }
            message="egalité";
        }
        ret = new checkGameResultReturnObject(new MyArrayList<>(actionsToReturn),foundAlpha,foundBeta,message); // enregistrement des paramètres et de leur modification
        return ret;
    }
}