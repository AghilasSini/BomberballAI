package com.glhf.bomberball.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.config.GameInfiniteConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.screens.EditorMenuScreen;
import com.glhf.bomberball.screens.EditorScreen;
import com.glhf.bomberball.utils.ScreenChangeListener;
import com.glhf.bomberball.utils.VectorInt2;

import java.util.ArrayList;

import static com.glhf.bomberball.utils.Constants.BOX_WIDTH;

public class EditorUI extends MenuUI {

    private EditorScreen screen;
    private Maze maze;
    private MazeDrawer maze_preview;

    public EditorUI(EditorScreen screen, Maze maze)
    {
        this.screen = screen;

        this.maze = maze;
        maze_preview = new MazeDrawer(maze, 0.0f, 0.80f, 0.0f, 1.0f, MazeDrawer.Fit.BEST);

        this.setFillParent(true);
        this.padLeft(Value.percentWidth(0.80f));

        initializeButtons();
        this.addActor(maze_preview);
    }

    public void initializeButtons() {
        TextButton bouton_retour = new AudioButton(Translator.translate("Back"), Graphics.GUI.getSkin());
        bouton_retour.addListener(new ScreenChangeListener(EditorMenuScreen.class));
        TextButton button_save = new AudioButton(Translator.translate("Save"), Graphics.GUI.getSkin());
        button_save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.saveMaze();
            }
        });
        this.add(new ObjectsWidget()).grow();
        this.row();
        this.add(bouton_retour).grow();
        this.row();
        this.add(button_save).grow();
    }

    public VectorInt2 screenPosToCell(float x, float y) {
        return maze_preview.screenPosToCell(x ,y);
    }

    class ObjectsWidget extends ScrollPane {

        private ArrayList<GameObject> presets = new ArrayList<>();
        private Table content;
        private ButtonGroup<ImageButton> group;

        public ObjectsWidget() {
            super(null);
            group = new ButtonGroup<>();
            group.setMaxCheckCount(1);
            group.setMinCheckCount(0);
            content = new Table();
            presets.add(new IndestructibleWall());
            presets.add(new DestructibleWall());
            presets.add(new BonusWall(new Bonus(Bonus.Type.SPEED)));
            presets.add(new BonusWall(new Bonus(Bonus.Type.BOMB_NUMBER)));
            presets.add(new BonusWall(new Bonus(Bonus.Type.BOMB_RANGE)));
            GameInfiniteConfig config = GameInfiniteConfig.get();
            //presets.add(new PassiveEnemy("skelet", config.passiveEnemy_life, config.passiveEnemy_moves, config.passiveEnemy_strength, new ArrayList<>()));
            //presets.add(new AggressiveEnemy("wogol", config.aggressiveEnemy_life, config.aggressiveEnemy_moves, config.aggressiveEnemy_strength, config.aggressiveEnemy_huntingRange));
            //presets.add(new ActiveEnemy("swampy", config.activeEnemy_life, config.activeEnemy_moves, config.activeEnemy_strength));

            presets.add(new Player("knight_m", 1, 1,1, 1));
            //presets.add(new Door());

            this.setActor(content);
            for (GameObject o : presets) {
                TextureRegionDrawable image = new TextureRegionDrawable(o.getSprite());
                Drawable imageChecked = image.tint(Color.YELLOW);
                ImageButton button = new ImageButton(image, image, imageChecked);
                button.getImageCell().expand().fill();
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        screen.select(o);
                    }
                });
                content.add(button).minSize(3*BOX_WIDTH).growX().row();//maze_preview.getScale()
                group.add(button);
            }
            group.uncheckAll();
        }
    }
}
