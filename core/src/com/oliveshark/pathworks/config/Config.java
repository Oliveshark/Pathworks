package com.oliveshark.pathworks.config;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Config {
    private static final String CONFIG_FILE = "core/src/com/oliveshark/pathworks/config/config.json";

    private static int GRID_WIDTH = 32;
    private static int GRID_HEIGHT = 24;
    private static int TILE_DIMENSION = 32;

    public static int getTileDimension() {
        return TILE_DIMENSION;
    }

    public static int getGridHeight() {
        return GRID_HEIGHT;
    }

    public static int getGridWidth() {
        return GRID_WIDTH;
    }

    public static void load(){
        JsonReader reader = new JsonReader();
        JsonValue config = null;

        try {
            config = reader.parse(new FileInputStream(CONFIG_FILE));
            GRID_WIDTH = config.get("GRID_WIDTH") != null ? config.get("GRID_WIDTH").asInt() : 32;
            GRID_HEIGHT= config.get("GRID_HEIGHT") != null ? config.get("GRID_HEIGHT").asInt() : 24;
            TILE_DIMENSION= config.get("TILE_DIMENSION") != null ? config.get("TILE_DIMENSION").asInt() : 32;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}
