package com.glhf.bomberball.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.InputHandler;
import com.glhf.bomberball.utils.Action;

public abstract class AbstractScreen implements Screen {

    protected InputHandler input_handler;
    private Stage stage;

    public AbstractScreen() {
        input_handler = new InputHandler();
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.setDebugAll(Bomberball.debug);
        stage.addListener(input_handler);
        registerActionsHandlers();
    }

    protected void registerActionsHandlers() {
        input_handler.registerActionHandler(Action.MENU_GO_BACK, () -> Bomberball.changeScreen(new MainMenuScreen()));
    }

    public void addUI(Actor ui) {
        stage.addActor(ui);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(34/255f, 34/255f, 34/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Graphics.GUI.scaleFont();
    }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
