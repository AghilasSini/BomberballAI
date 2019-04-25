/**
 * @author : Rémy
 * creates the user interface for the title menu. Displays all the features available in the game.
 */
package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.screens.*;
import com.glhf.bomberball.utils.ScreenChangeListener;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class MainMenuUI extends MenuUI {

    public MainMenuUI() {
        this.setFillParent(true);
        this.padLeft(Value.percentWidth(0.33f));
        this.padRight(Value.percentWidth(0.33f));
        this.padTop(Value.percentHeight(0.1f));
        this.padBottom(Value.percentHeight(0.1f));
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(PATH_GRAPHICS+"background/MainMenu01.png")));
        this.setBackground(texture);

       addButtons();
    }

    /**
     * creates and adds buttons to the interface. Also adds listeners to these buttons if necessary.
     */
    private void addButtons()
    {
        TextButton b;
        Skin skin = Graphics.GUI.getSkin();

        Value spacing = Value.percentHeight(0.15f);

        Table buttons = new Table();
        buttons.pad(Value.percentWidth(0.1f));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(PATH_GRAPHICS+"background/scroll.png"))));
        buttons.setBackground(background);

//        b = new AudioButton(Translator.translate("Solo"), skin);
//        b.addListener(new ScreenChangeListener(SoloMenuScreen.class));
//        buttons.add(b).growX().space(spacing).row();

        b = new AudioButton(Translator.translate("Multiplayer"), skin);
        b.addListener(new ScreenChangeListener(MultiMenuScreen.class));
        buttons.add(b).growX().space(spacing).row();

//        b = new AudioButton(Translator.translate("Map Editor"), skin);
//        b.addListener(new ScreenChangeListener(EditorMenuScreen.class));
//        buttons.add(b).growX().space(spacing).row();
//
//        b = new AudioButton(Translator.translate("Settings"), skin);
//        b.addListener(new ScreenChangeListener(SettingsMenuScreen.class));
//        buttons.add(b).growX().space(spacing).row();
//
//        b = new AudioButton(Translator.translate("Quit"), skin);
//        b.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.exit();
//            }
//        });
        buttons.add(b).growX();

        this.add(buttons).grow();
    }
}
