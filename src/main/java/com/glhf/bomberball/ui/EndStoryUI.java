/**
 * @author : Rémy
 * creates the interface when a player completed the whole story mode
 */
package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.screens.GameStoryScreen;
import com.glhf.bomberball.screens.MainMenuScreen;
import com.glhf.bomberball.screens.StoryMenuScreen;
import com.glhf.bomberball.utils.Constants;

public class EndStoryUI extends MenuUI {

    private StoryMenuScreen screen;
    private int last_level;

    public EndStoryUI(StoryMenuScreen screen,int maze_id){

        this.setFillParent(true);
        this.screen = screen;
        this.last_level = maze_id;
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(Constants.PATH_GRAPHICS + "background/VictorySoloScreen.png"))));
        //this.setBackground(background);
        addButtons();
    }

    /**
     * initializes and adds the buttons to the interface. Also adds listeners to these buttons if necessary.
     */
    private void addButtons() {

        //Labels
        Label congrats = new Label(Translator.translate("story mode congratulations"), Graphics.GUI.getSkin(), "Title");
        Label do_next = new Label(Translator.translate("All levels unlocked !"), Graphics.GUI.getSkin());
        this.add(congrats).spaceBottom(Value.percentHeight(0.9f)).row();
        this.add(do_next).spaceBottom(Value.percentHeight(0.9f)).row();

        //TextButtons
        TextButton replay_level = new AudioButton(Translator.translate("Replay last level"), Graphics.GUI.getSkin());
        replay_level.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(new GameStoryScreen(screen,Maze.importMazeSolo("maze_" + last_level),screen.getMazeId()));
        }
        });
        this.add(replay_level).spaceTop(Value.percentHeight(0.9f)).row();

        TextButton back_story_menu = new AudioButton(Translator.translate("Back to level selection"), Graphics.GUI.getSkin());
        back_story_menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(screen);
            }
        });
        this.add(back_story_menu).spaceTop(Value.percentHeight(0.9f)).row();

        TextButton back_main_menu = new AudioButton(Translator.translate("Back to main menu"), Graphics.GUI.getSkin());
        back_main_menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(new MainMenuScreen());
            }
        });
        this.add(back_main_menu).spaceTop(Value.percentHeight(0.9f)).row();


    }


}
