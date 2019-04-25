package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.ui.DeadUI;

public class DeadScreen extends MenuScreen {
    @Override
    public void show() {
        super.show();
        Audio.WASTED.play();
    }
    public DeadScreen(StoryMenuScreen screen, int maze_id){
        super();
        addUI(new DeadUI(screen,maze_id));
    }
}
