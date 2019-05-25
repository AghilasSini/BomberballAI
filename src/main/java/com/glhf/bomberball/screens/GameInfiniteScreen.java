package com.glhf.bomberball.screens;

import com.badlogic.gdx.utils.Timer;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.config.GameInfiniteConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.gameobject.NumberTurn;
import com.glhf.bomberball.maze.MazeBuilder;
import com.glhf.bomberball.ui.GameUI;
import com.glhf.bomberball.utils.Directions;

import java.util.ArrayList;

public class GameInfiniteScreen extends GameScreen {

    private ArrayList<Enemy> enemies;
    private int difficulty;

    public GameInfiniteScreen(int difficulty) {
        //super(maze);
        super(MazeBuilder.createInfinityMaze(difficulty));
        this.difficulty = difficulty;
        GameInfiniteConfig config = GameInfiniteConfig.get();
        current_player = this.maze.spawnPlayer(config);

        enemies = this.maze.getEnemies();
        enemies.forEach(Enemy::createAI);

        addUI(new GameUI(current_player, true, config.finite_number_turn));
        addUI(maze_drawer);

        current_player.initiateTurn();      //after the UI because initiateTurn notify the ui
        setMoveMode();

        NumberTurn.getInstance().resetNbTurn();

        Timer.schedule(new Timer.Task() {   //Verifying if an ennemy has killed the player
            @Override
            public void run() {
                if (!current_player.isAlive()) {
                    endGame();
                    Timer.instance().clear();
                }
            }
        }, 1f, 1f);
    }

    @Override
	protected void endGame() {
        GameInfiniteConfig config = GameInfiniteConfig.get();
        Score s = Score.getINSTANCE();
        if(config.highscore < s.getScore()){
            config.highscore = s.getScore();
            config.exportConfig();
        }
        Bomberball.changeScreen(new EndInfiniteScreen());
    }

    @Override
    protected void startGame() {
    }

    @Override
    public void registerActionsHandlers() {
        super.registerActionsHandlers();
    }

    /**
     * gives the next current_player after a turn. If the next current_player is dead, choose the following current_player.
     */
    @Override
	protected void nextPlayer() {
        if(GameInfiniteConfig.get().finite_number_turn){
            NumberTurn nt = NumberTurn.getInstance();
            nt.decreaseTurn(1);
            if(nt.getNbTurn()==0){
                endGame();
            }
        }
        if (!current_player.isAlive()) {
            endGame();
        } else {

            // test if the current_player reached the door
            boolean isIn = false;
            for (GameObject o : current_player.getCell().getGameObjects()) {
                if (o instanceof Door) {
                    isIn = true;
                }
            }
            if (isIn) {
                Bomberball.changeScreen(new GameInfiniteScreen(difficulty+1));
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
                }, 0.2f*enemies.size());

            } catch (RuntimeException e) {
                System.out.println("The player probably died");
            }
        }
    }

    /**
     * enemies path is calculated everytime a bomb has been droped
     * @param dir
     */
    @Override
    protected void dropBomb(Directions dir) {
        super.dropBomb(dir);
    }
}