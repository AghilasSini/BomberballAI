package com.glhf.bomberball.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.Graphics;
import com.glhf.bomberball.Translator;
import com.glhf.bomberball.audio.AudioButton;
import com.glhf.bomberball.maze.MazeDrawer;
import com.glhf.bomberball.screens.GameMultiScreen;
import com.glhf.bomberball.screens.MainMenuScreen;
import com.glhf.bomberball.screens.MultiMenuScreen;
import com.glhf.bomberball.utils.ScreenChangeListener;

import static com.glhf.bomberball.utils.Constants.PATH_GRAPHICS;

public class MultiMenuUI extends MenuUI {

    private MultiMenuScreen screen;
    private MazeDrawer maze_preview;

    public MultiMenuUI(MultiMenuScreen screen) {
        this.screen = screen;
        this.initialize();
    }

    private void initialize()
    {
        System.out.println("Initialization of the Multi Menu");
        this.setFillParent(true);
        //this.padTop(Value.percentHeight(0.5f));
        initializeButtons();
        initializeMazePreview();
    }

    private void update()
    {
        System.out.println("Update !");
        this.clear();
        initialize();
    }

    private void initializeMazePreview ()
    {
        maze_preview = new MazeDrawer(screen.maze, 0.25f, 0.75f,  0.4f, 1f, MazeDrawer.Fit.BEST);
        this.add(maze_preview);
    }

    private void initializeButtons() {
        // CREATION OF BUTTONS FOR THE CREATION OF THE MAP
        TextButton nextMapButton = new AudioButton(">", Graphics.GUI.getSkin());
        nextMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.nextMaze();
                maze_preview.setMaze(screen.maze);
            }
        });

        TextButton previousMapButton = new AudioButton("<", Graphics.GUI.getSkin());
        previousMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.previousMaze();
                maze_preview.setMaze(screen.maze);
           }
        });

        //ADDING THE BUTTONS TO THE TABLE
        Table selectMap = new Table();
        selectMap.add(previousMapButton).expand().align(Align.left);
        selectMap.add(nextMapButton).expand().align(Align.right);
        this.add(selectMap).grow();
        this.row();

        // BUTTON TO LOAD THE GAME
        Table buttons = new Table();
        buttons.pad(Value.percentWidth(0.05f));
        TextButton playButton = new AudioButton(Translator.translate("Play"), Graphics.GUI.getSkin());
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.saveToConfig();
               // Bomberball.changeScreen(new GameMultiScreen(screen.maze, screen.getMazeId()));
            }
        });

        // BUTTON TO CHOOSE A RANDOM MAZE

        TextButton randomMapButton = new AudioButton(Translator.translate("Random Maze"), Graphics.GUI.getSkin());
        randomMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.randomMaze();
                maze_preview.setMaze(screen.maze);
            }
        });
        // BUTTON TO EXIT THE MENU
        TextButton cancelButton = new AudioButton(Translator.translate("Back"), Graphics.GUI.getSkin());
        cancelButton.addListener(new ScreenChangeListener(MainMenuScreen.class));

        //ADDING THE BUTTONS TO THE TABLE
        Value spacing = Value.percentHeight(0.2f);
        buttons.add(playButton).grow().space(spacing).row();
        buttons.add(randomMapButton).grow().space(spacing).row();
        buttons.add(cancelButton).grow();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(PATH_GRAPHICS+"background/scroll.png"))));
        buttons.setBackground(background);

        //CREATING A PREVIEW FOR THE PLAYERS
        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture(PATH_GRAPHICS+"gui/plaindark_9patch.png"), 5, 5, 5, 5));

        AnimationActor p1 = new AnimationActor(new Animation<>(0.15f, Graphics.Anims.get(MultiMenuScreen.playable[MultiMenuScreen.p1_id] + "/idle"), Animation.PlayMode.LOOP));
        p1.mustMove(true);
        TextButton Bp1 = new AudioButton("P1", Graphics.GUI.getSkin());
        Bp1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.nextP1();
                update();
            }
        });
        Table Vp1 = new Table();
        Vp1.pad(Value.percentWidth(0.05f));
        Vp1.add(p1).grow();
        Vp1.row();
        Vp1.add(Bp1).growX();
        Vp1.setBackground(patch);

        AnimationActor p2 = new AnimationActor(new Animation<>(0.15f, Graphics.Anims.get(MultiMenuScreen.playable[MultiMenuScreen.p2_id] + "/idle"), Animation.PlayMode.LOOP));
        p2.mustMove(true);
        TextButton Bp2 = new AudioButton("P2", Graphics.GUI.getSkin());
        Bp2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.nextP2();
                update();
            }
        });
        Table Vp2 = new Table();
        Vp2.pad(Value.percentWidth(0.05f));
        Vp2.add(p2).grow();
        Vp2.row();
        Vp2.add(Bp2).growX();
        Vp2.setBackground(patch);

//        AnimationActor p3 = new AnimationActor(new Animation<>(0.15f, Graphics.Anims.get(MultiMenuScreen.playable[MultiMenuScreen.p3_id] + "/idle"), Animation.PlayMode.LOOP));
//        p3.mustMove(true);
//        TextButton Bp3 = new AudioButton("P3", Graphics.GUI.getSkin());
//        Bp3.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                screen.nextP3();
//                update();
//            }
//        });
//        Table Vp3 = new Table();
//        Vp3.pad(Value.percentWidth(0.05f));
//        Vp3.add(p3).grow();
//        Vp3.row();
//        Vp3.add(Bp3).growX();
//        Vp3.setBackground(patch);
//
//        AnimationActor p4 = new AnimationActor(new Animation<>(0.15f, Graphics.Anims.get(MultiMenuScreen.playable[MultiMenuScreen.p4_id] + "/idle"), Animation.PlayMode.LOOP));
//        p4.mustMove(true);
//        TextButton Bp4 = new AudioButton("P4", Graphics.GUI.getSkin());
//        Bp4.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                screen.nextP4();
//                update();
//            }
//        });
//        Table Vp4 = new Table();
//        Vp4.pad(Value.percentWidth(0.05f));
//        Vp4.add(p4).grow();
//        Vp4.row();
//        Vp4.add(Bp4).growX();
//        Vp4.setBackground(patch);

        Table selectPlayer = new Table();
        selectPlayer.add(Vp1).grow();
        selectPlayer.add(Vp2).grow();
        selectPlayer.add(buttons).grow();
//        selectPlayer.add(Vp3).grow();
//        selectPlayer.add(Vp4).grow();
        this.add(selectPlayer).height(Value.percentHeight(0.40f, this)).growX();
    }


}
