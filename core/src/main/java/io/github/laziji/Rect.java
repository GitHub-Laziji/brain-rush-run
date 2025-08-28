package io.github.laziji;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.laziji.constant.Problem;

public class Rect {
    private Problem problem;
    private ModelInstance bgInstance;
    private ModelInstance txtInstance;
    private boolean pass=false;

    public Rect(Problem problem, ModelInstance bgInstance, ModelInstance txtInstance) {
        this.problem = problem;
        this.bgInstance=bgInstance;
        this.txtInstance=txtInstance;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
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
