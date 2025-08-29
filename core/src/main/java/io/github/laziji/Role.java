package io.github.laziji;

public class Role {

    private float x;
    private float y;
    private float dx=0;
    private float dy=0;
    private float width;
    private float height;
    private float score;
    private float speed;

    public Role(float x, float y, float width, float height, float score,float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.score = score;
        this.speed=speed;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }
}
