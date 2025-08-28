package io.github.laziji;

public class RectGroup {

    private Rect left;
    private Rect right;
    private float y;
    private float speed;
    private boolean pass=false;

    public RectGroup(Rect left, Rect right, float y,float speed) {
        this.left = left;
        this.right = right;
        this.y = y;
        this.speed=speed;
    }

    public Rect getLeft() {
        return left;
    }

    public void setLeft(Rect left) {
        this.left = left;
    }

    public Rect getRight() {
        return right;
    }

    public void setRight(Rect right) {
        this.right = right;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
