package com.glhf.bomberball.config;

public class GameConfig extends Config {

    public final static int maze_count = 7; //number of levels in the story mode
    // Walls
    public int wall_life = 1;

    // Bombs
    public int initial_bomb_number = 1;
    public int initial_bomb_range = 3;
    public int bomb_damage = 1;

    // Player
    public int player_life = 1;
    public int initial_player_moves = 5;

    /*public String[] player_skins_list = {
            "knight_m",
            "knight_f",
            "elf_m",
            "wizzard_f"};*/
    @Override
    protected Config reset() {
        return this;
    }
}
