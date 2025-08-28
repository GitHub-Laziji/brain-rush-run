package io.github.laziji;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Rect {
    private float score;
    private String info;
    private ModelInstance bgInstance;
    private ModelInstance txtInstance;
    private boolean pass=false;

    public Rect(float score, String info,ModelInstance bgInstance,ModelInstance txtInstance) {
        this.score = score;
        this.info = info;
        this.bgInstance=bgInstance;
        this.txtInstance=txtInstance;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ModelInstance getBgInstance() {
        return bgInstance;
    }

    public void setBgInstance(ModelInstance bgInstance) {
        this.bgInstance = bgInstance;
    }

    public ModelInstance getTxtInstance() {
        return txtInstance;
    }

    public void setTxtInstance(ModelInstance txtInstance) {
        this.txtInstance = txtInstance;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
