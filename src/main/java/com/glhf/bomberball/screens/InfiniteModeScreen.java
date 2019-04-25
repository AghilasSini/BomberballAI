package com.glhf.bomberball.screens;

import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.ui.InfiniteMenuUI;

import java.util.ArrayList;

public class InfiniteModeScreen extends MenuScreen {

    //attributes
    public Maze maze;
    private ArrayList<Character> characters; // one player and some enemies

    public InfiniteModeScreen() {
        super();
        addUI(new InfiniteMenuUI(this));
    }


}
