package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.ui.EndLevelUI;

public class EndLevelScreen extends MenuScreen {
    @Override
    public void show() {
        super.show();
        Audio.VICTORY.play();
    }

    public EndLevelScreen(StoryMenuScreen screen, int maze_id){
        super();
        addUI(new EndLevelUI(screen,maze_id));
    }
}
