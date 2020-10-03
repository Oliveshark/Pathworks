package com.oliveshark.pathworks.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.oliveshark.pathworks.framework.algorithm.def.AbstractAlgorithm;
import com.oliveshark.pathworks.framework.algorithm.impl.NavigateTowardsDestinationAlgorithm;
import com.oliveshark.pathworks.framework.collision.CollisionController;
import com.oliveshark.pathworks.framework.entities.Agent;
import com.oliveshark.pathworks.framework.entities.PointerIndicator;
import com.oliveshark.pathworks.framework.grid.Grid;
import com.oliveshark.pathworks.framework.grid.util.Rectangle;
import com.oliveshark.pathworks.framework.grid.util.TilePackManager;
import com.oliveshark.pathworks.framework.storage.PathMapStore;
import com.oliveshark.pathworks.framework.ui.PathMapFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oliveshark.pathworks.config.Config.TILE_DIMENSION;
import static com.oliveshark.pathworks.framework.grid.util.Rectangle.createSquare;

public class ViewStage extends Stage {

    public static final char ESCAPE_BTN = '\u001b';

    private Grid grid;
    private PointerIndicator pointerIndicator;

    private Agent currentAgent = null;
    private boolean secondRightClick = false;

    private AbstractAlgorithm activeAlgorithm = new NavigateTowardsDestinationAlgorithm();
    private boolean executing = false;

    private Texture agentTexture;
    private Texture destTexture;
    private TilePackManager tilePackManager;

    private Window menu;
    private PathMapFileChooser pathMapFileChooser;
    private PathMapStore pathMapStore;

    private ShapeRenderer shapeRenderer;

    public ViewStage(NativeFileChooser nativeFileChooser) {
        agentTexture = new Texture(Gdx.files.internal("agent.png"));
        destTexture = new Texture(Gdx.files.internal("destination.png"));
        tilePackManager = new TilePackManager("tiles");
        shapeRenderer = new ShapeRenderer();
        addActor(grid = new Grid(tilePackManager));
        pathMapStore = new PathMapStore(grid);
        addActor(pointerIndicator = new PointerIndicator());
        getBatch().enableBlending();
        this.pathMapFileChooser = new PathMapFileChooser(nativeFileChooser);
        initUI();
        addListener(new InputListener() {

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (character == 'r') {
                    grid.reverseCells();
                    return true;
                } else if (character == 'e') {
                    executing = !executing;
                    for (Agent agent : getAgents()) {
                        agent.getVelocity().set(0, 0);
                    }
                } else if (character == '\t') {
                    tilePackManager.next();
                } else if (character == ESCAPE_BTN) {
                    menu.setVisible(!menu.isVisible());
                }
                return false;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                pointerIndicator.updatePosition(x, y);
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                pointerIndicator.updatePosition(x, y);
                super.touchDragged(event, x, y, pointer);
            }
        });
    }

    private void initUI() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        menu = new Window("Menu", skin);
        menu.setPosition(this.getWidth() / 2 - menu.getWidth() / 2, this.getHeight() / 2 - menu.getHeight() / 2);
        addActor(menu);
        menu.add(new Label("Press ESC to show/hide", skin));
        menu.setWidth(200);
        menu.row();

        TextButton load_map = new TextButton("Load map", skin);
        load_map.addListener(onClick(() -> pathMapFileChooser.chooseFile(pathMapStore::load)));
        menu.add(load_map);

        menu.row();

        TextButton save_map = new TextButton("Save map", skin);
        save_map.addListener(onClick(() -> pathMapFileChooser.chooseFile(pathMapStore::save)));
        menu.add(save_map);

        menu.row();

        TextButton reset = new TextButton("Reset map", skin);
        reset.addListener(onClick(() -> {
            grid.reset();
            this.resetAgents();
        }));
        menu.add(reset);
    }

    private ClickListener onClick(Runnable onClick) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                onClick.run();
            }
        };
    }

    @Override
    public void act(float delta) {
        CollisionController cctrl = CollisionController.get();
        cctrl.clear();
        cctrl.populate(getAgents(), grid);
        if (executing) {
            for (Agent agent : getAgents())
                activeAlgorithm.operate(grid, agent);
        }
        super.act(delta);
    }

    @Override
    public void draw() {
        super.draw();
        CollisionController.get().drawColliders(shapeRenderer);
        if (currentAgent != null) {
            Batch batch = getBatch();
            batch.begin();
            currentAgent.draw(batch, 1);
            batch.end();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            Vector2 cellPos = Grid.getGridPositionFromScreenPosition(screenX, screenY);
            Vector2 gdxCellPos = Grid.getStagePositionFromGridPosition(cellPos);
            Vector2 gdxPos = screenToStageCoordinates(new Vector2(screenX, screenY));

            // Check if this is a remove agent click
            if (!secondRightClick && hasAgentOrDestinationAt(gdxPos.x, gdxPos.y)) {
                getActors().removeValue(getAgentFor(gdxPos.x, gdxPos.y), true);
                return true;
            }

            // Otherwise check if we should add an agent or a destination
            if (grid.isCellOccupied(cellPos) || hasAgentOnTile(gdxCellPos)) {
                return false;
            }
            if (secondRightClick) {
                currentAgent.setDestination(gdxCellPos);
                addActor(currentAgent);
                currentAgent = null;
                secondRightClick = false;
            } else {
                currentAgent = new Agent(agentTexture, destTexture, gdxCellPos.x, gdxCellPos.y);
                secondRightClick = true;
            }
        }
        if (button == Input.Buttons.LEFT) {
            secondRightClick = false;
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private List<Agent> getAgents() {
        return Stream.of(getActors().items)
                .filter(Objects::nonNull)
                .filter(actor -> actor instanceof Agent)
                .map(actor -> (Agent) actor)
                .collect(Collectors.toList());
    }

    private boolean hasAgentOrDestinationAt(float x, float y) {
        return getAgentFor(x, y) != null;
    }

    private Agent getAgentFor(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        for (Agent agent : getAgents()) {
            Rectangle agentRect = Rectangle.fromActor(agent);
            if (agentRect.contains(pos)) {
                return agent;
            }
            if (agent.hasDestination()) {
                Rectangle destRect = createSquare(agent.getDestination(), TILE_DIMENSION);
                if (destRect.contains(pos)) {
                    return agent;
                }
            }
        }
        return null;
    }

    public boolean hasAgentOnTile(Vector2 tilePos) {
        Rectangle rect = createSquare(tilePos, TILE_DIMENSION);
        for (Agent agent : getAgents()) {
            Rectangle agentRect = Rectangle.fromActor(agent);
            if (rect.overlaps(agentRect)) {
                return true;
            }
            if (agent.hasDestination()) {
                Rectangle destRect = createSquare(agent.getDestination(), TILE_DIMENSION);
                if (rect.overlaps(destRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetAgents() {
        getAgents().forEach(agent -> {
            agent.remove();
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}
