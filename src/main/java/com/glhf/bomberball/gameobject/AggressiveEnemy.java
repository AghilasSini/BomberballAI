package com.glhf.bomberball.gameobject;

import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.Node;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.glhf.bomberball.utils.Constants.MAX_DEPTH;

public class AggressiveEnemy extends Enemy {

    private int hunting_range;

    public AggressiveEnemy(){
        super();
    }

    public AggressiveEnemy(String skin, int life, int initial_moves, int strength, int hunting_range) {
        super(skin, life, initial_moves, strength);
        // active mode when created
        this.hunting_range = hunting_range;
    }

    @Override
    public void createAI() {
        this.nb_reachable_cells = MazeTransversal.getReacheableCellsInRange(this.cell, 100).size();
        this.way = MazeTransversal.longestWayMovesSequence(MazeTransversal.constructWay(this.getCell(), MAX_DEPTH));
    }

    @Override
    public void updateAI() {
        if(cell!=null){// equivalent to isAlive()
            this.way = MazeTransversal.longestWayMovesSequence(MazeTransversal.constructWay(this.getCell(), MAX_DEPTH));
        }
    }


    @Override
    public GameObject clone() {
        return new AggressiveEnemy(skin,life,initial_moves, strength, hunting_range);
    }

    @Override
    public void followWay() {
        ArrayList<Cell> reachableCells = MazeTransversal.getReacheableCellsInRange(cell, hunting_range);
        ArrayList<GameObject> gos;
        for (Cell c : reachableCells) {
            gos = c.getGameObjects();
            for (GameObject go : gos) {
                if (go instanceof Player) {
                    way = shortestWay(MazeTransversal.constructWay(this.cell, hunting_range), go.getCell());
                    actual_move = 0;
                }
            }
        }

        if (moves_remaining > 0 && !way.isEmpty()) {
            if (!this.move(way.get(actual_move))) {
                way = MazeTransversal.longestWayMovesSequence(MazeTransversal.constructWay(this.getCell(), MAX_DEPTH));
            }
            actual_move = (actual_move+1)%way.size();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    followWay();
                }
            }, 0.1f);
        }
    }


    public ArrayList<Directions> shortestWay(Node ways, Cell target) {
        //Getting the shortest path to the target
        Node current_way = null;
        Node current_node;
        LinkedList<Node> to_visit = new LinkedList<>();
        to_visit.add(ways);

        while (to_visit.size() > 0) {
            current_node = to_visit.poll();

            if (current_node.getMatching_cell() == target && current_way == null) {
                current_way = current_node;
            } else if (current_node.getMatching_cell() == target && current_node.getAncestors().size() < current_way.getAncestors().size()) {
                current_way = current_node;
            }

            for (int i=0; i<4; i++) {
                if (current_node.getSons(i) != null) {
                    to_visit.add(current_node.getSons(i));
                }
            }
        }
        ArrayList<Cell> shortest_way = current_way.getAncestors();
        shortest_way.add(current_way.getMatching_cell());


        //Creating the moves sequence
        ArrayList<Directions> moves_sequence = new ArrayList<>();
        int shortest_way_size = shortest_way.size();
        Directions next_direction;

        for(int i=0; i< shortest_way_size-1; i++){
            next_direction = shortest_way.get(i).getCellDir(shortest_way.get(i+1));
            moves_sequence.add(next_direction);
        }

        return moves_sequence;
    }
}
