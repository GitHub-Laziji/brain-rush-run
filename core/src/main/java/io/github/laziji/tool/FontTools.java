package io.github.laziji.tool;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class FontTools {

    public static final Map<Character, BufferedImage> FONT_B;
    public static final Map<Character, BufferedImage> FONT_W;
    public static final Map<Character, BufferedImage> DEFAULT_FONT;

    static {
        FONT_B = FontTools.load("assets/font.fnt", "assets/font-b.png"); //TODO
        FONT_W = FontTools.load("assets/font.fnt", "assets/font-w.png");
        DEFAULT_FONT = FONT_W;
    }

    private static Map<Character, BufferedImage> load(String fntPath, String pngPath) {
        Map<Character, BufferedImage> charFontMap = new HashMap<>();
        try {
            BufferedImage fontAtlasImage = ImageIO.read(new File(pngPath)); // 替换为实际路径
            try (FileInputStream fis = new FileInputStream(fntPath)) {
                for (String row : IOUtils.toString(fis).split("\n")) {
                    if (!row.startsWith("char id=")) {
                        continue;
                    }
                    try {
                        char ch = 0;
                        int charX = 0;
                        int charY = 0;
                        int charWidth = 0;
                        int charHeight = 0;
                        for (String col : row.split("\\s+")) {
                            if (col.startsWith("id=")) {
                                ch = (char) Integer.parseInt(col.substring("id=".length()));
                            } else if (col.startsWith("x=")) {
                                charX = Integer.parseInt(col.substring("x=".length()));
                            } else if (col.startsWith("y=")) {
                                charY = Integer.parseInt(col.substring("y=".length()));
                            } else if (col.startsWith("width=")) {
                                charWidth = Integer.parseInt(col.substring("width=".length()));
                            } else if (col.startsWith("height=")) {
                                charHeight = Integer.parseInt(col.substring("height=".length()));
                            }
                        }
                        BufferedImage charAImage = fontAtlasImage.getSubimage(charX, charY, charWidth, charHeight);
                        charFontMap.put(ch, charAImage);
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return charFontMap;
    }


    public static Texture createTexture(Map<Character, BufferedImage> font, String text, int width, int height) {
        int fontWidth = 0;
        int fontHeight = 0;
        for (char ch : text.toCharArray()) {
            BufferedImage src = font.get(ch);
            fontWidth += src.getWidth();
            fontHeight = Math.max(fontHeight, src.getHeight());
        }
        BufferedImage target = new BufferedImage(
                Math.max(fontWidth, width),
                Math.max(fontHeight, height),
                BufferedImage.TYPE_INT_ARGB
        );
        int[] targetPixels = new int[target.getWidth() * target.getHeight()];
        int subX = (target.getWidth() - fontWidth) / 2;
        int subY = (target.getHeight() - fontHeight) / 2;
        for (char ch : text.toCharArray()) {
            BufferedImage src = font.get(ch);
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int[] srcPixels = new int[srcWidth * srcHeight];
            src.getRGB(0, 0, srcWidth, srcHeight, srcPixels, 0, srcWidth);
            for (int y = 0; y < srcHeight; y++) {
                for (int x = 0; x < srcWidth; x++) {
                    int targetX = subX + x;
                    int targetY = subY + y;
                    if (targetX >= 0 && targetX < target.getWidth() && targetY < target.getHeight()) {
                        int targetIndex = targetY * target.getWidth() + targetX;
                        int srcIndex = y * srcWidth + x;
                        int srcArgb = srcPixels[srcIndex];
                        if ((srcArgb >>> 24) > 0) {
                            targetPixels[targetIndex] = srcArgb;
                        }
                    }
                }
            }
            subX += srcWidth;
        }
        target.setRGB(0, 0, target.getWidth(), target.getHeight(), targetPixels, 0, target.getWidth());
        return trans(target);
    }

    public static Texture trans(Image image) {
        // 1. 将Image转换为BufferedImage（确保可获取像素数据）
        BufferedImage bufferedImage = toBufferedImage(image);

        // 2. 创建与BufferedImage同尺寸的Pixmap
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // 3. 逐个像素复制数据（从BufferedImage到Pixmap）
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取ARGB格式的像素值
                int argb = bufferedImage.getRGB(x, y);

                // 转换ARGB到RGBA（LibGDX的Pixmap使用RGBA格式）
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int a = (argb >> 24) & 0xFF;

                // 计算RGBA颜色值（LibGDX的颜色格式）
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;

                // 设置Pixmap的像素
                pixmap.drawPixel(x, y, rgba);
            }
        }

        // 4. 从Pixmap创建Texture
        Texture texture = new Texture(pixmap);

        // 5. 释放Pixmap资源
        pixmap.dispose();

        return texture;
    }

    /**
     * 将Image转换为BufferedImage
     */
    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // 创建一个兼容的BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB // 使用带Alpha通道的格式
        );

        // 绘制图像到BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }
}
