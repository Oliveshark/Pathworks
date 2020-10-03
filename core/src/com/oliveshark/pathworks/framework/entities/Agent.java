package com.oliveshark.pathworks.framework.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.oliveshark.pathworks.framework.algorithm.def.AgentUserData;
import com.oliveshark.pathworks.framework.collision.CollisionController;

import java.util.Random;

public class Agent extends Actor {

    private Vector2 destination;
    private Vector2 velocity = new Vector2(0, 0);
    private Texture agentTexture = null;
    private Texture destinationTexture = null;
    private AgentUserData userData;

    public Agent(Texture agentTexture, Texture destinationTexture, float x, float y){
        setColor(generateRandomColor());
        this.agentTexture = agentTexture;
        this.destinationTexture = destinationTexture;
        setX(x);
        setY(y);
        setWidth(32);
        setHeight(32);
        setName("agent");
    }



    private Color generateRandomColor() {
        Random random = new Random(System.nanoTime());
        final int colorUpperBound = 256;
        float red = random.nextInt(colorUpperBound) / 255.0f;
        float green = random.nextInt(colorUpperBound) / 255.0f;
        float blue = random.nextInt(colorUpperBound) / 255.0f;
        return new Color(red, green, blue, 1.0f);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float lastX = getX();
        float lastY = getY();
        setX(getX() + velocity.x * delta);
        if (CollisionController.get().hasCollided(this)) {
            setX(lastX);
        }
        setY(getY() + velocity.y * delta);
        if (CollisionController.get().hasCollided(this)) {
            setY(lastY);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(agentTexture, getX(), getY());
        if (destination != null) {
            batch.draw(destinationTexture, destination.x, destination.y);
        }
        batch.setColor(Color.WHITE);
    }

    public boolean hasDestination() {
        return destination != null;
    }

    public Vector2 getDestination() {
        if (destination != null)
            return destination.cpy();
        return null;
    }

    public void setDestination(Vector2 destination) {
        this.destination = destination;
    }

    public AgentUserData getUserData() {
        return userData;
    }

    public void setUserData(AgentUserData userData) {
        this.userData = userData;
    }
}
