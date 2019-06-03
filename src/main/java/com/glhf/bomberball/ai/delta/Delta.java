package com.glhf.bomberball.ai.delta;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.List;

import static com.glhf.bomberball.utils.Action.*;

public class Delta extends AbstractAI {

    static Action[] dirToBombDrop = {DROP_BOMB_RIGHT, DROP_BOMB_UP, DROP_BOMB_LEFT, DROP_BOMB_DOWN};

    static final int MAX_EXPLORE_COUNT = Integer.MAX_VALUE;
    static final int MAX_DEPTH = 3;

    GameConfig config;
    LinkedList<Action> actions_for_turn = new LinkedList<>();
    LinkedList<Action> actions_tmp = new LinkedList<>();
    Random rng;
    int explore_count;
    int solution_depth;
    boolean best_possible_turn;

    public Delta(GameConfig config, String player_skin, int playerId) {
        super(config, "orc_warrior", "Delta", playerId);
        this.config = config;
        setMemorizedAction(ENDTURN);
        rng = new Random();
    }

    @Override
    public Action choosedAction(GameState state) {
        // Si une précédente recherche avait abouti à des actions, mémoriser la première action afin de la jouer
        // si aucune autre solution n'est trouvée.
        if (!actions_for_turn.isEmpty()) {
            memorizeAction(actions_for_turn.poll());
        }
        else {
            best_possible_turn = false;
            solution_depth = 1;
        }

        // Si les actions actuelles n'amènent pas au meilleur état possible
        // on recherche de meilleures actions pour le tour.
        if (!best_possible_turn) {
            // On lance la recherche du meilleur état en augmentant la profondeur de recherche
            // jusqu'à atteindre la profondeur maximale.
            while (solution_depth <= MAX_DEPTH && !best_possible_turn) {
                int alpha = findActionsForTurn(state, solution_depth);
//                System.err.println("Best turn for depth " + solution_depth + " found  (" + alpha + ")");
                // La recherche du meilleur état pour la profondeur solution_depth est terminée,
                // On enregistre les meilleures actions.
                if (solution_depth != 2)
                    memorizeActionsForTurn(alpha);
                solution_depth++;
            }
            // Ici on a trouvé le meilleur état (et ses actions) pour la profondeur maximale ou la victoire.
            // Il n'y a pas de meilleures actions à effectuer pour cette profondeur.
            best_possible_turn = true;
        }

        // On retourne null, l'action éffectuée sera l'action mémorisée.
        return null;
    }

    /**
     * Recherche les meilleures actions à effectuer pour une profondeur donnée
     * (Mémorise les actions)
     * @param state Etat actuel du jeu
     * @param depth Profondeur de recherche
     * @return
     */
    private int findActionsForTurn(GameState state, int depth) {
        explore_count = 0;
//        System.out.println(" alphabeta depth " + depth + " [...]");
        int alpha = alphabeta(state, depth);
//        System.out.println(" explore_count : " + explore_count + " - alpha : " + alpha);
        return alpha;
    }

    /**
     * Memorise les actions pré-calculés par alphabeta
     */
    private void memorizeActionsForTurn(int alpha) {
        actions_for_turn = actions_tmp;
        if (alpha == Integer.MAX_VALUE) // Victoire
            best_possible_turn = true;
//        System.out.println(" actions_for_turn " + actions_for_turn + " (" + alpha + ") " + (best_possible_turn ? "[VICTORY]\n" : "\n"));
        memorizeAction(actions_for_turn.poll());
    }

    /**
     *
     * @param state Etat actuel
     * @param alpha
     * @param beta
     * @param depth Profondeur actuelle de recherche
     * @param max_depth Profondeur maximale de recherche
     * @return alpha
     */
    private int alphabeta(GameState state, int alpha, int beta, int depth, int max_depth) {
        if (estTerminal(state)) { // Etat terminal
            explore_count++;
            return utilite(state);
        } else if (depth >= max_depth) {
            explore_count++;
            return heuristique(state);
        } else {
            if (state.getCurrentPlayerId() == getPlayerId()) { // Etat MAX
                List<Pair<GameState, List<Action>>> childs = getChilds(state);
                Collections.shuffle(childs, rng);
                for (Pair<GameState, List<Action>> child : childs) {  // Itération sur les états fils de fin de tour.
                    int r = alphabeta(child.getFirst(), alpha, beta, depth + 1, max_depth);
                    if (depth == 0 && r > alpha) {
                        actions_tmp = (LinkedList<Action>) child.getSecond();
//                        System.out.println("actions_tpm " + actions_tmp + " (" + r + ")");
                        if (solution_depth == 2)
                            memorizeActionsForTurn(alpha);
                    }
                    alpha = Math.max(alpha, r);
                    if (alpha >= beta || explore_count >= MAX_EXPLORE_COUNT)
                        break;
                }
                return alpha;
            } else { // Etat MIN
                List<Pair<GameState, List<Action>>> childs = getChilds(state);
                Collections.shuffle(childs, rng);
                for (Pair<GameState, List<Action>> child : childs) { // Itération sur les états fils de fin de tour.
                    int r = alphabeta(child.getFirst(), alpha, beta, depth + 1, max_depth);
                    beta = Math.min(beta, r);
                    if (alpha >= beta || explore_count >= MAX_EXPLORE_COUNT)
                        break;
                }
                return beta;
            }
        }
    }

    private int alphabeta(GameState state, int max_depth) {
        return alphabeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, max_depth);
    }

    /**
     * Cacule les états fils d'un état de jeu inital représentant un tour d'un joueur.
     * @param state Etat du jeu initial
     * @param actions Actions ayant amené à l'état state
     * @param use_move Les actions peuvent ou non être des mouvements.
     * @return Liste de couples Etat - Liste d'actions
     * Pour chaque couple, l'état est obtenu en appliquant la liste d'actions à l'état initial
     */
    public List<Pair<GameState, List<Action>>> getChilds(GameState state, List<Action> actions, boolean use_move) {
        if (estTerminal(state) || state.turnIsOver()) {
            return new ArrayList<>();
        } else {
            List<Pair<GameState, List<Action>>> childs = new LinkedList<>();
            Player player = state.getCurrentPlayer();
            Cell player_cell = player.getCell();
            int moves_remaining = player.getNumberMoveRemaining();
            int bombs_remaining = player.getNumberBombRemaining();

            // Movement childs
            if (moves_remaining > 0 && use_move) {
                for (Path path : getAvailablePaths(player_cell, moves_remaining)) {
                    if (path.getLength() > 0) {
                        GameState new_state = state.clone();
                        List<Action> path_actions = path.toActions();
                        applyActionsToState(path.toActions(), new_state);
                        List<Action> new_actions_list = new LinkedList<>(actions);
                        new_actions_list.addAll(path_actions);
                        if (path.getLength() < moves_remaining || bombs_remaining > 0)
                            childs.addAll(getChilds(new_state, new_actions_list, false));
                        else
                            childs.add(new Pair<>(new_state, new_actions_list));
                    }
                }
            }

            // Bomb drop childs
            if (bombs_remaining > 0) {
                for (Directions dir : Directions.values()) {
                    Cell cell = player_cell.getAdjacentCell(dir);
                    if (cell != null && cell.isWalkable()) {
                        GameState new_state = state.clone();
                        Action bomb_action = dirToBombDrop[dir.ordinal()];
                        new_state.apply(bomb_action);
                        List<Action> new_actions_list = new LinkedList<>(actions);
                        new_actions_list.add(bomb_action);
                        if (bombs_remaining > 1 || moves_remaining > 0)
                            childs.addAll(getChilds(new_state, new_actions_list, true));
                        else
                            childs.add(new Pair<>(new_state, new_actions_list));
                    }
                }
            }

            // End turn child
            GameState new_state = state.clone();
            new_state.apply(ENDTURN);
            List<Action> new_actions_list = new LinkedList<>(actions);
            new_actions_list.add(ENDTURN);
            childs.add(new Pair<>(new_state, new_actions_list));

            return childs;
        }
    }

    public List<Pair<GameState, List<Action>>> getChilds(GameState state, boolean use_move) {
        return getChilds(state, new LinkedList<Action>(), use_move);
    }

    public List<Pair<GameState, List<Action>>> getChilds(GameState state) {
        return getChilds(state, new LinkedList<Action>(), true);
    }

    /**
     * Fonction d'utilité d'un état.
     * @param state Etat du jeu
     * @return
     */
    private int utilite(GameState state) {
        Player winner = state.getWinner();
        if (winner == null && state.gameIsOver()) { // Draw
            return -2500; // Eviter les matchs nuls.
        } else { // Winner
            return (winner.getPlayerId() == getPlayerId()) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
    }

    /**
     * @param state Etat du jeu
     * @return L'état est terminal (gagnant ou égalitée)
     */
    private boolean estTerminal(GameState state) {
        Player winner = state.getWinner();
        return (winner != null) || (winner == null && state.gameIsOver()); // Winner or Draw
    }

    /**
     * Fonction heuristique d'un état de jeu.
     * @param state Etat du jeu
     * @return
     */
    private int heuristique(GameState state) {
        Player player = state.getPlayers().get(getPlayerId());
        Player oponent = state.getPlayers().get((getPlayerId() + 1) % 2);
        return getPlayerScore(state, player) - getPlayerScore(state, oponent);
    }

    /**
     * Renvoie le score d'un joueur
     * @param state Etat du jeu
     * @param player Joueur
     * @return
     */
    private int getPlayerScore(GameState state, Player player) {
        int score = 0;
        score += scoreFromBonuses(state, player);
        score += scoreFromRange(state, player);
        // coefs non linéaires
        // Distance à l'adversaire
        return score;
    }

    /**
     * Calcule le score de liberté de mouvement d'un joueur
     * @param state Etat du jeu
     * @param player Joueur
     * @return
     */
    private int scoreFromRange(GameState state, Player player) {
        Cell cell = player.getCell();
        int moves = config.initial_player_moves + player.bonus_moves;
        return MazeTransversal.getReacheableCellsInRange(cell, moves).size();
    }

    /**
     * Cacule le score des bonus d'un joueur
     * @param state Etat du jeu
     * @param player Joueur
     * @return
     */
    private int scoreFromBonuses(GameState state, Player player) {
        int score = 0;
        // Possibilité de modifier les poids des bonus
        score += 1000 * player.bonus_moves;
        score += 1000 * player.bonus_bomb_range;
        score += 1000 * player.bonus_bomb_number;
        return score;
    }

    /**
     * Applique une liste d'action à un état.
     * @param actions Actions à effectuer.
     * @param state Etat du jeu.
     */
    private void applyActionsToState(List<Action> actions, GameState state) {
        for (Action action : actions) {
            state.apply(action);
        }
    }

    /**
     * Renvoie les chemins possibles depuis une origine
     * @param cell_origin Cellule d'origine
     * @param range Distance depuis la cellule d'origine
     * @return Chemins possibles
     */
    public List<Path> getAvailablePaths(Cell cell_origin, int range) {
        HashMap<Cell, Path> paths = new HashMap<>();
        List<Cell> cells = new ArrayList<>();
        LinkedList<Cell> active_queue = new LinkedList<>();
        LinkedList<Cell> inactive_queue = new LinkedList<>();
        int depth = 0;
        cells.add(cell_origin);
        paths.put(cell_origin, new Path(cell_origin));
        active_queue.add(cell_origin);
        // Invariant : Distance to all cells in the active queue is depth
        while (depth < range) {
            while (!active_queue.isEmpty()) {
                Cell c = active_queue.poll();
                for (Directions dir : Directions.values()) {
                    Cell other = c.getAdjacentCell(dir);
                    if (other != null && !cells.contains(other) && other.isWalkable()) {
                        inactive_queue.add(other);
                        cells.add(other);
                        paths.put(other, new Path(paths.get(c), dir));
                    }
                }
            }
            depth++;
            active_queue = inactive_queue;
            inactive_queue = new LinkedList<>();
        }
        return new ArrayList<>(paths.values());
    }

    /**
     * Classe chemin (cellules d'origine et d'arrivée et directions)
     */
    public class Path {


        Action[] dirToAction = {MOVE_RIGHT, MOVE_UP, MOVE_LEFT, MOVE_DOWN};

        public List<Directions> moves;
        public Cell origin;
        public Cell end;

        public Path(Cell origin) {
            this.origin = origin;
            moves = new ArrayList<>();
        }

        public Path(Path other, Directions dir) {
            moves = new ArrayList<>(other.moves);
            moves.add(dir);
            origin = other.origin;
            end = other.origin.getAdjacentCell(dir);
        }

        public int getLength() {
            return moves.size();
        }

        @Override
        public String toString() {
            String str = "Path (" + getLength() + ") : ";
            Cell current = origin;
            str += "\t {" + current.getX() + "," + current.getY() + "}";
            for (Directions dir : moves) {
                current = origin.getAdjacentCell(dir);
                str += "\t {" + current.getX() + "," + current.getY() + "}";
            }
            str += "\n";
            return str;
        }

        public List<Action> toActions() {
            List<Action> actions = new ArrayList<>();
            for (Directions dir : moves)
                actions.add(dirToAction[dir.ordinal()]);
            return actions;
        }
    }
}
