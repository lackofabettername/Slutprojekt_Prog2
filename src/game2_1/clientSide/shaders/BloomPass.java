package game2_1.clientSide.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BloomPass implements Pass {
    private final PApplet parent;
    private final BlurPass blurPass;

    public BloomPass(PApplet parent) {
        this.parent = parent;
        blurPass = new BlurPass(parent, false);
        reload();
    }

    public void reload() {
        blurPass.reload();
    }

    @Override
    public void prepare(Supervisor supervisor) {
        // set parameters of the shader if needed
    }

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

//        supervisor.clearPass(cp);
//        cp.beginDraw();
//        this.brightPass.setThreshold(this.threshold);
//        this.brightPass.prepare(supervisor);
//        cp.shader(this.brightPass.shader);
//        cp.image(np, 0.0F, 0.0F);
//        cp.endDraw();

//        this.blurPass.setSigma(this.sigma);
//        cp.beginDraw();
//        this.blurPass.setHorizontal(true);
//        this.blurPass.prepare(supervisor);
//        cp.shader(this.blurPass.shader);
//        cp.image(cp, 0.0F, 0.0F);
//        cp.endDraw();
//
//        cp.beginDraw();
//        this.blurPass.setHorizontal(false);
//        this.blurPass.prepare(supervisor);
//        cp.shader(this.blurPass.shader);
//        cp.image(cp, 0.0F, 0.0F);
//        cp.endDraw();
//
//        np.beginDraw();
//        np.blendMode(256);
//        np.image(cp, 0.0F, 0.0F);
//        np.blendMode(1);
//        np.endDraw();
    }
}
