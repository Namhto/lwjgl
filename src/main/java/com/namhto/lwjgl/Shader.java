package com.namhto.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int rendererId;

    private final Map<String, Integer> uniformLocations = new HashMap<>();

    public Shader(String filePath) {
        try {
            var sources = parseShader(filePath);
            rendererId = createShader(sources.vertexShader, sources.fragmentShader);
        } catch (URISyntaxException e) {
            var message = String.format("No file found with path '%s'", filePath);
            System.err.println(message);
        } catch (Exception e) {
            var message = String.format("An error occurred while reading file '%s'", filePath);
            System.err.println(message);
        }
    }

    public void bind() {
        glUseProgram(rendererId);
    }

    public void unBind() {
        glUseProgram(0);
    }

    public void delete() {
        glDeleteProgram(rendererId);
    }

    public void setUniform(String name, Vector4f value) {
        var location = getUniformLocation(name);
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void setUniform(String name, int value) {
        var location = getUniformLocation(name);
        glUniform1i(location, value);
    }

    public void setUniform(String name, Matrix4f value) {
        var location = getUniformLocation(name);
        glUniformMatrix4fv(location, false, value.get(new float[4 * 4]));
    }

    private int getUniformLocation(String name) {
        return uniformLocations.computeIfAbsent(name, s -> glGetUniformLocation(rendererId, name));
    }

    private Sources parseShader(String filePath) throws Exception {
        var path = Path.of(this.getClass().getResource(filePath).toURI());
        var currentType = "";
        var vertexBuilder = new StringJoiner("\n");
        var fragmentBuilder = new StringJoiner("\n");
        for (var line : Files.readAllLines(path)) {
            if (line.startsWith("#shader")) {
                var type = line.substring(line.indexOf("#shader ") + 8);
                if (type.equals("vertex") || type.equals("fragment")) {
                    currentType = type;
                }
            } else {
                if (currentType.equals("vertex")) {
                    vertexBuilder.add(line);
                }
                if (currentType.equals("fragment")) {
                    fragmentBuilder.add(line);
                }
            }
        }
        return new Sources(vertexBuilder.toString(), fragmentBuilder.toString());
    }

    private int createShader(String vertexShaderSource, String fragmentShaderSource) {
        int program = glCreateProgram();
        var vertexShader = compileShader(vertexShaderSource, GL_VERTEX_SHADER);
        glAttachShader(program, vertexShader);
        int fragmentShader = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        glValidateProgram(program);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return program;
    }

    private int compileShader(String source, int type) {
        int id = glCreateShader(type);
        glShaderSource(id, source);
        glCompileShader(id);
        int compilationResult = glGetShaderi(id, GL_COMPILE_STATUS);
        if (compilationResult == GL_FALSE) {
            var message = glGetShaderInfoLog(id);
            System.err.println(message);
            glDeleteShader(id);
            return 0;
        }
        return id;
    }

    private static class Sources {
        String vertexShader;
        String fragmentShader;

        public Sources(String vertexShader, String fragmentShader) {
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
        }
    }
}
