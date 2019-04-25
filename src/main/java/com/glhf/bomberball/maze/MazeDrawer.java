package com.glhf.bomberball.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.gameobject.Character;
import com.glhf.bomberball.maze.cell.CellEffect;
import com.glhf.bomberball.utils.Constants;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.VectorInt2;

import java.util.ArrayList;

/**
 * Class MazeDrawer
 * View of the class Maze model
 * Use it to draw a Maze on the screen
 */
public class MazeDrawer extends Actor {

    /**
     * Fit mode when drawing in the specified bounds
     */
    public enum Fit {
        /** Fit.WIDTH stretches the maze to the specified width */
        WIDTH,
        /** Fit.HEIGHT stretches the maze to the specified height */
        HEIGHT,
        /** Fit.BEST fully bounds the maze in the specified bounds */
        BEST
    }

    private Maze maze;
    private Batch batch;
    private int maze_width;
    private int maze_height;
    private OrthographicCamera camera;

    private int x_padding = 1;
    private int y_padding = 2;

    private float w_minp;
    private float w_maxp;
    private float h_minp;
    private float h_maxp;

    private Fit fit;

    /**
     * Constructor
     * @param maze Maze to draw
     * @param w_minp lower width bound in the screen (in percent)
     * @param w_maxp upper width bound in the screen (in percent)
     * @param h_minp lower height bound in the screen (in percent)
     * @param h_maxp upper height bound in the screen (in percent)
     * @param fit Fitting mode
     */
    public MazeDrawer(Maze maze, float w_minp, float w_maxp, float h_minp, float h_maxp, Fit fit)
    {
        this.maze = maze;

        this.w_minp = w_minp;
        this.w_maxp = w_maxp;
        this.h_minp = h_minp;
        this.h_maxp = h_maxp;
        this.fit = fit;

        maze_width = maze.getWidth();
        maze_height = maze.getHeight();

        updateView(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Setups the camera to draw the maze in the specified bounds.
     */
    public void updateView(int width, int height)
    {
        float dw = w_maxp - w_minp;
        float dh = h_maxp - h_minp;

        float maze_width_px = Constants.BOX_WIDTH * (maze.getWidth() + 2 * x_padding);
        float maze_height_px = Constants.BOX_HEIGHT * (maze.getHeight() + 2 * y_padding);
        float maze_aspect_ratio = maze_width_px / maze_height_px;

        float screen_aspect_ratio = (float) width / (float) height;

        float r = screen_aspect_ratio / maze_aspect_ratio;
        float width_scaling = 1 / dw;
        float height_scaling = 1 / dh;
        float cam_height;
        float cam_width;
        float cam_x_offset = 0f;
        float cam_y_offset = 0f;

        if (fit == Fit.WIDTH || (fit == Fit.BEST && r < 1)) {
            cam_width = maze_width_px * width_scaling;
            cam_height = cam_width * (1 / screen_aspect_ratio);
            cam_y_offset = ((cam_height * dh) - (maze_height_px * height_scaling * dh)) / 2f;
        } else {
            cam_height = maze_height_px * height_scaling;
            cam_width = cam_height * screen_aspect_ratio;
            cam_x_offset = ((cam_width * dw) - (maze_width_px * width_scaling * dw)) / 2f;
        }

        camera = new OrthographicCamera(cam_width, cam_height);
        camera.translate((cam_width * (0.5f - w_minp)) - cam_x_offset, (cam_height * (0.5f - h_minp)) - cam_y_offset);
        camera.update();
        //batch.setProjectionMatrix(camera.combined);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        maze_width = maze.getWidth();
        maze_height = maze.getHeight();
        updateView(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.batch = batch;
        Matrix4 tmp = batch.getProjectionMatrix();
        batch.setProjectionMatrix(camera.combined);
        drawFloor();
        drawBackWall();
        drawCells();
        drawSideWalls();
        drawFrontWall();
        batch.setProjectionMatrix(tmp);
    }

    public float getScale() {
        return camera.zoom;
    }

    private void drawCells() {
        for(int y = maze_height - 1; y >= 0; y--) {
            for (int x = 0; x < maze_width; x++) {
                drawCell(maze.getCellAt(x, y));
            }
        }
    }

    private void drawCell(Cell cell)
    {
        boolean thereIsADoor = false;
        for(Door door : cell.getInstancesOf(Door.class)){
            drawTextureInCell(door.getSprite(), cell.getX(), cell.getY());
            thereIsADoor = true;
        }
        CellEffect cell_effect = cell.getCellEffect();
        if (cell_effect != null) {
            batch.setColor(cell_effect.getColor());
            drawTextureInCell(cell_effect.getSprite(), cell.getX(), cell.getY());
            batch.setColor(Color.WHITE);
        }

        ArrayList<GameObject> gameObjects = cell.getGameObjects();
        int n = gameObjects.size();
        if(thereIsADoor) n--;
        if (n == 0) {
            return;
        }

        Vector2 offsetp;
        float radius = 1 / 3f;
        if (n == 1) {
            radius = 0f;
        }

//        GameObject o = gameObjects.get(0);
//        offsetp = o.getOffset();
//        offsetp.y += (o instanceof Player) ? 1/3f : 0.0f;
//        drawTextureInCell(o.getSprite(), cell.getX(), cell.getY(), offsetp.x, offsetp.y);
//    } else {
        float dteta = 2 * (float)Math.PI / n;
        float teta =  (float)Math.PI / 4f;
        for (GameObject gameObject : gameObjects) {
            if(gameObject instanceof Door)continue;//skip door
            offsetp = gameObject.getOffset();
            offsetp.x += (float)Math.cos(teta) * radius;
            offsetp.y += (float)Math.sin(teta) * radius;
            if(gameObject instanceof Character) offsetp.y += 1 / 3f;
            drawTextureInCell(gameObject.getSprite(), cell.getX(), cell.getY(), offsetp.x, offsetp.y);
            teta += dteta;
        }
    }

    private void drawFloor()
    {
        AtlasRegion sprite = Graphics.Sprites.get("floor");
        for(int y = 0; y < maze_height; y++) {
            for (int x = 0; x < maze_width; x++) {
               drawTextureInCell(sprite, x, y);
            }
        }
    }

    private void drawSideWalls()
    {
        AtlasRegion sprite_l = Graphics.Sprites.get("wall_side_mid_left");
        AtlasRegion sprite_r = Graphics.Sprites.get("wall_side_mid_right");
        for (int y = 0; y < maze_height; y++)
        {
            drawTextureInCell(sprite_l, -1, y);
            drawTextureInCell(sprite_r, maze_width, y);
        }
        drawTextureInCell(Graphics.Sprites.get("wall_side_top_left"), -1, maze_height + 1);
        drawTextureInCell(Graphics.Sprites.get("wall_side_mid_left"), -1, maze_height);
        drawTextureInCell(Graphics.Sprites.get("wall_side_top_right"), maze_width, maze_height + 1);
        drawTextureInCell(Graphics.Sprites.get("wall_side_mid_right"), maze_width, maze_height);
    }

    private void drawBackWall()
    {
        AtlasRegion sprite_top = Graphics.Sprites.get("wall_top_mid");
        AtlasRegion sprite_mid = Graphics.Sprites.get("wall_mid");
        AtlasRegion sprite_wall_top = Graphics.Sprites.get("wall_top");
        for (int x = 0; x < maze_width; x++) {
            Cell down = maze.getCellAt(x,maze_height - 1);
            if (down.hasInstanceOf(IndestructibleWall.class)) {
                drawTextureInCell(sprite_wall_top, x, maze_height);
            } else {
                drawTextureInCell(sprite_top, x, maze_height + 1);
                drawTextureInCell(sprite_mid, x, maze_height);
            }
        }
    }

    private void drawFrontWall()
    {
        AtlasRegion sprite_top = Graphics.Sprites.get("wall_top_mid");
        AtlasRegion sprite_mid = Graphics.Sprites.get("wall_mid");
        AtlasRegion sprite_bottom = Graphics.Sprites.get("edge");
        for (int x = 0; x < maze_width; x++) {
            drawTextureInCell(sprite_top, x, 0);
            drawTextureInCell(sprite_mid, x, -1);
            drawTextureInCell(sprite_bottom, x, -2);
        }
        drawTextureInCell(Graphics.Sprites.get("wall_side_front_left"), -1, -1);
        drawTextureInCell(Graphics.Sprites.get("edge_left"), -1, -2);
        drawTextureInCell(Graphics.Sprites.get("wall_side_front_right"), maze_width, -1);
        drawTextureInCell(Graphics.Sprites.get("edge_right"), maze_width, -2);
    }

    private void drawTextureInCell(AtlasRegion atlasRegion, int cell_x, int cell_y, float offsetp_x, float offsetp_y)
    {
        if (atlasRegion != null) {
            float x = ((cell_x + x_padding + offsetp_x) * Constants.BOX_WIDTH);
            float y = ((cell_y + y_padding + offsetp_y) * Constants.BOX_HEIGHT);
            batch.draw(atlasRegion, x, y);
        }
    }

    private void drawTextureInCell(AtlasRegion atlasRegion, int cell_x, int cell_y)
    {
        drawTextureInCell(atlasRegion, cell_x, cell_y, 0.0f, 0.0f);
    }

    /**
     * Transforms screen position to cell position
     * @param screen_x screen x position
     * @param screen_y screen y position
     * @return Corresponding cell position in maze
     */
    public VectorInt2 screenPosToCell(float screen_x, float screen_y)
    {
        screen_y = Gdx.graphics.getHeight() - screen_y;
        Vector3 p = new Vector3(screen_x, screen_y, 0f);
        camera.unproject(p);
        p.x = p.x - (Constants.BOX_WIDTH * x_padding);
        p.y = p.y - (Constants.BOX_HEIGHT * y_padding);
        int cell_x = (int)Math.floor(p.x / Constants.BOX_WIDTH);
        int cell_y = (int)Math.floor(p.y / Constants.BOX_HEIGHT);
        return new VectorInt2(cell_x, cell_y);
    }
}
