package com.glhf.bomberball.audio;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ListenerAudioButton  extends ChangeListener {

        @Override
        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Audio.CLICK_BUTTON.play();
        }
}

