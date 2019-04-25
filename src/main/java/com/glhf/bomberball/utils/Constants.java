package com.glhf.bomberball.utils;

public class Constants {
    // textures properties
    public static final int BOX_WIDTH = 16;
    public static final int BOX_HEIGHT = 16;

    //game constants
    public static final int NB_MAX_PLAYERS = 4;
    public static final int MAX_DEPTH = 16;

    // constants for paths
    // added by asini
    public static final String PATH_ASSET	  = "./src/resources/assets/";
    // for generating the jar file
    //public static final String PATH_ASSET         = "assets/";
    public static final String PATH_MAZE          = PATH_ASSET+"mazes/";
    public static final String PATH_GRAPHICS      = PATH_ASSET+"graphics/";
    public static final String PATH_CONFIGS       = PATH_ASSET+"configs/";
    public static final String PATH_TRANSLATIONS  = PATH_ASSET+"translations/";
    public static final String PATH_SOUNDS        = PATH_ASSET+"sounds/";
    public static final String PATH_FONTS         = PATH_GRAPHICS+"fonts/";
    public static final String PATH_PACKS         = PATH_GRAPHICS+"packs/";
    public static final String PATH_ATLAS_SPRITES = PATH_PACKS+"pack_sprites.atlas";
    public static final String PATH_ATLAS_ANIMS   = PATH_PACKS+"pack_animations.atlas";
    public static final String PATH_ATLAS_GUI     = PATH_PACKS+"pack_gui.atlas";
    public static final String PATH_MAZECUSTOM    = "mazes/";

    // others constants
    public static final int NB_ANIMATION_FRAMES = 5;
    public static final String DEFAULT_CONFIG_APP = "config_app";
    public static final String DEFAULT_CONFIG_INPUTS = "default_inputs";
}
