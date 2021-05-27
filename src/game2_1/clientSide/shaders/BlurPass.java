package game2_1.clientSide.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

/**
 * Blur shader.
 * @see processing.opengl.PShader
 * @see ch.bildspur.postfx.PostFXSupervisor
 * @see ch.bildspur.postfx.builder.PostFX
 */
public class BlurPass implements Pass {
    private final PApplet parent;
    protected PShader shader;

    private boolean vertical;
    private float strength;

    /**
     *
     * @param parent needed for loading and compiling the shader
     * @param vertical if the shader blurs vertically or horizontally.
     */
    public BlurPass(PApplet parent, boolean vertical) {
        this.parent = parent;
        this.vertical = vertical;
        reload();
        setStrength(1);
    }

    /**
     * Reload the shader from it's source file and recompile it.
     */
    public void reload() {
        shader = parent.loadShader("src/game2_1/clientSide/shaders/Blur.glsl");
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    /**
     * @param strength Each pixel's color is multiplied by this,
     *                 a value of 1 means a normal blur shader,
     *                 lower than 1 means everything is darker,
     *                 greater than 1 means everything is brighter.
     */
    public void setStrength(float strength) {
        this.strength = strength;
    }

    /**
     * Prepares the shader, sets the values of the shader's uniforms
     */
    @Override
    public void prepare(Supervisor supervisor) {
        shader.set("vertical", vertical ? 1 : 0);
        shader.set("strength", strength);
    }

    /**
     * Applies the shader to the graphicsbuffer using the given Supervisor.
     * @see PGraphics
     */
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
