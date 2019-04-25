package com.glhf.bomberball.screens;

import com.glhf.bomberball.Bomberball;
import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.config.AppConfig;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.ui.StoryTellingUI;

public class StoryTellingScreen extends AbstractScreen {
    public final int nb_chapters = 4;
    private final AppConfig config;
    public int chapter;
    private StoryTellingUI ui;

    public StoryTellingScreen(){
        ui = new StoryTellingUI(this);
        addUI(ui);
        chapter=1;
        config = AppConfig.get();
        Audio.STORY_SONG.playMusique();
    }

    @Override
    protected void registerActionsHandlers() {
        super.registerActionsHandlers();
        //input_handler.registerActionHandler(Action.NEXT_SCREEN, this::continueStory);
    }

    public void continueStory() {
        if(chapter<nb_chapters){
            chapter++;
            ui.continueStory();
        }else {
            endStory();
        }
    }

    public void endStory() {
        config.story_displayed=true;
        config.exportConfig();
        Bomberball.changeScreen(new GameStoryScreen(new StoryMenuScreen(), Maze.importMazeSolo("maze_"+0), 0));
    }
}
