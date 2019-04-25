package com.glhf.bomberball.config;

public abstract class GameSoloConfig extends GameConfig {

    public String player_skin = "knight_m";

    // Enemies
    public int activeEnemy_life = 1;
    public int activeEnemy_strength = 1;
    public int activeEnemy_moves = 5;

    public int passiveEnemy_life = 1;
    public int passiveEnemy_strength = 1;
    public int passiveEnemy_moves = 5;

    public int aggressiveEnemy_life = 1;
    public int aggressiveEnemy_strength = 1;
    public int aggressiveEnemy_moves = 5;
    public int aggressiveEnemy_huntingRange = 4;

    /**
     * Default constructor with the default configuration values
     */
    public GameSoloConfig() {
    }
}
