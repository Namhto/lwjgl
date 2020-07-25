package com.namhto.lwjgl;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0f, 0f, 0f, 1f);
    }

    public void draw(List<DrawCall> drawCalls) {
        drawCalls.forEach(drawCall -> {
            var vao = drawCall.getVertexArray();
            var shader = drawCall.getShader();
            var prepareShader = drawCall.getPrepareShader();
            shader.bind();
            prepareShader.accept(shader);
            vao.bind();
            glDrawElements(GL_TRIANGLES, vao.getIndicesCount(), GL_UNSIGNED_INT, NULL);
            vao.unBind();
            shader.unBind();
        });
    }
}
