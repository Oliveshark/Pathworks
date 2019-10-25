package com.oliveshark.pathworks.config;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Config {
    private static int gridWidth = 32;
    private static int gridHeight = 24;
    private static int tileDimension = 32;

    public static int getTileDimension() {
        return tileDimension;
    }

    public static int getGridHeight() {
        return gridHeight;
    }

    public static int getGridWidth() {
        return gridWidth;
    }

    public static void load(){
        JsonReader reader = new JsonReader();
        JsonValue config;

        try {
            config = reader.parse(new FileInputStream("config.json"));
            gridWidth = config.get("GRID_WIDTH") != null ? config.get("GRID_WIDTH").asInt() : gridWidth;
            gridHeight = config.get("GRID_HEIGHT") != null ? config.get("GRID_HEIGHT").asInt() : gridHeight;
            tileDimension = config.get("TILE_DIMENSION") != null ? config.get("TILE_DIMENSION").asInt() : tileDimension;

        } catch (FileNotFoundException e) {
        }

    }


}
