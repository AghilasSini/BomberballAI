package com.glhf.bomberball.screens;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.config.GameStoryConfig;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.ui.StoryMenuUI;

public class StoryMenuScreen extends MenuScreen {

    private final StoryMenuUI ui;
    public Maze maze;
    private int maze_id = 0;
    private GameStoryConfig config;

    public StoryMenuScreen(){
        super();
        config = GameStoryConfig.get();
        maze = Maze.importMazeSolo("maze_" + maze_id);
        ui = new StoryMenuUI(this);
        addUI(ui);
    }

    /**
     * Change the maze to the next one in the maze list
     */
    public void nextMaze(){
        int nb_max = config.last_level_unlocked+1;
        maze_id = (maze_id + 1) % nb_max;
        maze = Maze.importMazeSolo("maze_" + maze_id);
    }

    /**
     * Change the maze to the previous one in the maze list
     */
    public void previousMaze(){
        int nb_max = config.last_level_unlocked+1;
        maze_id = (nb_max + maze_id - 1) % nb_max;
        maze = Maze.importMazeSolo("maze_" + maze_id);
    }

    /**
     * selects the maze of the specified parameter
     * @param i
     */
    public void getMaze(int i)
    {
        maze_id = i;
        maze = Maze.importMazeSolo("maze_" + maze_id);
    }

    public Maze getMaze()
    {
        return maze;
    }

    /**
     * informs if the specified level is locked or not
     * @param i
     * @return true if the level is unlocked, false otherwise
     */
    public boolean isLevelUnlocked(int i){
        return i <= config.last_level_unlocked;
    }

    // getters and setters

    public int getMazeId(){
        return maze_id;
    }

    public int getMazeCount(){ return GameConfig.maze_count; }

    /**
     * sets the state of a specified level to unlocked
     * @param i
     */
    public void setLevelUnlocked(int i){
        config.last_level_unlocked=i;
        ui.unlockLevel(i);
        config.exportConfig();
    }

    public int getLastLevelPlayed() {
        return config.last_level_played;
    }
    public void setLastLevelPlayed(int i) {
        config.last_level_played=i;
        config.exportConfig();
    }
}
