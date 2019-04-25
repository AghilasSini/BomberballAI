package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public abstract class MenuUI extends Table {
    public MenuUI () {
        super();
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture(PATH_GRAPHICS+"background/WallMenu.png")));
        //this.setBackground(texture);
    }
}
