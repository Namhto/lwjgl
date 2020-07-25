package com.namhto.lwjgl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {

    private final int rendererId;

    private int width;

    private int height;

    private ByteBuffer buffer;

    public Texture(String filePath) {
        loadImage(filePath);
        rendererId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, rendererId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        unBind();
    }

    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, rendererId);
    }

    public void unBind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete() {
        glDeleteTextures(rendererId);
    }

    private void loadImage(String filePath) {
        try {
            var image = ImageIO.read(this.getClass().getResource(filePath));
            width = image.getWidth();
            height = image.getHeight();
            var pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            buffer = ByteBuffer.allocateDirect(width * height * 4);

            for(var h = height - 1; h >= 0; h--) {
                for(var w = 0; w < width; w++) {
                    var pixel = pixels[h * width + w];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            buffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
