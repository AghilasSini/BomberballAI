package com.glhf.bomberball.maze;

import com.glhf.bomberball.config.GameInfiniteConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.gameobject.Bonus.Type;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.VectorInt2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class MazeBuilder {
    private static LinkedHashMap<Class<? extends Wall>, Double> availableWall;
    private static Maze maze;
    private static Random rand = new Random();
    private static Cell cellDoor;
    private static Cell cellPlayer;
    private static double probFreeCase;
    private static int nb_ennemies = 10;
    private static GameInfiniteConfig config;


    public static Maze createInfinityMaze(int difficulty){
        config = GameInfiniteConfig.get();
        probFreeCase = config.probFreeCase;

        nb_ennemies = 1 + difficulty/2;

        maze = new Maze();

        maze.title = "Classic";
        int diff_sizer = 1 + difficulty/15;
        maze.height = 7 + (int) (Math.random()*5*diff_sizer)*2;
        maze.width = 9 + (int) (Math.random()*5*diff_sizer)*2;

        maze.spawn_positions = new ArrayList<>();
        VectorInt2 pos = new VectorInt2(0, rand.nextInt(maze.height));
        maze.spawn_positions.add(pos);

        availableWall = new LinkedHashMap<>();
        availableWall.put(IndestructibleWall.class, 4.0);

        if(config.bonus_activated){
            availableWall.put(BonusWall.class, 1.0);
        }
        if(config.destructible_wall_available){
            availableWall.put(DestructibleWall.class, 4.0);
        }

        initialiseCells();
        initialiseWalls(difficulty);
        initialiseEnemies(difficulty);


        return maze;
    }

    private static void initialiseCells() {

        maze.cells = new Cell[maze.width][maze.height];
        for (int x = 0; x < maze.width; x++) {
            for (int y = 0; y < maze.height; y++) {
                maze.cells[x][y] = new Cell(x, y);
            }
        }
        maze.initialize();
        cellDoor = maze.getCellAt(maze.width-1,rand.nextInt(maze.height));
        VectorInt2 pos = maze.spawn_positions.get(0);
        cellPlayer = maze.getCellAt(pos.x, pos.y);
        cellDoor.addGameObject(new Door());

    }

    private static void initialiseWalls(int difficulty) {
        normalizeProba();

        for (int x = 0; x < maze.width; x++) {
            for (int y = 0; y < maze.height; y++) {
                if(maze.spawn_positions.get(0).dst(x,y)<=1) continue;
                if(x == cellDoor.getX() && (y == cellDoor.getY())){
                }
                else{
                     if(Math.random() < probFreeCase ) {
                         maze.cells[x][y].addGameObject(randomWall());
                     }
                }
            }
        }

        if(!MazeTransversal.isReachableCell(cellPlayer, cellDoor)){
            initialiseCells();
            initialiseWalls(difficulty);
        }
    }

    private static void initialiseEnemies(int difficulty) {
        double diff_ratio = 0.5 + Math.pow(0.5, difficulty);
        System.out.println(diff_ratio);
        for(int i = 0; i< nb_ennemies; i++){
            Cell cell;
            int x;
            int y;
            do {
                x = rand.nextInt(maze.width);
                y = rand.nextInt(maze.height);
                cell = maze.cells[x][y];
            }while (!cell.isEmpty() || maze.spawn_positions.get(0).equals(x,y));
            float r = rand.nextFloat();
            if (r < diff_ratio) {
                ArrayList<Directions> ways = MazeTransversal.getRandomPath(cell);
                cell.addGameObject(new PassiveEnemy("skelet", config.passiveEnemy_life, config.passiveEnemy_moves, config.passiveEnemy_strength, ways));
            } else if (i < difficulty/3) {
                cell.addGameObject(new AggressiveEnemy("wogol", config.aggressiveEnemy_life, config.aggressiveEnemy_moves, config.aggressiveEnemy_strength, config.aggressiveEnemy_huntingRange));
            } else {
                cell.addGameObject(new ActiveEnemy("swampy", config.activeEnemy_life, config.activeEnemy_moves, config.activeEnemy_strength));
            }
        }
    }

    private static Wall randomWall() {
        try {
            double range_start=0;
            double rand = Math.random();
            for(Class<? extends GameObject> c : availableWall.keySet()){
                double range_end = range_start + availableWall.get(c);
                if (rand < range_end) {
                    GameObject o = c.newInstance();
                    if(o instanceof BonusWall){
                        Bonus b = new Bonus(Type.values()[(int)(Math.random()*10)%3]);
                        o = new BonusWall(b);
                    }
                    return (Wall)o;
                }
                range_start=range_end;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error in randomWall");
    }

    private static void normalizeProba(){
        Double somme = 0.0;
        for (Double proba : availableWall.values()) {
            somme += proba;
        }
        for (Class<? extends Wall> key : availableWall.keySet()) {
            availableWall.put(key, availableWall.get(key)/somme);
        }
    }

}
