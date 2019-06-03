package com.glhf.bomberball.ai.echo;

import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.List;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;

public class Echo extends AbstractAI
{

    private int bombBonus;
    private int moveBonus;
    private int powerBonus;
    private int nb_blocs;
    private int tmp = -1000;
    private GameState ActualGameState;
    private int prof_max;
    private Action meilleureaction;

    public Echo(GameConfig config,String player_skin,int playerId)
    {
        super(config,"black_knight","Echo",playerId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Action choosedAction(GameState ActualGameState)
    {
        tmp = 0;

        /* Mise à jour de variables globales pour l'heuristique */
        nb_blocs = nbBlocBroken(ActualGameState);
        bombBonus = ActualGameState.getCurrentPlayer().bonus_bomb_number;
        moveBonus = ActualGameState.getCurrentPlayer().bonus_moves;
        powerBonus = ActualGameState.getCurrentPlayer().bonus_bomb_range;

        this.ActualGameState = ActualGameState;

        /* Boucle pour gérer la profondeur de l'approfondissement itératif */
        for (int i = 1; i <= 100; i++)
        {
            prof_max = i;
            int t = alpha_beta(ActualGameState, 0, -300, 300);
            if(t>=tmp)
            {
                System.out.println("Coup " + meilleureaction + " avec score " + t);
                memorizedAction = meilleureaction;
                tmp = t;
            }
        }

        return memorizedAction;

    }

    /* Fonction alpha_beta avec approfondissement itératif */
    int alpha_beta(GameState gameState, int profondeur, int alpha, int beta)
    {
        /* Etat terminal */
        if(gameState.gameIsOver())
        {
            return utilite(gameState);
        }

        else if(profondeur==prof_max)
        {
            return heuristic(gameState);
        }

        /* Etat non terminal */
        else
        {
            List<Action> actionsPossibles = gameState.getAllPossibleActions();
            int actionsSize = actionsPossibles.size();

            /* Si tour de l'IA : Etat MAX */
            if(gameState.getCurrentPlayerId()==this.getPlayerId())
            {
                int i = 0;
                while( i< actionsSize && alpha < beta )
                {
                    Action actionToTry = actionsPossibles.get(i);
                    GameState gameStateClone = gameState.clone();
                    gameStateClone.apply(actionToTry);

                    if(gameState.equals(ActualGameState))
                    {
                        int val = alpha_beta(gameStateClone,profondeur+1,alpha,beta);
                        if( val > alpha )
                        {
                            alpha = val;
                            meilleureaction = actionToTry;  // Mise à jour de la meilleure action
                        }
                    }
                    else
                    {
                        alpha = Math.max(alpha,alpha_beta(gameStateClone,profondeur+1,alpha,beta));
                    }

                    i++;
                }

                return alpha;
            }

            /* Si tour d'un opposant : Etat MIN */
            else
            {
                int i = 0;
                while( i< actionsSize && alpha < beta )
                {
                    Action actionToTry = actionsPossibles.get(i);
                    GameState gameStateClone = gameState.clone();
                    gameStateClone.apply(actionToTry);
                    int val = alpha_beta(gameStateClone,profondeur+1,alpha,beta);
                    beta = Math.min(beta,val);
                    i++;

                }

                return beta;

            }
        }
    }

    /* Fonction d'utilité */
    int utilite(GameState gameState)
    {
        Player winner = gameState.getWinner();
        /* Egalite */
        if(winner==null)
        {
            return 0;
        }
        /* Victoire de l'IA */
        else if(winner.getPlayerId()==gameState.getCurrentPlayerId())
        {
            return 300;
        }
        /* Défaite de l'IA */
        else
        {
            return -300;
        }

    }

    /* Fonction permettant le calcul de l'heuristique */
    int heuristic(GameState gameState)
    {

        int h_walls = 0;
        int h_distance = 0;
        int h_bonus = 0;


        /* Partie distance */
        /////////////////////
        Player player1 = gameState.getPlayers().get(0);
        Player player2 = gameState.getPlayers().get(1);
        h_distance = 30 - (int)(2*distance(player1,player2));  // Plus on s'éloigne -> diminue l'heuristique


        /* Partie blocs cassés */
        /////////////////////////
        int walls = 0;
        walls = nbBlocBroken(gameState);
        if( walls > nb_blocs )
        {
            h_walls = walls*2;
        }


        /* Partie bonus */
        //////////////////

        Player playerIA = gameState.getPlayers().get(this.getPlayerId());
        int nb_Bomb = playerIA.bonus_bomb_number;
        int nb_Moves = playerIA.bonus_moves;
        int bombRange = playerIA.bonus_bomb_range;

        if( nb_Bomb > bombBonus || nb_Moves > moveBonus || bombRange > powerBonus )
        {
            h_bonus = 3*nb_Bomb + 3*nb_Moves + 3*bombRange;
        }

        /* On retourne l'heuristique totale */
        return h_distance + h_walls + h_bonus;
    }

    /* Fonction pour détecter si des blocs ont été cassé d'un tour à l'autre */
    public int nbBlocBroken(GameState gameState)
    {

        int widthMaze = gameState.getMaze().getWidth();
        int heightMaze = gameState.getMaze().getHeight();
        Cell[][] cells = gameState.getMaze().getCells();
        int h_blocBroken = 0;

        for (int x = 0; x < widthMaze; x++)
        {
            for (int y = 0; y < heightMaze; y++)
            {
                /* On vérifie s'il y a plus de blocs où l'on peut marcher dessus */
                if( cells[x][y].isWalkable() )
                {
                    h_blocBroken++;
                }
            }
        }
        return h_blocBroken;
    }

    /* Fonction pour calculer la distance entre 2 joueurs donnés */
    public float distance(Player joueur1 , Player joueur2)
    {
        Cell x = joueur1.getCell();
        Cell y = joueur2.getCell();
        float distance = y.distanceTo(x);
        return distance;
    }
}
