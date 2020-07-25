package com.namhto.lwjgl;

import org.joml.*;
import org.joml.Math;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;
import static org.lwjgl.system.MemoryUtil.NULL;

public class EntryPoint {

    private long window;

    private int width = 1280;

    private int height = 720;

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(width, height, "LWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

        GL.createCapabilities();
        setupDebugMessageCallback();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
    }

    private void run() {

        var square = new VertexArray(
                new float[]{
                        -0.5f, -0.5f, 0f,
                        0.5f, -0.5f, 0f,
                        0.5f, 0.5f, 0f,
                        -0.5f, 0.5f, 0f
                },
                new int[]{
                        0, 1, 2,
                        0, 2, 3
                },
                new float[]{
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                        1.0f, 1.0f,
                        0.0f, 1.0f
                }
        );

        var shader = new Shader("/shaders/basic.shader");
        var texture = new Texture("/textures/glass.png");
        texture.bind(0);
        shader.bind();
        shader.setUniform("u_Texture", 0);

        var renderer = new Renderer();

        var proj = new Matrix4f().perspective(Math.toRadians(70), 16f / 9, 0.1f, 10000);

        while (!glfwWindowShouldClose(window)) {
            renderer.clear();

            if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) viewAngle += 1;
            if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) viewAngle -= 1;
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) objectAngle += 1;
            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) objectAngle -= 1;

            var view = new Matrix4f().translationRotateScale(
                    new Vector3f(0, 0, 0),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), viewAngle),
                    new Vector3f(1, 1, 1)
            );

            var drawCalls = new ArrayList<DrawCall>();
            var dz = -1f;
            var c = new Vector4f(1, 0, 0, 0.5f);
            for (int i = 2; i < 10000; i++) {
                var z = i * dz;
                var color = c;
                drawCalls.add(
                        new DrawCall(square, shader, s -> {
                            var model = new Matrix4f().translationRotateScale(
                                    new Vector3f(-3, 0, z),
                                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), objectAngle),
                                    new Vector3f(1, 1, 1)
                            );
                            s.setUniform("u_Color", color);
                            s.setUniform("u_MVP", new Matrix4f(proj).mul(view).mul(model));
                        })
                );
                drawCalls.add(
                        new DrawCall(square, shader, s -> {
                            var model = new Matrix4f().translationRotateScale(
                                    new Vector3f(-1, 0, z),
                                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), objectAngle),
                                    new Vector3f(1, 1, 1)
                            );
                            s.setUniform("u_Color", color);
                            s.setUniform("u_MVP", new Matrix4f(proj).mul(view).mul(model));
                        })
                );
                drawCalls.add(
                        new DrawCall(square, shader, s -> {
                            var model = new Matrix4f().translationRotateScale(
                                    new Vector3f(1, 0, z),
                                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), objectAngle),
                                    new Vector3f(1, 1, 1)
                            );
                            s.setUniform("u_Color", color);
                            s.setUniform("u_MVP", new Matrix4f(proj).mul(view).mul(model));
                        })
                );
                drawCalls.add(
                        new DrawCall(square, shader, s -> {
                            var model = new Matrix4f().translationRotateScale(
                                    new Vector3f(3, 0, z),
                                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), objectAngle),
                                    new Vector3f(1, 1, 1)
                            );
                            s.setUniform("u_Color", color);
                            s.setUniform("u_MVP", new Matrix4f(proj).mul(view).mul(model));
                        })
                );
                if (c.x == 1) c = new Vector4f(0, 1, 0, 0.5f);
                else if (c.y == 1) c = new Vector4f(0, 0, 1, 0.5f);
                else if (c.z == 1) c = new Vector4f(1, 0, 0, 0.5f);
            }

            renderer.draw(drawCalls);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    float viewAngle = 0;
    float objectAngle = 0;

    public static void main(String[] args) {
        var entryPoint = new EntryPoint();
        entryPoint.init();
        entryPoint.run();
    }
}
