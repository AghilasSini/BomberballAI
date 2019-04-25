package com.glhf.bomberball.screens;

import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.config.GameStoryConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.ui.GameUI;
import com.glhf.bomberball.utils.Directions;
import com.glhf.bomberball.utils.VectorInt2;

import java.util.ArrayList;

public class GameStoryScreen extends GameScreen {

    private ArrayList<Enemy> enemies;
    private ArrayList<Cell> selected_cells = new ArrayList<>();
    private int maze_id;
    private StoryMenuScreen screen;

    public GameStoryScreen(StoryMenuScreen screen, Maze maze, int maze_id) {
        //super(maze);
        super(maze);
        this.maze_id = maze_id;
        this.screen = screen;
        GameStoryConfig config = GameStoryConfig.get();

        current_player = this.maze.spawnPlayer(config);

        //enemies = this.maze.getEnemies();
        enemies = this.maze.getEnemies();
        enemies.forEach(Enemy::createAI);

        addUI(new GameUI(current_player, true, false));
        addUI(maze_drawer);

        current_player.initiateTurn();      //after the UI because initiateTurn notify the ui
        setMoveMode();
        Score.getINSTANCE().resetScore();

        Timer.schedule(new Timer.Task() {   //Verifying if an ennemy has killed the player
            @Override
            public void run() {
                if (!current_player.isAlive()) {
                    Bomberball.changeScreen(new DeadScreen(screen, maze_id));
                    Timer.instance().clear();
                }
            }
        }, 1f, 1f);
    }

    @Override
    public void registerActionsHandlers() {
        super.registerActionsHandlers();
        //input_handler.registerActionHandler(Action.DROP_BOMB, this::teleportPlayer);
    }

    private void teleportPlayer(float x, float y) {
        VectorInt2 pos = maze_drawer.screenPosToCell(x, y);
        if(maze.isCellInBounds(pos.x, pos.y)) {
            current_player.moveToCell(maze.getCellAt(pos.x, pos.y));
        }
    }

    /**
     * gives the next current_player after a turn. If the next current_player is dead, choose the following current_player.
     */
    @Override
	protected void nextPlayer() {
        if(!current_player.isAlive()
            || current_player.getCell().hasInstanceOf(Door.class)){
            endGame();
            return;
        }

        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.initiateTurn();
                enemy.followWay();
            }
        }

        try {
            current_player.initiateTurn();
            setMoveEffect();
            setMoveMode();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    input_handler.lock(false);
                }
            }, 0.1f*enemies.size());

        } catch (RuntimeException e) {
            System.out.println("The player probably died");
        }
    }

    @Override
    protected void endGame() {
        if(current_player.isAlive()){
            if (maze_id + 1 == screen.getMazeCount()) { // if the current_player has completed the last level
                Bomberball.changeScreen(new EndStoryScreen(screen, this.maze_id));
            }else{
                Bomberball.changeScreen(new EndLevelScreen(screen, this.maze_id));
            }
        }else{
            Bomberball.changeScreen(new DeadScreen(screen, maze_id));
        }
    }

    @Override
    protected void startGame() {

    }

    /**
     * enemies path is calculated everytime a bomb has been droped
     * @param dir
     */
    @Override
    protected void dropBomb(Directions dir) {
        super.dropBomb(dir);
        //enemies.forEach(Enemy::updateAI);
    }
}