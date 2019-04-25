package com.glhf.bomberball.screens;

import com.glhf.bomberball.audio.Audio;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.ui.VictoryMenuUI;

public class VictoryMenuScreen extends AbstractScreen {
    boolean equality;
    public VictoryMenuScreen(Player p, int maze_id) {
        super();
        equality=(p==null);
        addUI(new VictoryMenuUI(p, maze_id));
    }

    @Override
    public void show() {
        super.show();
        if(equality)  {Audio.EQUALITY.play();}
        else{Audio.VICTORY.play();}
    }
}
