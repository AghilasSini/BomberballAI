package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.gameobject.NumberTurn;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.screens.GameMultiScreen;
import com.glhf.bomberball.screens.MainMenuScreen;
import com.glhf.bomberball.screens.MultiMenuScreen;
import com.glhf.bomberball.ui.GameUI.TurnUI;
import com.glhf.bomberball.utils.ScreenChangeListener;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

import java.util.ArrayList;


public class VictoryMenuUI extends MenuUI {
    private int previous_maze_id;
    private boolean repeat;

    public VictoryMenuUI(Player player, int maze_id) {
        this.previous_maze_id = maze_id;
        this.setFillParent(true);
        this.padLeft(Value.percentWidth(0.35f));
        this.padRight(Value.percentWidth(0.35f));
        this.padTop(Value.percentHeight(0.1f));
        this.padBottom(Value.percentHeight(0.1f));

        if (player == null) {
            this.add(new Label(Translator.translate("draw ..."), Graphics.GUI.getSkin(), "default")).row();
            TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(PATH_GRAPHICS+"background/EqualityMenu.png")));
            this.setBackground(texture);
        } else {
            this.add(new Label(Translator.translate("VICTORY!")+"\n", Graphics.GUI.getSkin(), "default")).row();
            if (player instanceof AbstractAI) {
                this.add(new Label(((AbstractAI) player).getPlayerName()+" (ID "+player.getPlayerId()+", remaining turns "+NumberTurn.getInstance().getNbTurn()+")", Graphics.GUI.getSkin(), "default")).row();
            }
            else {
            	this.add(new Label(Translator.translate("Human player")+" (ID "+player.getPlayerId()+")", Graphics.GUI.getSkin(), "default")).row();
            }
            AnimationActor player_animation = new AnimationActor(player.getAnimation());
            player_animation.mustMove(true);
            this.add(player_animation).grow().row();
            TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(PATH_GRAPHICS+"background/VictoryMenu.png")));
            this.setBackground(texture);
        }

        addButtons();
    }

    public VictoryMenuUI(Player player, int maze_id, boolean repeat) {
    			this(player,maze_id);
    	        this.repeat=repeat;
       	}

	private void addButtons() {
        TextButton b;
        Skin skin = Graphics.GUI.getSkin();

        Value spacing = Value.percentHeight(0.20f);

        b = new AudioButton(Translator.translate("Replay"), skin);
        b.addListener(new ChangeListener() {
            @Override
           
            public void changed(ChangeEvent event, Actor actor) {
            	if (!repeat) {
				Bomberball.changeScreen(new GameMultiScreen(Maze.importMazeMulti("maze_" + previous_maze_id), previous_maze_id));
            	}else {
            	Bomberball.reinitialize();
            		
            	}
            }
        });
        this.add(b).growX().space(spacing).row();

        
//        b = new AudioButton(Translator.translate("Multiplayer menu"), skin);
//        b.addListener(new ScreenChangeListener(MultiMenuScreen.class));
//        this.add(b).growX().space(spacing).row();
//
//        b = new AudioButton(Translator.translate("Back to main menu"), skin);
//        b.addListener(new ScreenChangeListener(MainMenuScreen.class));
//        this.add(b).growX();
    }
}
