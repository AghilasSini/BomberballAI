package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.screens.*;
import com.glhf.bomberball.utils.ScreenChangeListener;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class EditorMenuUI extends MenuUI {

    private EditorMenuScreen screen;
    private MazeDrawer maze_preview;

    public EditorMenuUI(EditorMenuScreen screen) {
        this.screen = screen;
        this.initialize();
    }

    private void initialize()
    {
        this.setFillParent(true);

        this.add(new ButtonsWidget()).growY().width(Value.percentWidth(1/3f, this));
        this.add(new MazeSelectorWidget()).grow();

        this.add(maze_preview);
    }

    public class ButtonsWidget extends  Table {

        public ButtonsWidget() {
            TextButton edit_button = new AudioButton(Translator.translate("Edit"), Graphics.GUI.getSkin());
            edit_button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Bomberball.changeScreen(new EditorScreen(screen.getMazeId()));
                }
            });

            TextButton back_button = new AudioButton(Translator.translate("Back"), Graphics.GUI.getSkin());
            back_button.addListener(new ScreenChangeListener(MainMenuScreen.class));

            Value spacing = Value.percentHeight(0.1f);

            this.add(edit_button).growX().space(spacing);
            this.row();
            this.add(new NewMazeWidget()).growX().space(spacing);
            this.row();
            this.add(back_button).expand().growX().align(Align.bottom);

            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plain_9patch.png"), 5, 5, 5, 5));
            this.setBackground(patch);
        }
    }

    public class NewMazeWidget extends Table {
        public NewMazeWidget() {

            Label width_label = new Label(Translator.translate("Width") + " : ", Graphics.GUI.getSkin());
            TextField width_field = new TextField("13", Graphics.GUI.getSkin());
            Label height_label = new Label(Translator.translate("Height") + " : ", Graphics.GUI.getSkin());
            TextField height_field = new TextField("11", Graphics.GUI.getSkin());

            TextButton new_button = new AudioButton(Translator.translate("New"), Graphics.GUI.getSkin());
            new_button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    int width = 13;
                    int height = 11;
                    try {
                        width = Integer.parseInt(width_field.getText());
                    } catch(NumberFormatException e) { }
                    try {
                        height = Integer.parseInt(height_field.getText());
                    } catch(NumberFormatException e) { }
                    width = Math.min(Math.max(3, width), 41);
                    height = Math.min(Math.max(3, height), 41);
                    Bomberball.changeScreen(new EditorScreen(new Maze(width, height)));
                }
            });

            this.add(new_button).growX().row();

            Table dimensions = new Table();
            dimensions.padLeft(Value.percentWidth(0.1f));
            dimensions.padRight(Value.percentWidth(0.1f));
            dimensions.add(width_label).growX();
            dimensions.add(width_field).row();
            dimensions.add(height_label).growX();
            dimensions.add(height_field);
            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plaindark_9patch.png"), 5, 5, 5, 5));
            dimensions.setBackground(patch);

            this.add(dimensions).growX();
        }
    }

    public class MazeSelectorWidget extends Table {

        public MazeSelectorWidget() {
            //setFillParent(true);
            this.padTop(Value.percentHeight(0.85f));

            TextButton next_map = new AudioButton(">", Graphics.GUI.getSkin());
            next_map.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    screen.nextMaze();
                    maze_preview.setMaze(screen.maze);
                }
            });

            TextButton previous_map = new AudioButton("<", Graphics.GUI.getSkin());
            previous_map.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    screen.previousMaze();
                    maze_preview.setMaze(screen.maze);
                }
            });

            maze_preview = new MazeDrawer(screen.maze, 1/3f, 1f,  0.15f, 1f, MazeDrawer.Fit.BEST);

            this.add(previous_map).growX().pad(Value.percentHeight(0.1f));
            this.add(next_map).growX().pad(Value.percentHeight(0.1f));
        }

    }
}
