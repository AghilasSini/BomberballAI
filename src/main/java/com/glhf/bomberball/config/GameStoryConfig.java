package com.glhf.bomberball.config;

public class GameStoryConfig extends GameSoloConfig {

    public int last_level_played = 0;
    public int last_level_unlocked = 0;


    /**
     * Default constructor with the default configuration values
     */
    public GameStoryConfig(){
    }

    public static GameStoryConfig get(String config_name) {
        return get(config_name, GameStoryConfig.class);
    }

    public static GameStoryConfig get() {
        return get("config_game_story");
    }
    public void exportConfig() {
        exportConfig("config_game_story");
    }



    /**
     * resets all the levels to the initial state (only the first level is unlocked) and saves it in the config file.
     */
    public void resetLevels()
    {
        last_level_unlocked=0;
        exportConfig();
    }
}
