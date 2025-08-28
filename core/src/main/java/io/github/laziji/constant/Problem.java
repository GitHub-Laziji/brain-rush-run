package io.github.laziji.constant;

import java.util.Random;
import java.util.function.Function;

public enum Problem {

    CALC1("x+3", (x) -> x + 3),
    CALC2("x+1", (x) -> x + 1),
    CALC3("x-3", (x) -> x - 3),
    CALC4("x-1", (x) -> x - 1),
    CALC5("x-cos(pi)", (x) -> x + 1);

    private static final Random random = new Random();

    private String info;
    private Function<Float, Float> scoreFunc;

    Problem(String info, Function<Float, Float> scoreFunc) {
        this.info = info;
        this.scoreFunc = scoreFunc;
    }

    public static Problem random() {
        Problem[] values = values();
        return values[random.nextInt(values.length)];
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Function<Float, Float> getScoreFunc() {
        return scoreFunc;
    }

    public void setScoreFunc(Function<Float, Float> scoreFunc) {
        this.scoreFunc = scoreFunc;
    }
}
