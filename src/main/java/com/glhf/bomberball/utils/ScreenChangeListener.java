package com.glhf.bomberball.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.screens.AbstractScreen;

public class ScreenChangeListener extends ChangeListener {
    Class<? extends AbstractScreen> screen_class;

    public <T extends AbstractScreen> ScreenChangeListener(Class<T> screen_class) {
        this.screen_class = screen_class;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        try {
            Bomberball.changeScreen(screen_class.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}