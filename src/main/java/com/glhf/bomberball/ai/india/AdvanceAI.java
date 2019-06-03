package com.glhf.bomberball.ai.india;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.List;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;


/**
 * Implémentation d'un joueur artificiel Avancé
 */
public class AdvanceAI extends AbstractAI {

    public static int PROFONDEUR_MAX = 50;

    //TODO: Modifier la taille en fonction de la carte choisie.
    public static int TAILLE_MAZE = 11;

    /**
     * Constructeur
     *
     ** //@param nom nom du joueur
     */
    public AdvanceAI(GameConfig config, String player_skin, int playerId) {
        super(config,"knight_m","AdvanceAI",playerId);
    }

    /**
     * Choisit une action
     *
     * //@param GameState État actuel de la partie
     * @return une action
     */
    @Override
    public Action choosedAction(GameState gameState) {

        //Clonage de l'état
        GameState etatClone = gameState.clone();
        setMemorizedAction(null);

        //temps initial
        long startTime = System.currentTimeMillis();

        for (int i = 2; i <= PROFONDEUR_MAX ; i++) {
            //Fin du tour automatique
            if (System.currentTimeMillis() - startTime > TIME_TO_THINK){
                break;
            }
            alphabeta(etatClone, 1, i, -99999 , 99999);
        }
        return  getMemorizedAction();
    }

    /**
     * Fonction d'utilité
     *
     * //@param GameState État actuel de la partie
     * @return une action
     */
    public double valeurEtat(GameState gameState){
        int res = -200;
        if(gameState.gameIsOver() && gameState.getWinner() != null){
            if(this.getPlayerId() == gameState.getWinner().getPlayerId()){
                res = 1000 + gameState.getRemainingTurns();
            }else{
                res = -1000 - gameState.getRemainingTurns();
            }
        }
        return res;
    }

    /**
     * Fonction d'heuristique
     *
     * //@param GameState État actuel de la partie
     * @return un double
     */
    public double heuristique(GameState gameState){
        int score = 0;
        Maze maze = gameState.getMaze();

        int nbblocs=0;
        int nbblocBonus=0;

        //calcul du nombre de blocs destructibles restants
        for(int i=0; i<TAILLE_MAZE; i++){
            for (int j=0; j<TAILLE_MAZE; j++){
                Cell curcell = maze.getCellAt(i,j);
                if(curcell.hasInstanceOf(DestructibleWall.class))
                {
                    nbblocs+=1;
                }
                if(curcell.hasInstanceOf(BonusWall.class)){
                    nbblocBonus+=1;
                }
            }
        }

        //HEURISTIQUE//
        //Important de casser des blocs
        score -= nbblocs;
        score -= nbblocBonus*2;

        Player player = gameState.getPlayers().get(player_id);
        //Important d'accumuler des bonus
        score += player.bonus_bomb_range*10+ player.bonus_bomb_number*10 + player.bonus_moves*10;

        //rapprocher l'ia de l'adversaire si il ne reste plus beaucoup de tours
        if (gameState.getRemainingTurns() < 5){
            score =- distanceToPlayer(gameState);
        }

        return score;
    }

    public int distanceToPlayer(GameState gameState){
        Player player0 = gameState.getPlayers().get(0);
        Player player1 = gameState.getPlayers().get(1);
        Cell cellDest = new Cell(player1.getX(), player1.getY());
        return Math.round(gameState.getMaze().getCellAt(player0.getX(),player0.getY()).distanceTo(cellDest));
    }

    public double alphabeta(GameState gameState,int profondeurArbre, int profondeurLimite, double a, double b){

        if (gameState.gameIsOver()){
            return valeurEtat(gameState);
        }

        if(profondeurArbre == PROFONDEUR_MAX){
            return heuristique(gameState);
        }

        //Arrêt itérratif
        if (profondeurArbre == profondeurLimite){
           return heuristique(gameState);
        }

        List<Action> possibleActions= gameState.getAllPossibleActions();
        Action meilleurAction = null;

        //Joueur max, ici l'ia
        if (gameState.getCurrentPlayerId() == this.getPlayerId())
        {
            for (int i = 0; i < possibleActions.size() ; i++) {
                GameState clone = gameState.clone();
                Action currentMove = possibleActions.get(i);
                clone.apply(currentMove);

                double valeur = alphabeta(clone, profondeurArbre+1,profondeurLimite, a, b);

                //alpha = Max(alpha, alphabeta)
                if (valeur > a){
                    a = valeur;
                    meilleurAction = currentMove;
                }

                if (a >= b) {
                    break; //élagage
                }
            }

            //Enregistrement de la meilleur action
            if(profondeurArbre == 1 && meilleurAction != null) {
                setMemorizedAction(meilleurAction);
            }

            return a;
        }

        //Joueur min
        else
        {
            for (int i = 0; i < possibleActions.size() ; i++) {
                GameState clone = gameState.clone();
                Action currentMove = possibleActions.get(i);
                clone.apply(currentMove);

                double valeur = alphabeta(clone,profondeurArbre+1,profondeurLimite, a, b);

                if (valeur < b){
                    b = valeur;
                }

                if (a >= b) {
                    break; //élagage
                }
            }
            return b;
        }
    }

}
