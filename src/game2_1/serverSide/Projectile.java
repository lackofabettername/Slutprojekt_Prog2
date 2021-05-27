package game2_1.serverSide;

import utility.Bounds2;
import utility.MathF;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.Serializable;

//TODO: create abstract Projectile class and make different projectile types that extend it.


/**
 * A projectile, shot by players.
 */
public class Projectile implements Serializable {
    public final short id;
    public final byte owner;

    public static final float RADIUS = 5;

    public final float strength;

    public Vector2 pos, vel;
    public boolean delete; //todo: transient?

    private static short projectileIDAccumulator = 0;

    public Projectile(byte owner, float strength, Vector2 pos, Vector2 vel, short id) {
        this.owner = owner;
        this.strength = strength;
        this.pos = pos;
        this.vel = vel;
        this.id = id;
    }

    public Projectile(byte owner, float strength, Vector2 pos, Vector2 vel) {
        this(owner, strength, pos, vel, projectileIDAccumulator++);
    }

    /**
     * @return true if this projectile has hit the given player.
     */
    public boolean checkHit(PlayerLogic playerLogic) {
        return Vector2.sub(playerLogic.pos, pos).magnitudeSqr() < (PlayerLogic.RADIUS + RADIUS) * (PlayerLogic.RADIUS + RADIUS);
    }

    /**
     * Update the projectile.
     *
     * @param bounds The bounds where this projectile can be, it is marked for deletion if it goes outside said bounds.
     */
    public void update(Bounds2 bounds) {
        pos.add(vel);

        if (!bounds.inBounds(pos))
            delete = true;
    }

    /**
     * Render this projectile to the PGraphics.
     */
    public void render(PGraphics g) {
        g.push();

        g.stroke(1, 0, 0);
        g.noFill();

        float d = RADIUS * 2 * MathF.lerp(strength, 1, 0.1f);
        if (strength == 0) d = RADIUS * 2;

        g.ellipse(pos.x, pos.y, d, d);


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
