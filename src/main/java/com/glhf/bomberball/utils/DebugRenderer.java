package com.glhf.bomberball.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/*
    class DebugRenderer

    Classe permettant d'afficher des éléments de debug sur la fenêtre
*/
public class DebugRenderer {

    private float width;
    private float height;
    private ShapeRenderer shapeRenderer;

    public DebugRenderer()
    {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        shapeRenderer = new ShapeRenderer();
    }

    // Affiche n lignes verticales et n lignes horizontales
    public void drawLines(int n)
    {
        float dh = height / n;
        float dw = width / n;
        for (int y = 1; y < n; y++)
            DrawDebugLine(0, y * dh, width, y * dh);
        for (int x = 1; x < n; x++)
            DrawDebugLine(x * dw, 0, x * dw, height);
    }

    private void DrawDebugLine(float x_0, float y_0, float x_1, float y_1)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE); // Red line
        shapeRenderer.line(x_0, y_0, x_1, y_1);
        shapeRenderer.end();
    }
}
