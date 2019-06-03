package com.glhf.bomberball.ai.lima;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.gameobject.DestructibleWall;
import com.glhf.bomberball.gameobject.IndestructibleWall;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.gameobject.Wall;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeTransversal;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import javafx.util.Pair;

import java.util.*;

public class Heuristique {
    public static float calculate(GameState gm, int ai_id){
        List<Player> players = gm.getPlayers();
        assert gm.getPlayers().size()==2;
        Player ai = players.get(ai_id);
        Player enemy = players.get((ai_id + 1) % 2);
        float distance = distance(ai.getCell(), enemy.getCell());
        float v = 1000;
        v -= distance*0.5;
        v -= countType(gm.getMaze(), DestructibleWall.class)*4;
        v += bonus(ai)*5;
        v -= bonus(enemy)*5;
        if(enemyCanKill(enemy, ai.getCell()))
            v -= 100000;
        return v; //todo check gm.getCurrentPlayerId()!=ai_id?v:-v;
    }

    private static int bonus(Player player) {
        return player.bonus_bomb_number+player.bonus_bomb_range+player.bonus_moves;
    }

    private static boolean enemyCanKill(Player enemy, Cell ai_cell) {
        ArrayList<Cell> cells = MazeTransversal.getReacheableCellsInRange(enemy.getCell(), enemy.bonus_moves + 5 + 1);//+1 drop bomb 1 farther
        for (Cell cell : cells) {
            if(cell.distanceTo(ai_cell) <= enemy.bonus_bomb_range+3)
                if(bombCanReach(cell, ai_cell, enemy.bonus_bomb_range+3))//bomb can reach
                    return true;
        }
        return false;
    }

    private static boolean bombCanReach(Cell bomb_cell, Cell ai_cell, int bombRange) {
        for (Directions dir : Directions.values()) {
            Cell c = bomb_cell;
            for (int i = 0; i < bombRange; i++) {
                c=c.getAdjacentCell(dir);
                if(c==null || c.hasInstanceOf(Wall.class))break;
                if(c == ai_cell)
                    return true;
            }
        }
        return false;
    }

    private static float distance(Cell o, Cell a) {
        LinkedList<Pair<Cell, Integer>> queue = new LinkedList<>();
        TreeSet<Cell> cells = new TreeSet<>((o1, o2) -> o1.getX()*1000+o1.getY()-o2.getX()*1000-o2.getY());
        queue.add(new Pair<>(o,0));
        cells.add(o);
        while (!queue.isEmpty()) {
            Pair<Cell, Integer> p = queue.poll();
            Cell c = p.getKey();
            if (c.getX() == a.getX() && c.getY() == a.getY()) {
                return p.getValue();
            }
            for (Cell other : c.getAdjacentCellsInMaze()) {
                if(other.getInstancesOf(IndestructibleWall.class).size() == 0){
                    if (!cells.contains(other)) {
                        queue.add(new Pair<>(other, p.getValue()+1));
                        cells.add(other);
                    }
                }
            }
        }
        throw new RuntimeException("distance : unable to find destination cell");
    }

    private static <T> int countType(Maze maze, Class<T> clazz){
        int count=0;
        Cell[][] cells = maze.getCells();
        for (Cell[] col : cells) {
            for (Cell cell : col) {
                if(cell.hasInstanceOf(clazz)) count++;
            }
        }
        return count;
    }
}
