package com.glhf.bomberball.maze;

import com.glhf.bomberball.gameobject.IndestructibleWall;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.Node;

import java.util.*;

public class MazeTransversal{

    /**
     * Returns all reacheable cells whitin a specified range from a cell
     * @param cell_origin
     * @param range
     * @return reacheable cells whithin range of cell_origin
     * Breadth First Search algorithm (BFS) stopping at depth range
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
                for (Cell other : c.getAdjacentCellsInMaze()) {
                    if (!cells.contains(other) && other.isWalkable()) {
                        inactive_queue.add(other);
                        cells.add(other);
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
     * Returns all cells reachable from a cell
     * @param cell_origin
     * @return cells reachable from cell_origin
     * Breadth First Search algorithm (BFS)
     */
    public static ArrayList<Cell> getReacheableCells(Cell cell_origin) {
        ArrayList<Cell> cells = new ArrayList<>();
        LinkedList<Cell> queue = new LinkedList<>();
        cells.add(cell_origin);
        queue.add(cell_origin);
        while (!queue.isEmpty()) {
            Cell c = queue.poll();
            for (Cell other : c.getAdjacentCellsInMaze()) {
                if (!cells.contains(other)) {
                    queue.add(other);
                    cells.add(other);
                }
            }
        }
        return cells;
    }

    public static boolean isReachableCell(Cell cell_origin, Cell cell_final){
        ArrayList<Cell> cells = new ArrayList<>();
        LinkedList<Cell> queue = new LinkedList<>();
        cells.add(cell_origin);
        queue.add(cell_origin);
        while (!queue.isEmpty()) {
            Cell c = queue.poll();
            if (c.getX() == cell_final.getX() && c.getY() == cell_final.getY()) {
                return true;
            }
            for (Cell other : c.getAdjacentCellsInMaze()) {
                if(other.getInstancesOf(IndestructibleWall.class).size() == 0){
                    if (!cells.contains(other)) {
                        queue.add(other);
                        cells.add(other);
                    }
                }
            }
        }
        return false;
    }

    public static ArrayList<Directions> getRandomPath(Cell cell) {
        ArrayList<Directions> path = new ArrayList<>();
        for(int i=0; i<10; i++){
            List<Directions> dirs = Arrays.asList(Directions.values());
            Collections.shuffle(dirs);
            Cell adjCell=null;
            for(Directions dir : dirs) {
                adjCell = cell.getAdjacentCell(dir);
                if (adjCell!=null && adjCell.isWalkable()){
                    path.add(dir);
                    break;
                }
            }
            if(adjCell==null || !adjCell.isWalkable()){//if all adjacent cell are not walkable
                return path;//return empty path
            }
            cell=adjCell;
        }
        //need to add the return path
        ArrayList<Directions> return_path = new ArrayList<>(path);
        Collections.reverse(return_path);
        for(int i=0; i<return_path.size(); i++){
            return_path.set(i, return_path.get(i).opposite());
        }
        path.addAll(return_path);
        return path;
    }

    /**
     * this method gives the "root" of a kind of tree which represents the different available ways from an initial position
     * @param initial_position
     * @return Node
     */
    public static Node constructWay(Cell initial_position, int range) {
        Node way = new Node(null, null, initial_position);
        LinkedList<Node> to_construct = new LinkedList<>();
        Node current_node;
        Node son;
        ArrayList<Cell> family;
        Cell current_adjacent_cell;
        to_construct.add(way);
        while (to_construct.size() > 0) {
            current_node = to_construct.poll();
            if (current_node.getAncestors() == null || current_node.getAncestors().size() < range) {
                for (Directions d : Directions.values()) {
                    current_adjacent_cell = current_node.getMatching_cell().getAdjacentCell(d);
                    family = current_node.getAncestors();
                    if(current_adjacent_cell == null
                            || !current_adjacent_cell.isWalkable()
                            || (family != null && family.contains(current_adjacent_cell))) {
                        current_node.setSons(null, d);
                        current_node.sonIsSet();
                    } else {
                        family = family == null ? new ArrayList<>() : new ArrayList<>(family);
                        family.add(current_node.getMatching_cell());
                        son = new Node(family, current_node, current_adjacent_cell);
                        current_node.setSons(son, d);
                        if (d.ordinal() < to_construct.size()) {
                            to_construct.add(d.ordinal(), son);
                        } else {
                            to_construct.add(son);
                        }
                    }
                }
            }
        }
        return way;
    }


    /**
     * this method gives the longest way that the active enemy will follow, chose the longest path
     * @param initial_node
     * @return ArrayList<Cell> a path
     */
    public static ArrayList<Cell> longestWay(Node initial_node){
        ArrayList<Cell> longest = new ArrayList<>();
        ArrayList<Cell> current;
        if(initial_node != null) {
            for(int i=0; i<4; i++){
                current = longestWay(initial_node.getSons(i));
                if (current != null && current.size() > longest.size()) {
                    longest = current;
                }
            }
            longest.add(0, initial_node.getMatching_cell());
        }
        return longest;
    }

    /**
     * gives the sequence of moves that the active enemy has to follow, if its a cycle from the initial position, then
     * does the cycle or revert his own path otherwise.
     * @param initial_node
     * @return ArrayList<Directions>
     */
    public static ArrayList<Directions> longestWayMovesSequence(Node initial_node){
        ArrayList<Directions> moves_sequence = new ArrayList<>();
        ArrayList<Directions> moves_sequence_mirror = new ArrayList<>();
        ArrayList<Cell> longest_way;
        longest_way = longestWay(initial_node);
        int longest_way_size = longest_way.size();
        Directions next_direction;
        Directions last_direction;
        for(int i=0; i< longest_way_size-1; i++){
            next_direction = longest_way.get(i).getCellDir(longest_way.get(i+1));
            moves_sequence.add(next_direction);
            moves_sequence_mirror.add(0, Directions.values()[(next_direction.ordinal()+2)%4]);
        }
        last_direction = longest_way.get(longest_way_size-1).getCellDir(initial_node.getMatching_cell());
        if(longest_way.get(longest_way_size-1).getCellDir(initial_node.getMatching_cell()) != null){
            moves_sequence.add(last_direction);
        }
        else{
            moves_sequence.addAll(moves_sequence_mirror);
        }
        return moves_sequence;
    }


    /**
     * this methods give to the agressif enemy the way he has to follow if the player is in his area given by range
     * this method is to be called at the beginning of the enemy's turn
     * @param cell_origin
     * @param range
     * @return ArrayList<Directions>
     */
    @SuppressWarnings("unchecked")
	public static ArrayList<Directions> depth_graph_transversal(Cell cell_origin, int range) {
        ArrayList<Cell> cells = new ArrayList<>();
        LinkedList<Cell> active_queue = new LinkedList<>();
        LinkedList<Cell> inactive_queue = new LinkedList<>();
        HashMap<Cell, ArrayList<Directions>> paths = new HashMap<>();
        int depth = 0;
        paths.put(cell_origin, new ArrayList<>());
        cells.add(cell_origin);
        active_queue.add(cell_origin);
        ArrayList<Directions> path_bis;
        // Invariant : Distance to all cells in the active queue is depth
        while (depth < range) {
            while (!active_queue.isEmpty()) {
                Cell c = active_queue.poll();
                for (Cell other : c.getAdjacentCellsInMaze()) {
                    if (!cells.contains(other) && other.isWalkable()) {
                        inactive_queue.add(other);
                        path_bis = (ArrayList<Directions>) paths.get(c).clone();
                        path_bis.add(c.getCellDir(other));
                        paths.put(other, path_bis);
                        cells.add(other);
                    }
                }
            }
            depth++;

            active_queue = inactive_queue;
            inactive_queue = new LinkedList<>();
        }
        for(Cell c : cells){
            if(!c.getInstancesOf(Player.class).isEmpty()){
                return paths.get(c);
            }
        }
        return null;
    }
}
