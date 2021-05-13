package game2_1;

import utility.Bounds2;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.Serializable;

public class Projectile implements Serializable {
    public static final float r = 5;

    public final float strength;

    Vector2 pos, vel;
    boolean delete;
    byte owner;

    Projectile(byte owner, float strength, Vector2 pos, Vector2 vel) {
        this.owner = owner;
        this.strength = strength;
        this.pos = pos;
        this.vel = vel;
    }

    boolean checkHit(Player player) {
        return Vector2.sub(player.pos, pos).magnitudeSqr() < (Player.r + r) * (Player.r + r);
    }

    void update(Bounds2 bounds) {
        pos.add(vel);

        if (!bounds.inBounds(pos))
            delete = true;
    }

    void show(PGraphics g) {
        g.push();

        g.stroke(255, 0, 0);
        g.noFill();

        if (strength != 0)
            g.ellipse(pos.x, pos.y, r * 2 * strength, r * 2);
        else
            g.ellipse(pos.x, pos.y, r * 2, r * 2);

        g.pop();
    }
}
