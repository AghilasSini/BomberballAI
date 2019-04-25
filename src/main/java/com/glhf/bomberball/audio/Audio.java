package com.glhf.bomberball.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.glhf.bomberball.utils.Constants;

/**
 * @author jyra
 * Example : Audio.CLICK_BUTTON.play() to play a song
 */
public enum Audio {
    CLICK_BUTTON(Constants.PATH_SOUNDS+"click_button.wav"),
    VICTORY(Constants.PATH_SOUNDS+"victory.wav"),
    WASTED(Constants.PATH_SOUNDS+"wasted.wav"),
    CLICK_PLAY(Constants.PATH_SOUNDS+"PlayLevelButtonTone.wav"),
    MAIN_MENU(Constants.PATH_SOUNDS+"MainMenu.mp3"),
    GAME_SONG(Constants.PATH_SOUNDS+"GameSong.mp3"),
    BOMB(Constants.PATH_SOUNDS+"Bomb.wav"),
    POWER_UP(Constants.PATH_SOUNDS+"Powerup.wav"),
    EQUALITY(Constants.PATH_SOUNDS+"equality.ogg"),
    STORY_SONG(Constants.PATH_SOUNDS+"histoireMusique.mp3");

    private Sound sound;
    private Boolean played;
    public final Boolean AUDIO_ENABLE = true;
    Audio(String path)
    {
        if(AUDIO_ENABLE) {
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            System.out.println("Sounds " + path + " loaded");
            played = false;
        }
    }

    public void play() {
        if (AUDIO_ENABLE) {
            sound.play();
        }
    }

    public void stop() {
        if(AUDIO_ENABLE) {
            this.sound.stop();
            played = false;
        }
    }
    public static void silence () {
        Audio[] a = Audio.values();
        for (Audio l : a) {
            l.stop();
        }
    }
    public boolean isPlayed(){
        return this.played;
    }

    /**
     * Permet de jouer une musique, ça va silence tous les autres sons et lancer la musique
     */
    public void playMusique(){
        if(AUDIO_ENABLE) {
            if (!this.isPlayed()) {
                silence();
                sound.loop(0.3f);
                played = true;
            }
        }
    }

}
