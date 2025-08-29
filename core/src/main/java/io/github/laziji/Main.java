package io.github.laziji;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;
import io.github.laziji.constant.Problem;
import io.github.laziji.tool.FontTools;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {

    private final float RUNWAY_HEIGHT = 300;
    private final float RUNWAY_WIDTH = 20;
    private final float RECT_GROUP_NUM = 15;
    private final float RECT_GROUP_SPEED = 20;
    private final float RECT_GROUP_HEIGHT = 5;
    private final float RECT_GROUP_COL_WIDTH = 0.5f;
    private Runway runway;
    private Role role;
    private Deque<RectGroup> rectGroups = new ArrayDeque<>();

    private ModelBuilder modelBuilder = new ModelBuilder();
    private PerspectiveCamera camera;
    private Model trackModel;
    private Model trackBgModel;
    private Model rectModel;
    private Model roleModel;
    private Model rectGroupColModel;
    private ModelInstance trackInstance;
    private ModelInstance trackBgInstance;
    private ModelInstance roleInstance;
    private ModelBatch modelBatch;
    private BitmapFont font;
    private Environment environment;

    private AnimationController animationController;

    @Override
    public void create() {

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, -5, 10); // 从正上方俯视
        camera.direction.set(0, 2, -1); // 看向下方
        camera.up.set(0, 0, 1); // 调整上方方向
        camera.update();

        runway = new Runway(RUNWAY_WIDTH, RUNWAY_HEIGHT);
        trackModel = modelBuilder.createRect(
                runway.getMaxX() / 2, 0, 0,
                runway.getMaxX() / 2, runway.getMaxY(), 0,
                -runway.getMaxX() / 2, runway.getMaxY(), 0,
                -runway.getMaxX() / 2, 0, 0,
                0, 0, -1,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        trackInstance = new ModelInstance(trackModel);
        trackBgModel = modelBuilder.createRect(
                runway.getMaxX() / 2+RECT_GROUP_COL_WIDTH/2, 0, 0,
                runway.getMaxX() / 2+RECT_GROUP_COL_WIDTH/2, runway.getMaxY(), 0,
                -runway.getMaxX() / 2-RECT_GROUP_COL_WIDTH/2, runway.getMaxY(), 0,
                -runway.getMaxX() / 2-RECT_GROUP_COL_WIDTH/2, 0, 0,
                0, 0, -1,
                new Material(ColorAttribute.createDiffuse(new Color(0xfafafaff))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        trackBgInstance = new ModelInstance(trackBgModel);

        Texture texture = new Texture(Gdx.files.internal("assets/rect-bg.png"), true); // 第二个参数开启mipmap
        Material material = new Material();
        material.set(TextureAttribute.createDiffuse(texture));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        rectModel = modelBuilder.createRect(
                0, 0, 0,
                runway.getMaxX() / 2, 0, 0,
                runway.getMaxX() / 2, 0, RECT_GROUP_HEIGHT,
                0, 0, RECT_GROUP_HEIGHT,
                0, 1, 0,
                material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        rectGroupColModel = modelBuilder.createBox(
                0.5f, 0.5f, RECT_GROUP_HEIGHT,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        for (int i = 0; i < RECT_GROUP_NUM; i++) {
            rectGroups.add(newRectGroup());
        }

        role = new Role(0, 5, 2, 4, 0, 10);
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        roleModel = modelLoader.loadModel(Gdx.files.internal("assets/role.g3db"));  // 替换为你的模型文件名
        roleInstance = new ModelInstance(roleModel);
        roleInstance.transform.scl(0.02f);
        roleInstance.transform.rotate(Vector3.X, MathUtils.degreesToRadians * 90 * 60);
        roleInstance.transform.rotate(Vector3.Y, MathUtils.degreesToRadians * 170 * 60);
        roleInstance.transform.translate(-role.getX() / roleInstance.transform.getScaleX(), 0, role.getY() / roleInstance.transform.getScaleY());

        // 初始化动画控制器
        animationController = new AnimationController(roleInstance);
        // 检查模型是否包含动画
        if (roleModel.animations.size > 0) {
            String animName = roleModel.animations.get(1).id;
            // 播放动画：参数依次为动画名称、是否循环、速度、回调、延迟
            animationController.animate(animName, -1, 1f, null, 0f);
            Gdx.app.log("Animation", "播放动画: " + animName);
        } else {
            Gdx.app.log("Animation", "模型不包含动画数据！");
        }

        modelBatch = new ModelBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        // 设置环境光照
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor((float) 0xE3 / 0xFF, (float) 0x8E / 0xFF, (float) 0x14 / 0xFF, 1); // 浅蓝色背景
        handleInput();
        modelBatch.begin(camera);
        modelBatch.render(trackBgInstance, environment);
        modelBatch.render(trackInstance, environment);
        drawRect();
        drawRole();
        modelBatch.end();
    }

    private void drawRole() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        animationController.update(deltaTime);
        roleInstance.transform.translate(-role.getDx() / roleInstance.transform.getScaleX(), 0, role.getDy() / roleInstance.transform.getScaleY());
        modelBatch.render(roleInstance, environment);
    }

    private void drawRect() {
        float deltaTimeTime = Gdx.graphics.getDeltaTime();
        rectGroups.forEach(o -> {
            o.setDy(-o.getSpeed() * deltaTimeTime);
            o.setY(o.getY() + o.getDy());
            if (o.getY() < role.getY() && !o.isPass()) {
                o.setPass(true);
                Rect rect = role.getX() >= 0 ? o.getRight() : o.getLeft();
                rect.setPass(true);
                role.setScore(rect.getProblem().getScoreFunc().apply(role.getScore()));
            }
        });
        while (!rectGroups.isEmpty() && rectGroups.peek().getY() < 0) {
            rectGroups.pop();
            rectGroups.addLast(newRectGroup());
        }
        rectGroups.forEach(o -> {
            if (!o.getLeft().isPass()) {
                o.getLeft().getBgInstance().transform.setToTranslation(-runway.getMaxX() / 2, o.getY(), 0);
                modelBatch.render(o.getLeft().getBgInstance(), environment);
                o.getLeft().getTxtInstance().transform.setToTranslation(-runway.getMaxX() / 2, o.getY(), 0);
                modelBatch.render(o.getLeft().getTxtInstance(), environment);
            }
            if (!o.getRight().isPass()) {
                o.getRight().getBgInstance().transform.setToTranslation(0, o.getY(), 0);
                modelBatch.render(o.getRight().getBgInstance(), environment);
                o.getRight().getTxtInstance().transform.setToTranslation(0, o.getY(), 0);
                modelBatch.render(o.getRight().getTxtInstance(), environment);
            }
            o.getLeftColInstance().transform.setToTranslation(-runway.getMaxX() / 2, o.getY(), RECT_GROUP_HEIGHT / 2);
            modelBatch.render(o.getLeftColInstance(), environment);
            o.getMiddleColInstance().transform.setToTranslation(0, o.getY(), RECT_GROUP_HEIGHT / 2);
            modelBatch.render(o.getMiddleColInstance(), environment);
            o.getRightColInstance().transform.setToTranslation(runway.getMaxX() / 2, o.getY(), RECT_GROUP_HEIGHT / 2);
            modelBatch.render(o.getRightColInstance(), environment);
        });
    }

    private synchronized void handleInput() {
        role.setDx(0);
        role.setDy(0);
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            role.setDx(-role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            role.setDx(role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            role.setDy(role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            role.setDy(-role.getSpeed() * deltaTime);
        }
        if (role.getX() + role.getDx() > runway.getMaxX() / 2 - role.getWidth() / 2) {
            role.setDx(runway.getMaxX() / 2 - role.getWidth() / 2 - role.getX());
        }
        if (role.getX() + role.getDx() < -runway.getMaxX() / 2 + role.getWidth() / 2) {
            role.setDx(-runway.getMaxX() / 2 + role.getWidth() / 2 - role.getX());
        }
        if (role.getY() + role.getDy() > 20) {
            role.setDy(20 - role.getY());
        }
        if (role.getY() + role.getDy() < 5) {
            role.setDy(5 - role.getY());
        }
        role.setX(role.getX() + role.getDx());
        role.setY(role.getY() + role.getDy());
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        trackModel.dispose();
    }


    private ModelInstance createTextureInstance(String text) {
        Texture textTexture = FontTools.createTexture(FontTools.DEFAULT_FONT, text, 180, 120);
        Material material = new Material();
        material.set(TextureAttribute.createDiffuse(textTexture));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        Model model = modelBuilder.createRect(
                0, 0, 0,
                runway.getMaxX() / 2, 0, 0,
                runway.getMaxX() / 2, 0, 5,
                0, 0, 5,
                0, 1, 0,
                material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        return new ModelInstance(model);
    }


    private RectGroup newRectGroup() {
        return new RectGroup(newRect(), newRect(), (rectGroups.isEmpty() ? 0 : rectGroups.getLast().getY()) + runway.getMaxY() / RECT_GROUP_NUM, RECT_GROUP_SPEED, new ModelInstance(rectGroupColModel), new ModelInstance(rectGroupColModel), new ModelInstance(rectGroupColModel));
    }

    private Rect newRect() {
        Problem problem = Problem.random();
        ModelInstance textureInstance = createTextureInstance(problem.getInfo());
        return new Rect(problem, new ModelInstance(rectModel), textureInstance);
    }
}
