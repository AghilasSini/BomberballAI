package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Graphics.Anims;
import com.glhf.bomberball.Graphics.GUI;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.screens.StoryTellingScreen;
import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class StoryTellingUI extends Table {
    private final Label label;
    private int chapter = 1;
    private StoryTellingScreen screen;

    public StoryTellingUI(StoryTellingScreen screen) {
        this.screen = screen;
        setFillParent(true);
        label = new Label(Translator.translate("story"+chapter), Graphics.GUI.getSkin(), "small");
        label.setAlignment(Align.center);
        add(label).grow();

        Skin skin = GUI.getSkin();
        Table characters = new Table();
        characters.setFillParent(true);
        characters.align(Align.top);
        Value spacing = Value.percentWidth(0.05f);
        characters.add(new CharacterUI("knight_m", Translator.translate("The hero"), "Maximilien"))
                .height(Value.percentHeight(0.3f, this)).pad(spacing);
        characters.add(new CharacterUI("black_knight", Translator.translate("The bad guy"), "Plétor"))
                .height(Value.percentHeight(0.3f, this)).pad(spacing);
        characters.add(new CharacterUI("wizzard_m", Translator.translate("The magician"), "Bernard"))
                .height(Value.percentHeight(0.3f, this)).pad(spacing);
        characters.add(new CharacterUI("elf_f", Translator.translate("The princess"), "Bianca"))
                .height(Value.percentHeight(0.3f, this));
        addActor(characters);

        Table buttons = new Table();
        buttons.setFillParent(true);
        buttons.align(Align.bottom);
        TextButton skip = new AudioButton(Translator.translate("Skip"), skin);
        skip.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.endStory();
            }
        });
        skip.align(Align.bottomLeft);
        buttons.add(skip);
        buttons.add().growX();
        TextButton next_chapter = new AudioButton(Translator.translate("Next chapter"), skin);
        next_chapter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.continueStory();
            }
        });
        next_chapter.align(Align.bottomRight);
        buttons.add(next_chapter);
        addActor(buttons);
    }

    public void continueStory() {
        label.setText(Translator.translate("story"+screen.chapter));
    }

    private class CharacterUI extends Table{
        public CharacterUI(String skin_name, String titre, String nom) {
            Skin skin = GUI.getSkin();
            Image image = new Image(Anims.get(skin_name + "/idle").first());
            image.setScaling(Scaling.fit);
            image.setAlign(Align.center);

            add(image).grow().height(100).row();
            add(new Label(titre, skin,"black")).row();
            add(new Label(nom, skin,"black")).row();

            NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plain_9patch.png"), 5, 5, 5, 5));
            this.setBackground(patch);
        }
    }
}
