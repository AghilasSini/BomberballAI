package com.glhf.bomberball.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.gameobject.NumberTurn;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.gameobject.Score;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameUI extends Table {

    public GameUI(List<Player> players, boolean show_score, boolean show_turn) {
        setup(players, show_score, show_turn);
    }

    public GameUI(Player player, boolean show_score, boolean show_turn) {
        List<Player> players = new ArrayList<Player>();
        players.add(player);
        setup(players, show_score, show_turn);
    }

    public void setup(List<Player> players, boolean show_score, boolean show_turn) {
        this.setFillParent(true);

        ScoreUI score_ui = new ScoreUI();
        TurnUI turn_ui = new TurnUI();
        PlayersInfoUI left_ui = new PlayersInfoUI(players);
        ActionPlayerUI bottom_ui = new ActionPlayerUI();

        if (show_score) {
            this.add(score_ui).growX().width(Value.percentWidth(1/3f,this)).height(Value.percentHeight(0.1f, this)).align(Align.left);
            this.row();
        }
        if (show_turn) {
            this.add(turn_ui).growX().width(Value.percentWidth(1/3f,this)).height(Value.percentHeight(0.1f, this)).align(Align.left);
            this.row();
        }
        this.add(left_ui).grow().width(Value.percentWidth(1/3f,this)).align(Align.left);
        this.row();
        this.add(bottom_ui).growX().height(Value.percentHeight(0.20f, this)).align(Align.bottom);
    }

    class ScoreUI extends Table implements Observer {
        private final Score sc;
        private Label label;

        public ScoreUI() {
            super();
            label = new Label("Score : ", Graphics.GUI.getSkin());
            label.setAlignment(Align.center);
            sc = Score.getINSTANCE();
            sc.addObserver(this);
            add(label).growX();
            update(null, null);
        }

        @Override
        public void update(Observable observable, Object o) {
            label.setText("Score : " + sc.getScore());
        }
    }

    class TurnUI extends Table implements Observer {
        private NumberTurn turn_number;
        private Label label;

        public TurnUI() {
            super();
            label = new Label("", Graphics.GUI.getSkin());
            label.setAlignment(Align.center);
            turn_number = NumberTurn.getInstance();
            turn_number.addObserver(this);
            add(label).growX();
            update(null, null);
        }

        @Override
        public void update(Observable observable, Object o) {
            label.setText(Translator.translate("Turns left") + " : " + turn_number.getNbTurn());
        }
    }
}
