package game2_1.clientSide.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.BasePass;
import ch.bildspur.postfx.pass.Pass;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BlurPass implements Pass {
    private final PApplet parent;
    PShader shader;
    private boolean dir;

    public BlurPass(PApplet parent, boolean dir) {
        this.parent = parent;
        this.dir = dir;
        reload();
    }

    public void reload() {
        shader = parent.loadShader("src/game2_1/clientSide/shaders/Blur.glsl");
        //shader.set("textureW", parent.width);
        ////noinspection SuspiciousNameCombination
        //shader.set("textureH", parent.height);

        shader.set("canvasW", (float) parent.width);
        shader.set("canvasH", (float) parent.height);
        shader.set("dir", dir ? 1 : 0);
    }

    public void setDir(boolean dir) {
        this.dir = dir;
        shader.set("dir", dir ? 1 : 0);
    }

    @Override
    public void prepare(Supervisor supervisor) {
        // set parameters of the shader if needed
    }

    @Override
    public void apply(Supervisor supervisor) {
        PGraphics pass = supervisor.getNextPass();
        supervisor.clearPass(pass);

        pass.beginDraw();
        pass.shader(shader);
        pass.image(supervisor.getCurrentPass(), 0, 0);
        pass.endDraw();
    }
}
