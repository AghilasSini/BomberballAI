package com.glhf.bomberball.screens;

import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.ui.EditorMenuUI;

/**
 * Extends MenuScreen
 *
 * @author nayala
 */
public class EditorMenuScreen extends MenuScreen {

    public Maze maze;
    private int maze_id = 0;
    private int maze_count;

    public EditorMenuScreen() {
        maze_count = Maze.countMazesMulti();
        maze = Maze.importMazeMulti("maze_" + maze_id);
        this.addUI(new EditorMenuUI(this));
    }

    public void nextMaze() {
        maze_id = (maze_id + 1) % maze_count;
        maze = Maze.importMazeMulti("maze_" + maze_id);
    }

    public void previousMaze() {
        maze_id = (maze_id + maze_count - 1) % maze_count;
        maze = Maze.importMazeMulti("maze_" + maze_id);
    }

    public int getMazeId() {
        return maze_id;
    }
}
