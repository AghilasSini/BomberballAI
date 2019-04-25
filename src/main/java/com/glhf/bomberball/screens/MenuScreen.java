package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;

public abstract class MenuScreen extends AbstractScreen {

    public MenuScreen() {

    }

    /**@author: Jyra
     * On check si la musique est déjà joué et si non on la lance
     */
    @Override
    public void show() {
        super.show();
        Audio.MAIN_MENU.playMusique();
    }
}