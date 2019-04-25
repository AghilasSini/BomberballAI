package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.ui.EndInfiniteUI;

public class EndInfiniteScreen extends MenuScreen {
    @Override
    public void show() {
        super.show();
        Audio.WASTED.play();
    }
    public EndInfiniteScreen(){
        super();
        addUI(new EndInfiniteUI());
    }
}
