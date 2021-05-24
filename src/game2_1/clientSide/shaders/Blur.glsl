#version 120

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform float canvasW, canvasH;
uniform int dir;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

varying vec4 vertColor;
varying vec4 vertTexCoord;

#define size 5

vec4 sample_(vec2 st) {
    vec4 c = texture2D(texture, st) * vertColor;
    return c;
}

void main() {
    //    vec4 c = texture2D(texture, vertTexCoord.st) * vertColor;
    //    float val = 0.007;
    //    c.r = (texture2D(texture, vertTexCoord.st - vec2(val, 0)) * vertColor).r;
    //    c.b = (texture2D(texture, vertTexCoord.st + vec2(val, 0)) * vertColor).g;


    vec2 offset = 1.0 / vec2(canvasW, canvasH);
    offset *= 2.0;

    vec4 c = texture2D(texture, vertTexCoord.st) * vertColor * weight[0];
    if (dir == 0) {
        for (int i = 1; i < size; i += 1) {
            c += sample_(vertTexCoord.st + vec2(i, 0.0) * offset) * weight[i];
        }
        for (int i = 1; i < size; i += 1) {
            c += sample_(vertTexCoord.st - vec2(i, 0.0) * offset) * weight[i];
        }
    } else {
        for (int j = 1; j < size; j += 1) {
            c += sample_(vertTexCoord.st + vec2(0.0, j) * offset) * weight[j];
        }
        for (int j = 1; j < size; j += 1) {
            c += sample_(vertTexCoord.st - vec2(0.0, j) * offset) * weight[j];
        }
    }
    //c /= size*2 + 1;
    //c *= 10;
    c.rgb = pow(c.rgb, vec3(1 / 2.2));


    gl_FragColor = c;
}