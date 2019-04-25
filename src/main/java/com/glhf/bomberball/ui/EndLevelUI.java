/**
 * @author : Rémy
 * create and displays the user interface when a player completes a level
 */

package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.gameobject.Score;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.screens.*;
import com.glhf.bomberball.utils.Constants;
//import com.sun.org.apache.bcel.internal.classfile.Constant;
//import sun.java2d.windows.GDIRenderer;

public class EndLevelUI extends MenuUI {

    private static StoryMenuScreen screen;
    private int previous_level;
    private int next_level;


    public EndLevelUI(StoryMenuScreen screen,int maze_id){
        super();
        this.setFillParent(true);
        EndLevelUI.screen = screen;
        this.previous_level = maze_id;
        this.next_level = previous_level +1;
        screen.setLevelUnlocked(next_level); // unlocks next level
        addButtons();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(Constants.PATH_GRAPHICS + "background/VictorySoloScreen.png"))));
        //this.setBackground(background);
    }

    /**
     * initializes and adds all the buttons to the interface. Also adds listeners to these buttons if needed.
     */
    private void addButtons() {

        // labels
        Label victory = new Label(Translator.translate("level succeeded", previous_level + 1), Graphics.GUI.getSkin());
        Label unlocked = new Label(Translator.translate("level unlocked", next_level + 1), Graphics.GUI.getSkin());
        Label score = new Label("Score : "+ Score.getINSTANCE().getScore(), Graphics.GUI.getSkin());
        this.add(victory).spaceBottom(Value.percentHeight(0.9f)).row();
        this.add(unlocked).spaceBottom(Value.percentHeight(1.5f)).row();
        this.add(score).spaceBottom(Value.percentHeight(0.9f)).row();

        //buttons
        Table buttons = new Table();
        TextButton next = new AudioButton(Translator.translate("Play next level"), Graphics.GUI.getSkin());
        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(next_level<8){
                    screen.nextMaze();
                    Bomberball.changeScreen(new GameStoryScreen(screen,screen.maze,screen.getMazeId()));
                }
            }
        });
        buttons.add(next).spaceTop(Value.percentHeight(0.2f)).row();

        TextButton replay_level = new AudioButton(Translator.translate("Retry this level"), Graphics.GUI.getSkin());
        replay_level.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(new GameStoryScreen(screen, Maze.importMazeSolo("maze_" + previous_level),screen.getMazeId()));
            }
        });
        buttons.add(replay_level).spaceTop(Value.percentHeight(0.2f)).row();

        TextButton back_level_button = new AudioButton(Translator.translate("Back to level selection"), Graphics.GUI.getSkin());
        back_level_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(screen);
            }
        });
        buttons.add(back_level_button).spaceTop(Value.percentHeight(0.2f)).row();

        TextButton back_menu_button = new AudioButton(Translator.translate("Back to main menu"), Graphics.GUI.getSkin());
        back_menu_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(new MainMenuScreen());
            }
        });
        buttons.add(back_menu_button).spaceTop(Value.percentHeight(0.2f)).row();
        this.add(buttons);
    }

    /**
     * getter for the StoryMenuScreen
     * @return StoryMenuScreen
     */
    public static StoryMenuScreen getUpdatedStoryScreen(){
        return screen;
    }
}
