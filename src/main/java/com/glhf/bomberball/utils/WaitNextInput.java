package com.glhf.bomberball.utils;

import com.badlogic.gdx.InputProcessor;
import com.glhf.bomberball.config.InputsConfig;
import com.glhf.bomberball.screens.SettingsMenuScreen;

public class WaitNextInput implements InputProcessor {
    private SettingsMenuScreen screen;
    public WaitNextInput(SettingsMenuScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        String id = InputsConfig.getIDForKeyCode(keycode);
        screen.setIdReceived(id);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        String id = InputsConfig.getIDForMouseButtonCode(button);
        screen.setIdReceived(id);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
