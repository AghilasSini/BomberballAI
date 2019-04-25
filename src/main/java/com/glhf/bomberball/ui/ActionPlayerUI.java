package com.glhf.bomberball.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.config.InputsConfig;
import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class ActionPlayerUI extends Table {

    InputsConfig config;

    /**
     * constructor
     */
    public ActionPlayerUI() {
        config  = InputsConfig.get();
        TextButton.TextButtonStyle textButton_style = new TextButton.TextButtonStyle(Graphics.GUI.getSkin().get(TextButton.TextButtonStyle.class));
        textButton_style.overFontColor = Color.WHITE;

        Table table_move= new Table();
        Label move = new Label(Translator.translate("Action"), Graphics.GUI.getSkin());
        move.setAlignment(Align.center);
        table_move.add(move).growX().row();
        table_move.add(new ActionsWidget()).growX().pad(Value.percentWidth(0.025f));

        Table table_bomb = new Table();
        Label bomb = new Label(Translator.translate("Bomb"), Graphics.GUI.getSkin());
        TextButton bomb_button = new TextButton("B", textButton_style);
        bomb.setAlignment(Align.center);
        table_bomb.add(bomb).growX().row();
        table_bomb.add(bomb_button).growX().pad(Value.percentWidth(0.025f));

        Table table_move_mode = new Table();
        Label move_mode = new Label(Translator.translate("Move"), Graphics.GUI.getSkin());
        move_mode.setAlignment(Align.center);
        TextButton move_mode_button = new TextButton("D", textButton_style);
        table_move_mode.add(move_mode).growX().row();
        table_move_mode.add(move_mode_button).growX().pad(Value.percentWidth(0.025f));


        Table table_end_turn= new Table();
        Label end_turn = new Label(Translator.translate("End Turn"), Graphics.GUI.getSkin());
        TextButton end_turn_button = new TextButton("F", textButton_style);
        end_turn.setAlignment(Align.center);
        table_end_turn.add(end_turn).growX().row();
        table_end_turn.add(end_turn_button).growX().pad(Value.percentWidth(0.025f));

        this.add(table_move).grow();
        this.add(table_bomb).grow();
        this.add(table_move_mode).grow();
        this.add(table_end_turn).grow();

        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plaindark_9patch.png"), 5, 5, 5, 5));
        this.setBackground(patch);
    }

    public class ActionsWidget extends Table {
        public ActionsWidget() {
            TextButton move_L = new TextButton(config.getInputName(Action.MOVE_LEFT), Graphics.GUI.getSkin(), "small");
            TextButton move_R = new TextButton(config.getInputName(Action.MOVE_RIGHT), Graphics.GUI.getSkin(), "small");
            TextButton move_U = new TextButton(config.getInputName(Action.MOVE_UP), Graphics.GUI.getSkin(), "small");
            TextButton move_D = new TextButton(config.getInputName(Action.MOVE_DOWN), Graphics.GUI.getSkin(), "small");
            this.add(move_L).grow();
            this.add(move_R).grow();
            this.add(move_U).grow();
            this.add(move_D).grow();
        }
    }
}
