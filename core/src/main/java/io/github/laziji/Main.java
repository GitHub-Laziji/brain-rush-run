package io.github.laziji;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.laziji.tool.FontTools;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {

    private final float RECT_GROUP_NUM = 15;
    private final float RECT_GROUP_SPEED = 15;
    private final float RUNWAY_HEIGHT = 300;
    private final float RUNWAY_WIDTH = 20;
    private Runway runway;
    private Role role;
    private Deque<RectGroup> rectGroups = new ArrayDeque<>();
    private Random random;

    private ModelBuilder modelBuilder = new ModelBuilder();
    private PerspectiveCamera camera;
    private Model trackModel;
    private ModelInstance trackInstance;
    private Model rectModel;
    private Model roleModel;
    private ModelInstance roleInstance;
    private ModelBatch modelBatch;
    private BitmapFont font;
    private Environment environment;

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

        random = new Random();

        Texture texture = new Texture(Gdx.files.internal("assets/rect-bg.png"), true); // 第二个参数开启mipmap
        Material material = new Material();
        material.set(TextureAttribute.createDiffuse(texture));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        rectModel = modelBuilder.createRect(
                0, 0, 0,
                runway.getMaxX() / 2, 0, 0,
                runway.getMaxX() / 2, 0, 5,
                0, 0, 5,
                0, 1, 0,
                material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        for (int i = 0; i < RECT_GROUP_NUM; i++) {
            rectGroups.add(new RectGroup(newRect(), newRect(), (i + 1) * runway.getMaxY() / RECT_GROUP_NUM, RECT_GROUP_SPEED));
        }

        role = new Role(0, 5, 2, 4, 0, 10);
        roleModel = modelBuilder.createBox(
                2f, 2f, 4f, // 宽度、高度、深度
                new Material(ColorAttribute.createDiffuse(Color.RED)), // 材质（红色）
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal // 顶点属性
        );

        roleInstance = new ModelInstance(roleModel);
        roleInstance.transform.setToTranslation(0, 5, 2f);

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
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.8f, 1); // 浅蓝色背景
        handleInput();
        modelBatch.begin(camera);
        modelBatch.render(trackInstance, environment);
        drawRect();
        drawRole();
        modelBatch.end();
    }

    private void drawRole() {
        roleInstance.transform.setToTranslation(role.getX(), role.getY(), 2f);
        modelBatch.render(roleInstance, environment);
    }

    private void drawRect() {
        float deltaTimeTime = Gdx.graphics.getDeltaTime();
        rectGroups.forEach(o -> {
            o.setY(o.getY() - o.getSpeed() * deltaTimeTime);
            if (o.getY() < role.getY() && !o.isPass()) {
                o.setPass(true);
                if (role.getX() >= 0) {
                    o.getRight().setPass(true);
                } else {
                    o.getLeft().setPass(true);
                }
            }
        });
        while (!rectGroups.isEmpty() && rectGroups.peek().getY() < 0) {
            rectGroups.pop();
            rectGroups.addLast(new RectGroup(newRect(), newRect(), rectGroups.getLast().getY() + runway.getMaxY() / RECT_GROUP_NUM, RECT_GROUP_SPEED));
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
        });
    }

    private synchronized void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            role.setX(role.getX() - role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            role.setX(role.getX() + role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            role.setY(role.getY() + role.getSpeed() * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            role.setY(role.getY() - role.getSpeed() * deltaTime);
        }
        if (role.getX() > runway.getMaxX() / 2 - role.getWidth() / 2) {
            role.setX(runway.getMaxX() / 2 - role.getWidth() / 2);
        }
        if (role.getX() < -runway.getMaxX() / 2 + role.getWidth() / 2) {
            role.setX(-runway.getMaxX() / 2 + role.getWidth() / 2);
        }
        if (role.getY() > 20) {
            role.setY(20);
        }
        if (role.getY() < 5) {
            role.setY(5);
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        trackModel.dispose();
    }


    private ModelInstance createTextureInstance(String text) {
        Texture textTexture = FontTools.createTexture(FontTools.DEFAULT_FONT, text,100,100);
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


    private Rect newRect() {
        String info = "x*3";
        ModelInstance textureInstance = createTextureInstance(info);
        return new Rect((float) random.nextInt(100), info, new ModelInstance(rectModel), textureInstance);
    }
}
