package com.oliveshark.pathworks.framework.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.oliveshark.pathworks.framework.entities.Agent;
import com.oliveshark.pathworks.framework.grid.Cell;
import com.oliveshark.pathworks.framework.grid.Grid;
import com.oliveshark.pathworks.framework.grid.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class CollisionController {

    private static CollisionController instance = null;
    private ArrayList<Collidable> colliders;

    private CollisionController() {
        colliders = new ArrayList<>();
    }

    public void clear() {
        colliders.clear();
    }

    public void populate(List<Agent> actors, Grid grid) {
        for (Actor a : actors) {
            String id = Integer.toHexString(a.hashCode());
            Rectangle rect = Rectangle.fromActor(a);
            colliders.add(new Collidable(id, rect));
        }
        Cell[][] cells = grid.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isOccupied()) {
                    colliders.add(new Collidable("blocker", Rectangle.createSquare(i * 32, j * 32, 32)));
                }
            }
        }
    }

    public void drawColliders(ShapeRenderer shapeRenderer) {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        for (Collidable c : colliders) {
            c.draw(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public boolean hasCollided(Actor a) {
        Rectangle rect = Rectangle.fromActor(a);
        for (Collidable collidable : colliders) {
            String id = Integer.toHexString(a.hashCode());
            if (id.equals(collidable.identifier)) {
                continue;
            }
            if (rect.overlaps(collidable.rect)) {
                return true;
            }
        }
        return false;
    }

    public static CollisionController get() {
        if (instance == null) {
            instance = new CollisionController();
        }
        return instance;
    }

    private static class Collidable {
        private String identifier;
        private Rectangle rect;

        public Collidable(String identifier, Rectangle rect) {
            this.identifier = identifier;
            this.rect = rect;
        }

        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.rect(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        }
    }
}
