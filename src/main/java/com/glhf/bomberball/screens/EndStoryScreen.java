package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.ui.EndStoryUI;

public class EndStoryScreen extends MenuScreen {
    @Override
    public void show() {
        super.show();
        Audio.VICTORY.play();
    }

    public EndStoryScreen(StoryMenuScreen screen, int maze_id){
        super();
        addUI(new EndStoryUI(screen,maze_id));
    }
}
