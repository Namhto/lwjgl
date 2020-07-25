package com.namhto.lwjgl;

import java.util.function.Consumer;

public class DrawCall {

    private final VertexArray vertexArray;

    private final Shader shader;

    private final Consumer<Shader> prepareShader;

    public DrawCall(VertexArray vertexArray, Shader shader, Consumer<Shader> prepareShader) {
        this.vertexArray = vertexArray;
        this.shader = shader;
        this.prepareShader = prepareShader;
    }

    public VertexArray getVertexArray() {
        return vertexArray;
    }

    public Shader getShader() {
        return shader;
    }

    public Consumer<Shader> getPrepareShader() {
        return prepareShader;
    }
}
