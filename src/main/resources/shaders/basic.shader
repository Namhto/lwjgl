#shader vertex
#version 330 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 textureCoord;
uniform mat4 u_MVP;
out vec2 v_TextureCoord;

void main() {
    gl_Position = u_MVP * position;
    v_TextureCoord = textureCoord;
}

#shader fragment
#version 330 core

out vec4 color;
uniform vec4 u_Color;
uniform sampler2D u_Texture;
in vec2 v_TextureCoord;

void main() {
    vec4 textureColor = texture(u_Texture, v_TextureCoord);
    color = mix(textureColor, u_Color, 0.7);
}
