package game2_1.serverSide;

import utility.Bounds2;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.Serializable;
import java.util.UUID;

public class Projectile implements Serializable {
    public final short id;

    public static final float r = 5;

    public final float strength;

    public Vector2 pos, vel;
    public boolean delete; //todo: transient?
    public byte owner;

    private static short teasmdopsa = 0;

    public Projectile(byte owner, float strength, Vector2 pos, Vector2 vel, short id) {
        this.owner = owner;
        this.strength = strength;
        this.pos = pos;
        this.vel = vel;
        this.id = id;
    }

    public Projectile(byte owner, float strength, Vector2 pos, Vector2 vel) {
        this(owner, strength, pos, vel, teasmdopsa++);
    }

    public boolean checkHit(PlayerLogic playerLogic) {
        return Vector2.sub(playerLogic.pos, pos).magnitudeSqr() < (PlayerLogic.r + r) * (PlayerLogic.r + r);
    }

    public void update(Bounds2 bounds) {
        pos.add(vel);

        if (!bounds.inBounds(pos))
            delete = true;
    }

    public void render(PGraphics g) {
        g.push();

        g.stroke(255, 0, 0);
        g.noFill();

        if (strength != 0)
            g.ellipse(pos.x, pos.y, r * 2 * strength, r * 2);
        else
            g.ellipse(pos.x, pos.y, r * 2, r * 2);

        g.pop();
    }

    @Override
    public String toString() {
        return id + " " + (delete ? "dead" : "alive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Projectile that = (Projectile) o;

        return id == that.id;
    }
    @Override
    public int hashCode() {
        return id;
    }
}