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
import com.glhf.bomberball.config.GameInfiniteConfig;
import com.glhf.bomberball.gameobject.Score;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.screens.*;
import com.glhf.bomberball.utils.ScreenChangeListener;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class InfiniteMenuUI extends MenuUI {

    private final GameInfiniteConfig config;
    private InfiniteModeScreen screen;
    private int highscore;
    private Maze mazex;

    public InfiniteMenuUI(InfiniteModeScreen screen) {
        this.screen = screen;
        this.setFillParent(true);

        config = GameInfiniteConfig.get();
        mazex = screen.maze;
        highscore = config.highscore;

        this.add(new PanelWidget()).grow().width(Value.percentWidth(0.65f, this)).height(Value.percentHeight(0.60f, this));
    }

    public class PanelWidget extends Table {

        public PanelWidget() {
            this.pad(Value.percentHeight(0.05f));
            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plaindark_9patch.png"), 5, 5, 5, 5));
            this.setBackground(patch);

            //Title
            Label label = new Label(Translator.translate("Infinite Mode"), Graphics.GUI.getSkin(), "Title");
            label.setAlignment(Align.center);
            label.setFontScale(1.7f, 1.7f);

            Skin skin = Graphics.GUI.getSkin();

            //CheckBoxes
            Table checkboxes = new Table();
            CheckBox box1;
            CheckBox box2;
            CheckBox box3;
            box1 = new CheckBox(Translator.translate("No bonus"), skin);
            box2 = new CheckBox(Translator.translate("Limited turns"), skin);
            box3 = new CheckBox(Translator.translate("No crates"), skin);
            box1.setChecked(!config.bonus_activated);
            box2.setChecked(config.finite_number_turn);
            box3.setChecked(!config.destructible_wall_available);

            //Settings CheckBoxes
            box1.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameInfiniteConfig config = GameInfiniteConfig.get();
                    config.bonus_activated = !((CheckBox)actor).isChecked();
                    config.exportConfig();
                }
            });

            box2.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameInfiniteConfig config = GameInfiniteConfig.get();
                    config.finite_number_turn = ((CheckBox)actor).isChecked();
                    config.exportConfig();
                }
            });

            box3.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameInfiniteConfig config = GameInfiniteConfig.get();
                    config.destructible_wall_available = !((CheckBox)actor).isChecked();
                    config.exportConfig();
                }
            });
            checkboxes.add(box1).grow().align(Align.left).space(Value.percentHeight(0.05f)).row();
            checkboxes.add(box2).grow().align(Align.left).space(Value.percentHeight(0.05f)).row();
            checkboxes.add(box3).grow().align(Align.left);
            patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plain_9patch.png"), 5, 5, 5, 5));
            checkboxes.setBackground(patch);

            //Other buttons
            TextButton back;
            back = new AudioButton(Translator.translate("Back"), skin);
            //back.addListener(new ScreenChangeListener(SoloMenuScreen.class));

            TextButton play;
            play = new AudioButton(Translator.translate("Play"), Graphics.GUI.getSkin());
            play.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Score.getINSTANCE().resetScore();
                    Bomberball.changeScreen(new GameInfiniteScreen(1));
                }
            });


            //Current Highscore
            Label lab;
            lab = new Label(Translator.translate("Highscore") + " : " + highscore, skin, "default");
            lab.setAlignment(Align.center);

            Table bottom = new Table();
            bottom.add(back).growX();
            bottom.add(play).growX();
            bottom.add(lab).growX();

            //Adding to the screen
            this.add(label).growX().space(Value.percentHeight(0.05f)).row();
            this.add(checkboxes).growY().space(Value.percentHeight(0.05f)).row();
            this.add(bottom).growX();
        }

    }
}
