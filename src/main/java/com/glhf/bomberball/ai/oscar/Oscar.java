package com.glhf.bomberball.ai.oscar;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.BonusWall;
import com.glhf.bomberball.gameobject.GameObject;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.List;

// gradle build && java -jar build\libs\ia_bomberball-all-1.0.jar
public class Oscar extends AbstractAI {

    private final int MAX = 10000;
    private final int MIN = -10000;
    private int OscarIndex;
    private int EnemyIndex;
    private boolean indexNotSet;

    public Oscar(GameConfig config, String player_skin, int playerId) {
        super(config, player_skin, "Oscar", playerId);
        indexNotSet = true;
        System.out.println("Creating OscarAI " + playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        if(indexNotSet) {
            setIndexes(gameState.getPlayers());
            indexNotSet = false;
        }

        int profondeur = 5;
        GameState son;
        int alphaMax = MIN;
        int alphaChallenger;
        while(profondeur < 100) {
            System.out.println("profondeur: " + profondeur);
            for(Action action : gameState.getAllPossibleActions()) {
                son = gameState.clone();
                son.apply(action);
                alphaChallenger = alphabeta(son, MIN, MAX, profondeur);
                if(alphaChallenger == MAX) {
                    return action;
                }
                if(alphaChallenger > alphaMax) {
                    System.out.println("Action mémorisée: " + action + " -------- avec alpha = " + alphaChallenger);
                    this.setMemorizedAction(action);
                    alphaMax = alphaChallenger;
                }
            }
            profondeur++;
        }
        return this.getMemorizedAction();
    }

    private int score(GameState state) {
        state.apply(Action.ENDTURN); // faire exploser les bombes
        if(state.gameIsOver()) {
            return scoreGO(state);
        }

        int score = 0;
        List<Player> players = state.getPlayers();

        score += players.get(OscarIndex).bonus_moves * 15 - players.get(EnemyIndex).bonus_moves * 10;
        score += (players.get(OscarIndex).bonus_bomb_range - players.get(EnemyIndex).bonus_bomb_range) * 5;
        if(players.get(OscarIndex).bonus_bomb_number < 1) {
            score += (players.get(OscarIndex).bonus_bomb_number - players.get(EnemyIndex).bonus_bomb_number);
        }
        else {
            score -= (players.get(OscarIndex).bonus_bomb_number - 2) * 5;
        }
        score -= getBonusWallCount(state) * 5;
        if(state.getRemainingTurns() < 30) {
            score -= distanceWithEnemy(players);
        }

        return score;
    }

    private int scoreGO(GameState state) {
        if(state.getWinner() != null) {
            int winnerId = state.getWinner().getPlayerId();
            if(winnerId == this.player_id) {
                return MAX;
            }
            else {
                return MIN;
            }
        }
        return -50;
    }

    private int alphabeta(GameState node, int alpha, int beta, int profondeur) {
        if(profondeur == 1) {
            return score(node);
        }
        else if(node.gameIsOver()) {
            return scoreGO(node);
        }
        else {
            GameState son;
            if(node.getCurrentPlayerId() == this.player_id) {
                //noeud MAX
                for(Action action : node.getAllPossibleActions()) {
                    if(alpha >= beta) {
                        return alpha;
                    }
                    son = node.clone();
                    son.apply(action);
                    alpha = max(alpha, alphabeta(son, alpha, beta, profondeur - 1));
                }
                return alpha;
            }
            else {
                //noeud MIN
                for(Action action : node.getAllPossibleActions()) {
                    if(alpha >= beta) {
                        return beta;
                    }
                    son = node.clone();
                    son.apply(action);
                    beta = min(beta, alphabeta(son, alpha, beta, profondeur - 1));
                }
                return beta;
            }
        }
    }


    private int max(int a, int b) {
        if(a > b) {
            return a;
        }
        else {
            return b;
        }
    }

    private int min(int a, int b) {
        if(a < b) {
            return a;
        }
        else {
            return b;
        }
    }

    private void setIndexes(List<Player> players) {
        if(players.get(0).getPlayerId() == this.player_id) {
            OscarIndex = 0;
            EnemyIndex = 1;
        }
        else {
            EnemyIndex = 0;
            OscarIndex = 1;
        }
    }

    private int getBonusWallCount(GameState state) {
        int bonusWall = 0;

        for(int i = 0; i < state.getMaze().getWidth(); i++) {
            for(int j = 0; j < state.getMaze().getHeight(); j++) {
                for(GameObject obj : state.getMaze().getCellAt(i, j).getGameObjects()) {
                    if(obj instanceof BonusWall) {
                        bonusWall++;
                    }
                }
            }
        }

        return bonusWall;
    }

    private int distanceWithEnemy(List<Player> players) {
        return abs(players.get(OscarIndex).getX() - players.get(EnemyIndex).getX()) + abs(
                players.get(OscarIndex).getY() - players.get(EnemyIndex).getY());
    }

    private int abs(int nb) {
        if(nb < 0) {
            return -nb;
        }
        else {
            return nb;
        }
    }

    public String toString() {
        return "I'm the mighty Oscar " + this.player_id;
    }
}
