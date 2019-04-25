package com.glhf.bomberball.config;

public class GameInfiniteConfig extends GameSoloConfig {

    public int highscore = 0;
    public boolean bonus_activated = true;
    public boolean destructible_wall_available = true;
    public boolean finite_number_turn = false;
    public int nbturnmax = 15;

    //Settings maze
    public transient double probFreeCase = 1.0 / 3;


    /**
     * Default constructor with the default configuration values
     */
    public GameInfiniteConfig() {
    }

    public static GameInfiniteConfig get(String config_name) {
        return get(config_name, GameInfiniteConfig.class);
    }

    public static GameInfiniteConfig get() {
        return get("config_game_inf");
    }

    public void exportConfig() {
        exportConfig("config_game_inf");
    }
}
