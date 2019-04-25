package com.glhf.bomberball.config;
public class GameMultiConfig extends GameConfig {
    public transient int player_count;
    public String[] player_skins;
    public GameMultiConfig() {
    }

    public static GameMultiConfig get(String config_name){
        return get(config_name, GameMultiConfig.class);
    }

    public static GameMultiConfig get() {
        return get("config_game_multi");
    }
    public void exportConfig() {
        super.exportConfig("config_game_multi");
    }
}
