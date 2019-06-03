package com.glhf.bomberball.ai.kilo;

import com.badlogic.gdx.Game;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.List;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import org.lwjgl.Sys;

/**
 * Cette classe represente l'IA fonctionnant avec un approfondissement iteratif
 */
public class FirstAI extends AbstractAI {

    /**
     * Constructeur
     * @param config la configuration de la partie
     * @param player_skin le visuel du personnage
     * @param playerId l'id de l'ia dans le jeu
     */
    public FirstAI(GameConfig config, String player_skin, int playerId) {
        super(config,"wizzard_m","FirstAI",playerId);
        // TODO Auto-generated constructor stub
    }

    /**
     * Choisit l'action a renvoyer au thread de l'ia
     * @param etat l'etat du jeu
     * @return l'action
     */
    @Override
    public Action choosedAction(GameState etat){
        List<Action> actionsPossibles = etat.getAllPossibleActions();

        Action actionChoisie = actionsPossibles.get(0);
        float meilleurScore = -99;
        int i;

        int profParcours = 10; //TODO: A définir
        int profCourant = 1;

        while(profCourant<profParcours && !(etat.gameIsOver())) {
            for (i = 0; i < actionsPossibles.size(); i++) {
                GameState copieEtat = etat.clone();
                Action curEtat = actionsPossibles.get(i);
                copieEtat.apply(curEtat);
                float currentScore = ApprofondissementIteratif(copieEtat, -99, 99, 1, profCourant);
                if (currentScore > meilleurScore) {
                    meilleurScore = currentScore;
                    actionChoisie = actionsPossibles.get(i);
                }
            }
            this.setMemorizedAction(actionChoisie);
            profCourant++;
        }
        if (kill(etat) && safe(etat)) actionChoisie = Action.ENDTURN;
        else if (kill(etat) && !safe(etat) && moves_remaining > 0){
            Action getsafe = getSafe(etat);
            if (getsafe != null) actionChoisie = getsafe;
        }
        else if(!safe(etat) && moves_remaining == 1){
            Action getsafe = getSafe(etat);
            if (getsafe != null) actionChoisie = getsafe;
        }
        else if(safe(etat) && moves_remaining == 1){
            actionChoisie = Action.ENDTURN;
        }
        return actionChoisie;
    }


    /**
     * Cette fonction calcule l'heuristique de l'appronfondissement iteratif
     * @param etat l'etat du jeu
     * @return le score calcule selon l'etat du jeu
     */
    public float h(GameState etat){
        float ret = 0;
        Cell[][] cells = etat.getMaze().getCells();
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        ArrayList<DestructibleWall> dws = new ArrayList<DestructibleWall>();
        ArrayList<Bonus> bonus = new ArrayList<Bonus>();

        Player opponent = null;
        boolean oppInRange = false;
        boolean weakWallInRange = false;
        boolean safe = true;

        //ajoute les bombes, les bonus et les murs destructibles dans leurs listes respectives
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[0].length; j++){
                for (GameObject g : cells[i][j].getGameObjects()){
                    objects.add(g);
                    if (g instanceof Bomb) bombs.add((Bomb)g);
                    if (g instanceof DestructibleWall) dws.add((DestructibleWall) g);
                    if (g instanceof Bonus) bonus.add((Bonus) g);
                }
            }
        }

        //recupere l'adversaire
        for (Player p : etat.getPlayers()){
            if (p.getPlayerId() != this.getPlayerId())opponent = p;
        }

        //nombre mouvements possibles du joueur courant
        List<Action> possibleActions= etat.getAllPossibleActions();
        int cpt = 0;
        for (Action a : possibleActions){
            if (a == Action.MOVE_UP ||
                    a == Action.MOVE_DOWN ||
                    a == Action.MOVE_LEFT ||
                    a == Action.MOVE_RIGHT){
                cpt += 1;
            }
        }

        for (Bomb o : bombs) {
            //verifie si l'adversaire est in range
            if (o.getX() == opponent.getX() && Math.abs(o.getY() - opponent.getY()) <= this.getBombRange() &&
                    !verifyWall(o, opponent, "Y", etat)) {
                oppInRange = true;
            }
            if (o.getY() == opponent.getY() && Math.abs(o.getX() - opponent.getX()) <= this.getBombRange() &&
                    !verifyWall(o, opponent, "X", etat)) {
                oppInRange = true;
            }

            //malus pour adversaire out range
            if (!oppInRange) ret -= 5;

            //veirifie si le joueur courant est out range
            if ((o.getX() == this.getX() && Math.abs(o.getY() - this.getY()) <= this.getBombRange()) ||
                (o.getY() == this.getY() && Math.abs(o.getX() - this.getX()) <= this.getBombRange())) {
                safe = false;
            }

            //verifie si un mur destructible est in range
            for (DestructibleWall w : dws){
                if (o.getX() == w.getX() && Math.abs(o.getY() - w.getY()) <= this.getBombRange() &&
                        !verifyWall(o, w, "Y", etat)) {
                    weakWallInRange = true;
                }
                if (o.getY() == w.getY() && Math.abs(o.getX() - w.getX()) <= this.getBombRange() &&
                        !verifyWall(o, w, "X", etat)) {
                    weakWallInRange = true;
                }
            }
        }

        //adversaire in range, et joueur courant safe
        if (oppInRange && safe) ret += 60;

        //adversaire in range, bonus different suivant le nombre de mouvements restant
        if ((!safe) && oppInRange && this.getMovesRemaining() == 3) ret += 30;
        else if ((!safe) && oppInRange && this.getMovesRemaining() >= 4) ret += 40;

        //si le joueur courant ne peut plus se deplacer, et qu'il n'est pas safe
        if ((!safe) && cpt == 0) ret -= 60;

        //si le joueur courant est safe mais plus de mouvements restant
        if (safe && this.getMovesRemaining() <= 1) ret += 30;

        //si le joueur courant est non safe, bonus different suivant le nombre de mouvements restant
        if ((!safe) && this.getMovesRemaining() <= 1) ret -= 50;
        else if ((!safe) && this.getMovesRemaining() == 2) ret -= 10;
        else if ((!safe) && this.getMovesRemaining() == 3) ret += 30;
        else if ((!safe) && this.getMovesRemaining() >= 3) ret += 40;

        //malus/bonus suivant le nombre de mouvements possibles ce tour
        if (cpt == 0) ret -= 80;
        if (cpt == 1) ret -= 5;
        if (cpt > 2) ret += (cpt*2);

        //si un mur destructible est vise, bonus different suivant le nombre de mouvements restant
        if (!oppInRange) {
            if (weakWallInRange && safe) ret += 25;
            else if (weakWallInRange && this.getMovesRemaining() >= 3) ret += 20;
            else if (weakWallInRange && this.getMovesRemaining() == 2) ret += 10;
        }

        //malus si l'adversaire est trop eloigne
        int distanceOpp = Math.abs(opponent.getX() - this.getX()) + Math.abs(opponent.getY() - this.getY());
        if(distanceOpp > 7) ret -= distanceOpp * 6;
        else if (distanceOpp > 5) ret -= distanceOpp * 5;

        //en cas de depassement de seuil
        if (ret > 100) ret = 100;
        if (ret < -100) ret = -100;
        return ret;
    }

    /**
     * Algorithme principal
     * @param n l'etat du jeu
     * @param alpha la valeur de alpha
     * @param beta la valeur de beta
     * @param curProf la profondeur courante
     * @param maxProf la profondeur maximale
     * @return la valeur du score courant
     */
    public float ApprofondissementIteratif(GameState n, float alpha, float beta, int curProf, int maxProf){
        //On est arrivé à une feuille de l'arbre
        if (n.gameIsOver()) {
            if (n.getWinner() == this) {
                return 100;
            } else {
                return -100;
            }
        }

        //Sinon, le jeu n'est pas terminé
        List<Action> actionsPossibles = n.getAllPossibleActions();
        if (actionsPossibles.isEmpty()){
            return -100;
        }
        int joueur = n.getCurrentPlayerId();

        //On est à une feuille de la longueur courante, mais pas de l'arbre
        if(curProf==maxProf) {
            return h(n);
        }
        //En cours de calcul
        else {
            //Type Max
            if (this.getPlayerId() == joueur) {
                int action;
                for (action = 0; (action < actionsPossibles.size()) && (alpha < beta); action++) {

                    GameState copieden = n.clone();
                    Action curAction = actionsPossibles.get(action);
                    copieden.apply(curAction);
                    alpha = Math.max(alpha, ApprofondissementIteratif(copieden, alpha, beta, curProf + 1, maxProf));
                }
                return alpha;
            }

            //Type Min
            else {
                int action;
                for (action = 0; (action < actionsPossibles.size()) && (alpha < beta); action++) {

                    GameState copieden = n.clone();
                    Action curAction = actionsPossibles.get(action);
                    copieden.apply(curAction);
                    beta = Math.min(beta, ApprofondissementIteratif(copieden, alpha, beta, curProf + 1, maxProf));
                }
                return beta;
            }
        }
    }


    /**
     * Verifie si il y a un objet bloquant entre une bombe et un joueur
     * @param b la bombe
     * @param p le joueur
     * @param typeCoord permet de savoir si il faut verifier les abscisses ou ordonnees
     * @param n l'etat du jeu
     * @return true si il y a un objet bloquant
     */
    private boolean verifyWall(Bomb b, Player p, String typeCoord, GameState n){
        boolean ret = false;
        ArrayList<GameObject> caseObj = new ArrayList<GameObject>();
        if (typeCoord.equals("X")){
            if (b.getX() - p.getX() > 1){
                for(GameObject o : n.getMaze().getCells()[b.getX() - 1][b.getY()].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getX() - p.getX() > 2){
                    for(GameObject o : n.getMaze().getCells()[b.getX() - 2][b.getY()].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getX() - p.getX() > 3){
                        for(GameObject o : n.getMaze().getCells()[b.getX() - 3][b.getY()].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }
            }
            else if (b.getX() - p.getX() < -1){
                for(GameObject o : n.getMaze().getCells()[b.getX() + 1][b.getY()].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getX() - p.getX() < -2){
                    for(GameObject o : n.getMaze().getCells()[b.getX() + 2][b.getY()].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getX() - p.getX() < -3){
                        for(GameObject o : n.getMaze().getCells()[b.getX() + 3][b.getY()].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }

            }
        }
        else{
            if (b.getY() - p.getY() > 1){
                for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 1].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getY() - p.getY() > 2){
                    for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 2].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getY() - p.getY() > 3){
                        for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 3].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }

            }
            else if (b.getY() - p.getY() < -1){
                for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 1].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getY() - p.getY() < -2){
                    for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 2].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getY() - p.getY() < -3){
                        for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 3].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }
            }
        }
        for(GameObject o : caseObj){
            if (o instanceof Wall)ret = true;
            else if (o instanceof Player){
                if (((Player) o).getPlayerId() != p.getPlayerId()){
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * Verifie si il y a un objet bloquant entre une bombe et un mur destructible
     * @param b la bombe
     * @param w le mur destructible
     * @param typeCoord permet de savoir si il faut verifier les abscisses ou ordonnees
     * @param n l'etat du jeu
     * @return true si il y a un objet bloquant
     */
    private boolean verifyWall(Bomb b, DestructibleWall w, String typeCoord, GameState n){
        boolean ret = false;
        ArrayList<GameObject> caseObj = new ArrayList<GameObject>();
        if (typeCoord.equals("X")){
            if (b.getX() - w.getX() > 1){
                for(GameObject o : n.getMaze().getCells()[b.getX() - 1][b.getY()].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getX() - w.getX() > 2){
                    for(GameObject o : n.getMaze().getCells()[b.getX() - 2][b.getY()].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getX() - w.getX() > 3){
                        for(GameObject o : n.getMaze().getCells()[b.getX() - 3][b.getY()].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }
            }
            else if (b.getX() - w.getX() < -1){
                for(GameObject o : n.getMaze().getCells()[b.getX() + 1][b.getY()].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getX() - w.getX() < -2){
                    for(GameObject o : n.getMaze().getCells()[b.getX() + 2][b.getY()].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getX() - w.getX() < -3){
                        for(GameObject o : n.getMaze().getCells()[b.getX() + 3][b.getY()].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }

            }
        }
        else{
            if (b.getY() - w.getY() > 1){
                for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 1].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getY() - w.getY() > 2){
                    for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 2].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getY() - w.getY() > 3){
                        for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() - 3].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }

            }
            else if (b.getY() - w.getY() < -1){
                for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 1].getGameObjects()){
                    caseObj.add(o);
                }
                if (b.getY() - w.getY() < -2){
                    for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 2].getGameObjects()){
                        caseObj.add(o);
                    }
                    if (b.getY() - w.getY() < -3){
                        for(GameObject o : n.getMaze().getCells()[b.getX()][b.getY() + 3].getGameObjects()){
                            caseObj.add(o);
                        }
                    }
                }
            }
        }
        for(GameObject o : caseObj){
            if (o instanceof IndestructibleWall)ret = true;
            else if (o instanceof Player){
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Verifie si une bombe va tuer l'adversaire au prochain tour
     * @param etat
     * @return true si le joueur adverse va mourir
     */
    public boolean kill(GameState etat){
        boolean kill = false;
        Cell[][] cells = etat.getMaze().getCells();
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        Player opponent = null;
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[0].length; j++){
                for (GameObject g : cells[i][j].getGameObjects()){
                    objects.add(g);
                    if (g instanceof Bomb) bombs.add((Bomb)g);
                }
            }
        }
        for (Player p : etat.getPlayers()){
            if (p.getPlayerId() != this.getPlayerId())opponent = p;
        }
        for (Bomb o : bombs) {
            //adversaire en range
            if (o.getX() == opponent.getX() && Math.abs(o.getY() - opponent.getY()) <= this.getBombRange() &&
                    !verifyWall(o, opponent, "Y", etat)) {
                kill = true;
            }
            if (o.getY() == opponent.getY() && Math.abs(o.getX() - opponent.getX()) <= this.getBombRange() &&
                    !verifyWall(o, opponent, "X", etat)) {
                kill = true;
            }

        }
        return kill;
    }

    /**
     * Verifie si ce joueur est en danger
     * @param etat l'etat du jeu
     * @return true si ce joueur n'est pas a portee d'une bombe
     */
    public boolean safe(GameState etat){
        boolean safe = true;
        Cell[][] cells = etat.getMaze().getCells();
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[0].length; j++){
                for (GameObject g : cells[i][j].getGameObjects()){
                    objects.add(g);
                    if (g instanceof Bomb) bombs.add((Bomb)g);
                }
            }
        }
        for (Bomb o : bombs) {
            if ((o.getX() == this.getX() && Math.abs(o.getY() - this.getY()) <= this.getBombRange()) ||
                    (o.getY() == this.getY() && Math.abs(o.getX() - this.getX()) <= this.getBombRange())) {
                safe = false;
            }
        }
        return safe;
    }

    /**
     * Verifie si ce joueur est en danger sur l'axe des abscisses
     * @param etat l'etat du jeu
     * @return true si ce joueur est a portee d'une bombe sur l'axe des abscisses
     */
    public boolean exploAbs(GameState etat){
        boolean exploAbs = false;
        Cell[][] cells = etat.getMaze().getCells();
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        ArrayList<Bomb> bombs = new ArrayList<Bomb>();
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[0].length; j++){
                for (GameObject g : cells[i][j].getGameObjects()){
                    objects.add(g);
                    if (g instanceof Bomb) bombs.add((Bomb)g);
                }
            }
        }
        for (Bomb o : bombs) {
            if (o.getY() == this.getY()) exploAbs = true;
        }
        return exploAbs;
    }

    /**
     * Permet au joueur de se mettre sur une case hors de danger lorsqu'il est en danger
     * @param etat l'etat du jeu
     * @return l'action qui le met hors de danger si elle existe
     */
    public Action getSafe(GameState etat){
        Action safe = null;
        List<Action> possibleActions = etat.getAllPossibleActions();
        ArrayList<Action> moveDispo = new ArrayList<>();
        boolean exploAbs = exploAbs(etat);
        for (Action a : possibleActions){
            if (a == Action.MOVE_UP ||
                    a == Action.MOVE_DOWN ||
                    a == Action.MOVE_LEFT ||
                    a == Action.MOVE_RIGHT){
                moveDispo.add(a);
            }
        }
        for (Action a : moveDispo) {
            if (a == Action.MOVE_UP && exploAbs) return a;
            else if(a == Action.MOVE_DOWN && exploAbs) return a;
            else if(a == Action.MOVE_LEFT && !exploAbs) return a;
            else if(a == Action.MOVE_RIGHT && !exploAbs) return a;
        }
        return safe;
    }
}

