package com.oliveshark.pathworks.framework.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.oliveshark.pathworks.framework.ViewStage;
import com.oliveshark.pathworks.config.Config;

public class Grid extends Actor {

    private static int GRID_WIDTH = Config.getGridWidth();
    private static int GRID_HEIGHT = Config.getGridHeight();
    private static int TILE_DIMENSION = Config.getTileDimension();


    private Cell[][] cells;
    private Texture tileTexture;

    private boolean touchDragToggle = false;

    public Grid() {
        tileTexture = new Texture(Gdx.files.internal("tiles_grass_boulder.png"));
        cells = new Cell[GRID_WIDTH][GRID_HEIGHT];
        for (int i = 0; i < GRID_WIDTH; ++i) {
            for (int j = 0; j < GRID_HEIGHT; ++j) cells[i][j] = new Cell(tileTexture);
        }

        setWidth(GRID_WIDTH * TILE_DIMENSION);
        setHeight(GRID_HEIGHT * TILE_DIMENSION);

        addListener(new GridClickListener());
        setName("grid");
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < GRID_WIDTH; ++i) {
            for (int j = 0; j < GRID_HEIGHT; ++j) {
                cells[i][j].draw(batch, i * TILE_DIMENSION, j * TILE_DIMENSION);
            }
        }
    }

    @Override
    public boolean remove() {
        boolean success = super.remove();
        tileTexture.dispose();
        return success;
    }

    public void reverseCells() {
        for (int i = 0; i < cells.length; ++i) {
            for (int j = 0; j < cells[i].length; ++j) {
                if (!hasAgentOnTile(getStagePositionFromGridPosition(i, j))) {
                    cells[i][j].toggleOccupied();
                }
            }
        }
    }

    private boolean hasAgentOnTile(Vector2 position) {
        return ((ViewStage) getStage()).hasAgentOnTile(position);
    }

    public Cell getCell(Vector2 pos) {
        return cells[(int)pos.x][(int)pos.y];
    }

    public boolean isCellOccupied(Vector2 pos) {
        return cells[(int)pos.x][(int)pos.y].isOccupied();
    }

    /**
     * This converts 'click' positions (that have origin in top left corner)
     * to a grid position (that has origin in bottom left) and coresponds to a grid cells
     * in the Grids cell matrix
     *
     * @param x The x pos
     * @param y The y pos
     * @return The position in Grid cell coordinates
     */
    public static Vector2 getGridPositionFromScreenPosition(int x, int y) {
        int cellX = (x - (x % TILE_DIMENSION)) / TILE_DIMENSION;
        int cellY = (y - (y % TILE_DIMENSION)) / TILE_DIMENSION;

        // Mouse pos originates from the top left corner (like SDL)
        // libgdx originates coordinates from bottom left so we have to reverse it here
        cellY = GRID_HEIGHT - cellY - 1;

        if (cellX < 0) cellX = 0;
        else if (cellX >= GRID_WIDTH) cellX = GRID_WIDTH - 1;
        if (cellY < 0) cellY = 0;
        else if (cellY >= GRID_HEIGHT) cellY = GRID_HEIGHT - 1;
        return new Vector2(cellX, cellY);
    }

    /**
     * Takes a position and converts it to the coresponding grid cell position.
     *
     * @param x The x pos
     * @param y The y pos
     * @return The position in Grid cell coordinates
     */
    public static Vector2 getGridPositionFromStagePosition(float x, float y) {
        float cellX = (x - (x % TILE_DIMENSION)) / TILE_DIMENSION;
        float cellY = (y - (y % TILE_DIMENSION)) / TILE_DIMENSION;

        if (cellX < 0) cellX = 0;
        else if (cellX >= GRID_WIDTH) cellX = GRID_WIDTH - 1;
        if (cellY < 0) cellY = 0;
        else if (cellY >= GRID_HEIGHT) cellY = GRID_HEIGHT - 1;
        return new Vector2(cellX, cellY);
    }

    public static Vector2 getStagePositionFromGridPosition(Vector2 pos) {
        return getStagePositionFromGridPosition(pos.x, pos.y);
    }

    public static Vector2 getStagePositionFromGridPosition(int x, int y) {
        return new Vector2(x * TILE_DIMENSION, y * TILE_DIMENSION);
    }

    public static Vector2 getStagePositionFromGridPosition(float x, float y) {
        return new Vector2(x * TILE_DIMENSION, y * TILE_DIMENSION);
    }

    private class GridClickListener extends ClickListener {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Vector2 cellPos = getGridPositionFromStagePosition(x, y);
            Cell cell = cells[(int)cellPos.x][(int)cellPos.y];
            if (button == Input.Buttons.LEFT) {
                if (hasAgentOnTile(getStagePositionFromGridPosition(cellPos))) {
                    return false;
                }
                cell.toggleOccupied();
                touchDragToggle = cell.isOccupied();
                return true;
            }

            return false;
        }

        @Override
        public void touchDragged(InputEvent input, float x, float y, int pointer) {
            Vector2 cellPos = getGridPositionFromStagePosition(x, y);
            if (!hasAgentOnTile(getStagePositionFromGridPosition(cellPos))) {
                cells[(int)cellPos.x][(int)cellPos.y].setOccupied(touchDragToggle);
            }
        }
    }
}
