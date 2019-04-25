package com.glhf.bomberball.config;

import com.glhf.bomberball.utils.Constants;
import com.glhf.bomberball.utils.Resolutions;

public class AppConfig extends Config {

    public boolean fullscreen = false;
    public Resolutions resolution = Resolutions.MEDIUM;
    public String language = "fr";
    public boolean story_displayed = false;

    public static AppConfig get() {
        return get(Constants.DEFAULT_CONFIG_APP, AppConfig.class);
    }

    public void exportConfig(){
        exportConfig(Constants.DEFAULT_CONFIG_APP);
    }

    @Override
    protected Config reset() {
        return this;
    }
}
