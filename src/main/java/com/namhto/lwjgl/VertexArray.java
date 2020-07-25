package com.namhto.lwjgl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class VertexArray {

    private final int rendererId;

    private final float[] positions;

    private final int[] indices;

    private final float[] textureCoords;

    public VertexArray(float[] positions, int[] indices, float[] textureCoords) {
        this.positions = positions;
        this.indices = indices;
        this.textureCoords = textureCoords;
        rendererId = glGenVertexArrays();
        bind();
        linkPositions();
        linkTextureCoords();
        linkIndices();
        unBind();
    }

    private void linkPositions() {
        var bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    }

    private void linkTextureCoords() {
        var bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, textureCoords, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    }

    private void linkIndices() {
        var bufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void bind() {
        glBindVertexArray(rendererId);
    }

    public void unBind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(rendererId);
    }

    public int getIndicesCount() {
        return indices.length;
    }
}
