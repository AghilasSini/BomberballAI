package com.glhf.bomberball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.glhf.bomberball.utils.Constants;

import java.util.HashMap;

public class Graphics {

    public static class Sprites {
        private static TextureAtlas sprites_atlasTexture;
        private static HashMap<String, AtlasRegion> sprites_atlasRegions;

        private static void load()
        {
        	//System.out.println("***************"+Gdx.files.external(Constants.PATH_ATLAS_SPRITES)+"+++++++++++++++");
        	
        	
        	
        	
        	
            sprites_atlasTexture = new TextureAtlas(Gdx.files.internal(Constants.PATH_ATLAS_SPRITES));
            
            sprites_atlasRegions = new HashMap<>();
            for (AtlasRegion atlasRegion : sprites_atlasTexture.getRegions()) {
                sprites_atlasRegions.put(atlasRegion.name, atlasRegion);
                System.out.println("Sprite " + atlasRegion.name + " loaded");
            }
            System.out.println(sprites_atlasRegions.size() + " sprites succesfully loaded");
        }

        public static AtlasRegion get(String sprite_str)
        {
            if (!sprites_atlasRegions.containsKey(sprite_str)) {
                throw new RuntimeException("Sprite " + sprite_str + " doesn't exists");
            }
            return sprites_atlasRegions.get(sprite_str);
        }
    }

    public static class Anims {
        private static TextureAtlas anim_atlasTexture;
        private static HashMap<String, Array<AtlasRegion>> anim_atlasRegions;

        private static void load()
        {
            anim_atlasTexture = new TextureAtlas(Gdx.files.internal(Constants.PATH_ATLAS_ANIMS));
            anim_atlasRegions = new HashMap<>();
            for (AtlasRegion atlasRegion : anim_atlasTexture.getRegions()) {
                if (!anim_atlasRegions.containsKey(atlasRegion.name)) {
                    anim_atlasRegions.put(atlasRegion.name, anim_atlasTexture.findRegions(atlasRegion.name));
                    System.out.println("Animation " + atlasRegion.name + " loaded");
                }
            }
            System.out.println(anim_atlasRegions.size() + " animations succesfully loaded");
        }

        public static Array<AtlasRegion> get(String anim_str)
        {
            if (!anim_atlasRegions.containsKey(anim_str)) {
                throw new RuntimeException("Animation " + anim_str + " doesn't exists");
            }
            return anim_atlasRegions.get(anim_str);
        }
    }

    public static class GUI {
        private static Skin skin;
        private static float font_size = 1;

        public static Skin getSkin(){
            return skin;
        }

        public static void scaleFont() {
            for (ObjectMap.Entry<String, BitmapFont> f : skin.getAll(BitmapFont.class)) {
                f.value.getData().setScale(font_size);
            }
        }

        private static void load() {

            skin = new Skin();

            //
            Texture white = new Texture(new Pixmap(1,1, Format.RGB888));
            skin.add("white", white);
            Texture transparent = new Texture(new Pixmap(1,1, Format.RGBA8888));
            skin.add("transparent", transparent);
            skin.add("bomb", new TextureRegionDrawable(Sprites.get("bomb")));

            TextureAtlas gui_atlasTexture = new TextureAtlas(Gdx.files.internal(Constants.PATH_ATLAS_GUI));
            int i=0;
            for (AtlasRegion atlasRegion : gui_atlasTexture.getRegions()) {
                if(atlasRegion.name.matches(".*9patch.*"))
                    skin.add(atlasRegion.name, new NinePatchDrawable(new NinePatch(atlasRegion,16,16,16,16)), Drawable.class);
                else
                    skin.add(atlasRegion.name, new TextureRegionDrawable(atlasRegion), Drawable.class);
                System.out.println("GUI element " + atlasRegion.name + " loaded");
                i++;
            }
            System.out.println(i + " gui elements succesfully loaded");

            skin.add("default", Color.WHITE);

            /* Génération de la BitmapFont avec FreeTypeFontGenerator */
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.PATH_FONTS + "Compass/CompassPro.ttf"));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = 32;
            BitmapFont font = generator.generateFont(parameter);
            skin.add("small", font);
            parameter.size = 26;
            font = generator.generateFont(parameter);
            skin.add("very_small", font);
            parameter.size = 38;
            font = generator.generateFont(parameter);
            skin.add("default", font);
            generator.dispose();

            //==========TextButtonStyle
            Drawable patch = skin.getDrawable("rockdark_long_9patch");
            Drawable patch2 = skin.getDrawable("rockbright_long_9patch");
            Drawable patch3 = skin.getDrawable("rockselected_long_9patch");
            TextButtonStyle textButtonStyle = new TextButtonStyle(patch, patch, patch, skin.getFont("default"));
            textButtonStyle.fontColor = Color.WHITE;
            textButtonStyle.overFontColor = Color.GRAY;
            //textButtonStyle.downFontColor = Color.RED;
            textButtonStyle.disabled = patch2;
            skin.add("default", textButtonStyle);

            textButtonStyle = new TextButtonStyle(textButtonStyle);//copy of textButtonStyle
            textButtonStyle.font = skin.getFont("small");
            skin.add("small", textButtonStyle);

            textButtonStyle = new TextButtonStyle(textButtonStyle);//copy of textButtonStyle
            textButtonStyle.font = skin.getFont("very_small");
            skin.add("input_select", textButtonStyle);

            patch = skin.getDrawable("rockdark_9patch");
            patch2 = skin.getDrawable("rockbright_9patch");
            patch3 = skin.getDrawable("rockselected_9patch");
            textButtonStyle = new TextButtonStyle(patch, patch, patch, skin.getFont("default"));
            textButtonStyle.checked = patch3;
            textButtonStyle.disabled = patch2;
            textButtonStyle.font = skin.getFont("default");
            skin.add("checkable", textButtonStyle);

            textButtonStyle = new TextButtonStyle(patch, patch, patch, skin.getFont("default"));
            textButtonStyle.font = skin.getFont("default");
            skin.add("small", textButtonStyle);

            //========LabelStyle
            LabelStyle labelStyle = new LabelStyle();
            labelStyle.font = font;
            skin.add("default", labelStyle);

            labelStyle = new LabelStyle();
            labelStyle.font = font;
            labelStyle.fontColor = Color.BLACK;
            skin.add("black", labelStyle);

            labelStyle = new LabelStyle();
            labelStyle.font = skin.getFont("small");
            skin.add("small", labelStyle);

            labelStyle = new LabelStyle();
            labelStyle.font = skin.getFont("very_small");
            skin.add("very_small", labelStyle);

            labelStyle = new LabelStyle();
            labelStyle.font = font;
            labelStyle.fontColor = Color.WHITE;
            skin.add("Title", labelStyle);

            //=======TextFieldStyle
            TextFieldStyle textFieldStyle = new TextFieldStyle();
            textFieldStyle.font = font;
            textFieldStyle.fontColor = Color.WHITE;
            textFieldStyle.focusedFontColor = Color.RED;
            skin.add("default", textFieldStyle);

            //=======WindowStyle
            WindowStyle windowStyle = new WindowStyle(font, Color.WHITE, skin.getDrawable("white"));
            skin.add("default", windowStyle);

            //=======SliderStyle
            SliderStyle sliderStyle = new SliderStyle();
            sliderStyle.knob = skin.get("bomb", TextureRegionDrawable.class);
            sliderStyle.background = skin.getDrawable("white");
            skin.add("default-horizontal", sliderStyle);

            //=======ListStyle
            ListStyle listStyle = new ListStyle();
            listStyle.font = font;
            listStyle.fontColorSelected = Color.WHITE;
            listStyle.fontColorUnselected = Color.RED;
            listStyle.selection = skin.get("bomb", TextureRegionDrawable.class);
            listStyle.background = skin.getDrawable("white");
            skin.add("default", listStyle);

            //=======ScrollPaneStyle
            ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
            scrollStyle.background = skin.getDrawable("white");
            scrollStyle.hScroll = skin.getDrawable("white");
            scrollStyle.hScrollKnob = skin.get("bomb", TextureRegionDrawable.class);
            scrollStyle.vScroll = skin.getDrawable("white");
            scrollStyle.vScrollKnob = skin.get("bomb", TextureRegionDrawable.class);
            skin.add("default", scrollStyle);

            //========SelectBoxStyle
            SelectBoxStyle selectBoxStyle = new SelectBoxStyle();
            selectBoxStyle.font = font;
            selectBoxStyle.fontColor = Color.BLUE;
            selectBoxStyle.background = skin.getDrawable("transparent");
            selectBoxStyle.listStyle = skin.get(ListStyle.class);
            selectBoxStyle.scrollStyle = skin.get(ScrollPaneStyle.class);
            skin.add("default", selectBoxStyle);

            //=======CheckBoxStyle
            CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
            checkBoxStyle.checkboxOff = skin.getDrawable("checkboxOff");
            checkBoxStyle.checkboxOn = skin.getDrawable("checkboxOn");
            checkBoxStyle.font = skin.getFont("default");
            checkBoxStyle.fontColor = Color.BLACK;
            skin.add("default", checkBoxStyle);
        }
    }


    public static void load() {
        Graphics.Sprites.load();
        Graphics.Anims.load();
        Graphics.GUI.load();
    }

}
