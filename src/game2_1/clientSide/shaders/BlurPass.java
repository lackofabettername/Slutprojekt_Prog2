package game2_1.clientSide.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;
import utility.Debug;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BlurPass implements Pass {
    private final PApplet parent;
    PShader shader;

    private boolean vertical;
    private float strength;

    public BlurPass(PApplet parent, boolean vertical) {
        this.parent = parent;
        this.vertical = vertical;
        reload();
        setStrength(1);
    }

    public void reload() {
        shader = parent.loadShader("src/game2_1/clientSide/shaders/Blur.glsl");
        //shader.set("textureW", parent.width);
        ////noinspection SuspiciousNameCombination
        //shader.set("textureH", parent.height);

        shader.set("vertical", vertical ? 1 : 0);
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    @Override
    public void prepare(Supervisor supervisor) {
        shader.set("vertical", vertical ? 1 : 0);
        shader.set("strength", strength);
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
