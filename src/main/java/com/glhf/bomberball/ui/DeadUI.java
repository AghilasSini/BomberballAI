/**
 * @author : Rémy
 * creates and displays the user interface when a player dies
 */
package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.screens.GameStoryScreen;
import com.glhf.bomberball.screens.StoryMenuScreen;

public class DeadUI extends MenuUI {

    private StoryMenuScreen screen;
    private int maze_id;

    public DeadUI(StoryMenuScreen screen, int maze_id) {
        this.setFillParent(true);
        this.screen = screen;
        this.maze_id = maze_id;
        addButtons();
    }

    /**
     * initializes and adds all the buttons to the ui. Also adds listeners to these buttons if necessary.
     */
    private void addButtons() {

        AnimationActor player_animation = new AnimationActor(new Animation<>(0.15f, Graphics.Anims.get("mort/idle"), Animation.PlayMode.LOOP));
        player_animation.mustMove(true);
        this.add(player_animation).grow().row();

        // Buttons
        Label dead = new Label(Translator.translate("Wasted !"), Graphics.GUI.getSkin());
        dead.setFontScale(2f,2f);
        dead.setColor(Color.RED);
        this .add(dead).spaceBottom(Value.percentHeight(0.9f)).row();

        TextButton replay = new AudioButton(Translator.translate("Retry this level"), Graphics.GUI.getSkin());
        replay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(new GameStoryScreen(screen, Maze.importMazeSolo("maze_" + maze_id),screen.getMazeId()));
            }
        });
        this.add(replay).spaceTop(Value.percentHeight(0.9f)).row();

        TextButton back_story_menu = new AudioButton(Translator.translate("Back to level selection"), Graphics.GUI.getSkin());
        back_story_menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Bomberball.changeScreen(screen);
            }
        });
        this.add(back_story_menu).spaceTop(Value.percentHeight(0.9f)).row();

        TextButton ragequit = new AudioButton(Translator.translate("Quit"), Graphics.GUI.getSkin());
        ragequit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        this.add(ragequit).spaceTop(Value.percentHeight(0.9f)).row();

    }
}
