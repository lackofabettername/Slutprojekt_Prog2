package game2_1.clientSide.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Bloom shader. Also known as glow shader. This is a rewritten version of {@link ch.bildspur.postfx.pass.BloomPass}
 * @see processing.opengl.PShader
 * @see ch.bildspur.postfx.PostFXSupervisor
 * @see ch.bildspur.postfx.builder.PostFX
 */
public class BloomPass implements Pass {
    private final PApplet parent;
    private final BlurPass blurPass;
    private float strength;

    /**
     * @param parent needed to load and compile the shaders.
     */
    public BloomPass(PApplet parent) {
        this.parent = parent;
        blurPass = new BlurPass(parent, false);
        reload();
        strength = 1;
    }

    /**
     * Reload the shader from it's source file and recompile it.
     */
    public void reload() {
        blurPass.reload();
    }

    /**
     * @param strength Each pixel in the "glow effect" is multiplied by this value.
     *                 0 means no glow just the source image,
     *                 1 means normal glow,
     *                 higher than 1 can look jagged and reduce the antialiasing.
     */
    public void setStrength(float strength) {
        this.strength = strength;
    }

    @Override
    public void prepare(Supervisor supervisor) {
        //Not used for anything
    }

    /**
     * Applies the shader to the graphicsbuffer using the given Supervisor.
     * @see PGraphics
     */
    @Override
    public void apply(Supervisor supervisor) {
        PGraphics np = supervisor.getNextPass();
        PGraphics cp = supervisor.getCurrentPass();

        //Copy the currentPass into the nextPass
        supervisor.clearPass(np);
        np.beginDraw();
        np.blendMode(1);
        np.image(cp, 0.0F, 0.0F);
        np.endDraw();

        blurPass.setStrength(strength);
        cp.beginDraw();
        this.blurPass.setVertical(true);
        this.blurPass.prepare(supervisor);
        cp.shader(this.blurPass.shader);
        cp.image(cp, 0.0F, 0.0F);
        cp.endDraw();

        cp.beginDraw();
        this.blurPass.setVertical(false);
        this.blurPass.prepare(supervisor);
        cp.shader(this.blurPass.shader);
        cp.image(cp, 0.0F, 0.0F);
        cp.endDraw();

        np.beginDraw();
        np.blendMode(256); //PConstants.SCREEN
        np.image(cp, 0.0F, 0.0F);
        np.blendMode(1);   //PConstants.BLEND
        np.endDraw();
    }
}
