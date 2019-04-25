/**
 * @author : Rémy
 * creates the interface of the story mode (level selection) */
package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.screens.*;
import com.glhf.bomberball.utils.Constants;
import com.glhf.bomberball.utils.ScreenChangeListener;

public class StoryMenuUI extends MenuUI {

    private Table level_selection;
    private TextButton[] levels;
    private MazeDrawer level_preview;
    private StoryMenuScreen screen;

    public StoryMenuUI(StoryMenuScreen screen) {

        super();
        this.screen = screen;
        this.setFillParent(true);
        level_selection = new Table();
        addButtons();
        level_preview = new MazeDrawer(screen.maze, 0.25f, 0.75f, 0.5f, 0.88f, MazeDrawer.Fit.BEST);
        this.add(level_preview);
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(Constants.PATH_GRAPHICS+"/background/SoloMenu.png"))));
        this.setBackground(texture);
    }

    /**
     * initializes and adds all the buttons to the table. Also adds listeners to buttons if necessary
     */
    private void addButtons() {

        // Title

        Label label = new Label(Translator.translate("Level Selection"), Graphics.GUI.getSkin(), "Title");
        label.setAlignment(Align.center);
        label.setFontScale(1.7f, 1.7f);
        this.add(label).row();

        // Buttons to select the level

        TextButton previous_level_button = new AudioButton("<", Graphics.GUI.getSkin());
        previous_level_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.previousMaze();
                level_preview.setMaze(screen.maze);
                highlightLevel(screen.getMazeId());
            }
        });
        level_selection.add(previous_level_button);

        TextButton next_level_button = new AudioButton(">", Graphics.GUI.getSkin());
        next_level_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.nextMaze();
                level_preview.setMaze(screen.maze);
                highlightLevel(screen.getMazeId());
            }
        });
        level_selection.add(next_level_button).spaceLeft(Value.percentHeight(8f));

        this.add(level_selection).align(Align.center).grow().row();

        Table buttons = new Table();

        // Available levels

        int nb_levels = screen.getMazeCount();
        levels = new TextButton[nb_levels];
        Table levels_group = new Table();
        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);

        for (int i = 0; i < nb_levels; i++) {
            levels[i] = new AudioButton(Integer.toString(i + 1), Graphics.GUI.getSkin(), "checkable");
            buttonGroup.add(levels[i]);
            int finalI = i;
            levels[i].setDisabled(!screen.isLevelUnlocked(i));
            levels[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    screen.getMaze(finalI);
                    highlightLevel(screen.getMazeId());
                    level_preview.setMaze(screen.maze);
                }
            });
            levels_group.add(levels[i]).growX().pad(Value.percentWidth(0.05f));
        }
        levels[screen.getLastLevelPlayed()].setChecked(true);
        buttons.add(levels_group).width(Value.percentWidth(0.8f, this)).row();
        // Play and quit buttons

        TextButton play_button = new AudioButton(Translator.translate("Play Selected Level"), Graphics.GUI.getSkin());
        play_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                play_button.setChecked(false);
                if (screen.isLevelUnlocked(screen.getMazeId())) { // allows to play the level only if it's unlocked
                    Bomberball.changeScreen(new GameStoryScreen(screen, screen.maze, screen.getMazeId()));
                }
            }
        });
        buttons.add(play_button).row();

        TextButton back_button = new AudioButton(Translator.translate("Back to main menu"), Graphics.GUI.getSkin());
        back_button.addListener(new ScreenChangeListener(MainMenuScreen.class));
        back_button.getLabel().setFontScale(0.8f, 0.8f);
        buttons.add(back_button).spaceTop(Value.percentHeight(0.9f));
        this.add(buttons);
    }

    /**
     * changes the texture and allows preview of the level when the button is clicked
     * @param i the id of the level that will be unlocked
     */
    public void unlockLevel(int i) {
        levels[i].setDisabled(false);
    }

    public void highlightLevel(int j){
        levels[j].setChecked(true);
    }
}


