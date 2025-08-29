package io.github.laziji;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class RectGroup {

    private Rect left;
    private Rect right;
    private float y;
    private float dy=0;
    private float speed;
    private boolean pass=false;
    private ModelInstance leftColInstance;
    private ModelInstance middleColInstance;
    private ModelInstance rightColInstance;

    public RectGroup(Rect left, Rect right, float y, float speed, ModelInstance leftColInstance, ModelInstance middleColInstance, ModelInstance rightColInstance) {
        this.left = left;
        this.right = right;
        this.y = y;
        this.speed = speed;
        this.leftColInstance = leftColInstance;
        this.middleColInstance = middleColInstance;
        this.rightColInstance = rightColInstance;
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

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public ModelInstance getLeftColInstance() {
        return leftColInstance;
    }

    public void setLeftColInstance(ModelInstance leftColInstance) {
        this.leftColInstance = leftColInstance;
    }

    public ModelInstance getMiddleColInstance() {
        return middleColInstance;
    }

    public void setMiddleColInstance(ModelInstance middleColInstance) {
        this.middleColInstance = middleColInstance;
    }

    public ModelInstance getRightColInstance() {
        return rightColInstance;
    }

    public void setRightColInstance(ModelInstance rightColInstance) {
        this.rightColInstance = rightColInstance;
    }
}
