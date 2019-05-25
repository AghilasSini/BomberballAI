package com.glhf.bomberball.config;

import com.glhf.bomberball.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;

/**
 * abstract class Config
 *
 * Class to store configs
 * Extend Config to create a new type of config file
 *
 * Example :
 * class <ConfigClass> extends Config { [...] }
 * <ConfigClass> config = Config.get("config name", <ConfigClass>) imports a config
 * config.exportConfig("config name") exports a config
 *
 * @author nayala
 */
public abstract class Config {
    private static HashMap<String, Config> configs = new HashMap<>();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Imports a config
     * @param config_name Config file name
     * @param c Config class to serialize
     * @return config class from config file
     */
    @SuppressWarnings("unchecked")
    public static <T extends Config> T get(String config_name, Class<T> c){
        if(!configs.containsKey(config_name)){
            configs.put(config_name, importConfig(config_name, c));
        }
        return (T) configs.get(config_name);
    }

    /**
     * Imports a config
     * @param name Config file name
     * @param c Config class to serialize
     * @return config class from config file
     */
    private static <T extends Config> T importConfig(String name, Class<T> c) {
        try {
            String fileName = Constants.PATH_CONFIGS + name + ".json";
            if(!new File(fileName).exists()) {
                c.newInstance().reset().exportConfig(name);
            }
            return gson.fromJson(new FileReader(fileName), c);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot import config : " + e.getMessage());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * function applied when file not found, just after the default constructor
     * @return itself
     */
    protected abstract Config reset();

    /**
     * Exports a config
     * @param name Config file name
     */
    public void exportConfig(String name) {
        try {
            File file = new File(Constants.PATH_CONFIGS + name + ".json");
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs(); //needed when file in a directory
            Writer writer = new FileWriter(file);
            writer.write(this.toString());
            writer.close();
            configs.remove(name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
