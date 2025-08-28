package io.github.laziji;

public class Runway {

    private float maxX=20;
    private float maxY=100;

    public Runway(float maxX, float maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
}
