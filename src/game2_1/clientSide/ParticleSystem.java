package game2_1.clientSide;

import processing.core.PGraphics;
import utility.Color;
import utility.MathF;
import utility.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A barebones ParticleSystem. This is not done.
 */
public class ParticleSystem {
    public Vector2 pos;
    public Color color;
    public ArrayList<Particle> particles;

    public ParticleSystem(Vector2 pos, Color color) {
        this.pos = pos;
        this.color = color;
        particles = new ArrayList<>();
    }

    public void update() {
        particles.add(new Particle(MathF.random(100, 200)));

        for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext(); ) {
            Particle particle = iterator.next();
            particle.update();

            if (particle.isDead())
                iterator.remove();
        }
    }

    public void render(PGraphics g) {
        g.push();
        g.translate(pos.x, pos.y);
        particles.forEach(p -> p.render(g));
        g.pop();
    }


    public class Particle {
        Vector2 pos;
        Vector2 vel;

        int life, startLife;

        public Particle(int life) {
            this.life = startLife = life;

            pos = new Vector2();
            vel = Vector2.random().mult(0.2f);
            //vel.set(1, 1);
        }

        public boolean isDead() {
            return life <= 0;
        }

        public void update() {
            --life;

            pos.add(vel);
        }

        public void render(PGraphics g) {
            g.stroke(color.getRed(), color.getGreen(), color.getBlue(), life / (float) startLife);
            g.noFill();

            g.ellipse(pos.x, pos.y, 5, 5);
        }
    }
}
