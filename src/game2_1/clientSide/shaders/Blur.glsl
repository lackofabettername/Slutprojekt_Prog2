#version 120

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

//The amount of pixels offseted from the center pixel that are sampled.
//so the total amount of pixels is size*2 (left/right or up/down) + 1 (center)
#define size 4

//Standard uniforms from processing
uniform sampler2D texture;
uniform vec2 texOffset;

//Custom uniform
uniform int vertical;
uniform float strength;

//Constant
uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

//Also from processing
varying vec4 vertColor;
varying vec4 vertTexCoord;

vec4 sample_(vec2 st) {
    vec4 c = texture2D(texture, st) * vertColor;
    return c;
}

void main() {
    vec2 offset = texOffset * (vertical == 0 ? vec2(1.0, 0.0) : vec2(0.0, 1.0));
    //offset *= 20;

    vec4 averageColor = texture2D(texture, vertTexCoord.st) * vertColor * weight[0];
    for (int i = 1; i <= size; i += 1) {
        averageColor += sample_(vertTexCoord.st + vec2(i) * offset) * weight[i];
        averageColor += sample_(vertTexCoord.st - vec2(i) * offset) * weight[i];
    }

    averageColor *= strength;
    averageColor.rgb = pow(averageColor.rgb, vec3(1 / 2.2));


    gl_FragColor = averageColor;
}