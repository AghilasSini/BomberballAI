package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.gameobject.Player;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class PlayersInfoUI extends Table {

    public PlayersInfoUI(List<Player> players) {
        for (Player player : players) {
            PlayerWidget pw = new PlayerWidget(player);
            this.add(pw).growX().prefHeight(Value.percentHeight(1/3f, this)).space(Value.percentHeight(0.05f, this));
            this.row();
        }
        this.pad(Value.percentHeight(0.025f));
    }

    class PlayerWidget extends Table implements Observer {
        private Player player;
        private boolean previous_player_state;
        private AnimationActor player_skin;
        private PlayerInfoWidget player_info;

        public PlayerWidget(Player player) {
            TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(PATH_GRAPHICS+"background/InfoPlayer.png")));
            this.setBackground(texture);
            this.player = player;
            this.previous_player_state = player.isActive();
            player_skin = new AnimationActor(player.getAnimation());
            player_skin.setScaling(Scaling.fit);
            this.add(player_skin).grow();
            player_info = new PlayerInfoWidget(player);
            this.add(player_info).grow();
            player.addObserver(this);

            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plaindark_9patch.png"), 5, 5, 5, 5));
            this.setBackground(patch);
        }

        @Override
        public void update(Observable observable, Object o) {
            if (player.isActive() && !previous_player_state) {
                previous_player_state = true;
                player_skin.mustMove(true);
            } else if (!player.isActive() && previous_player_state) {
                previous_player_state = false;
                player_skin.mustMove(false);
            }
        }
    }

    class PlayerInfoWidget extends Table {
        private HeartsWidget player_hearts;

        public PlayerInfoWidget(Player player) {
            this.add(new BonusWidget(player)).grow();
        }
    }

    class HeartsWidget extends Table implements Observer {
        private Player player;
        private ArrayList<Image> hearts;
        public HeartsWidget(Player player) {
            this.pad(5);
            this.player = player;
            player.addObserver(this);
            this.hearts = new ArrayList<>();
            for (int i=0; i<player.getLife(); i++) {
                Image heart = new Image(Graphics.Sprites.get("ui_heart_full"));
                heart.setScaling(Scaling.fit);
                heart.setAlign(Align.left);
                hearts.add(heart);
                this.add(heart).grow().space(5);
            }
        }

        @Override
        public void update(Observable observable, Object o) {
            while (player.getLife() < hearts.size()) {
                hearts.get(hearts.size()-1).remove();
                hearts.remove(hearts.size()-1);
            }
        }
    }

    class BonusWidget extends Table implements Observer {
        private Player player;
        private Label number_moves;
        private Label number_bombs;
        private Label bomb_range;
        public BonusWidget(Player player) {
            this.pad(5);
            this.player = player;
            player.addObserver(this);
            //ajout des icones
            Image number_moves_image = new Image(Graphics.Sprites.get("speed_bonus"));
            Image number_bombs_image = new Image(Graphics.Sprites.get("bomb_bonus"));
            Image bomb_range_image = new Image(Graphics.Sprites.get("explo_bonus"));
            number_moves_image.setScaling(Scaling.fit);
            number_bombs_image.setScaling(Scaling.fit);
            bomb_range_image.setScaling(Scaling.fit);
            this.add(number_moves_image).grow();
            this.add(number_bombs_image).grow();
            this.add(bomb_range_image).grow();
            //retour ligne
            this.row();
            //ajout des nombres
            number_moves = new Label("x"+player.getNumberMoveRemaining(), Graphics.GUI.getSkin(), "black");
            number_bombs = new Label("x"+player.getNumberBombRemaining(), Graphics.GUI.getSkin(), "black");
            bomb_range = new Label("x"+player.getBombRange(), Graphics.GUI.getSkin(), "black");
            number_moves.setAlignment(Align.center);
            number_bombs.setAlignment(Align.center);
            bomb_range.setAlignment(Align.center);
            this.add(number_moves).grow();
            this.add(number_bombs).grow();
            this.add(bomb_range).grow();

            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plain_9patch.png"), 5, 5, 5, 5));
            this.setBackground(patch);
        }

        @Override
        public void update(Observable observable, Object o) {
            number_moves.setText("x"+player.getNumberMoveRemaining());
            number_bombs.setText("x"+player.getNumberBombRemaining());
            bomb_range.setText("x"+player.getBombRange());
        }
    }
}
