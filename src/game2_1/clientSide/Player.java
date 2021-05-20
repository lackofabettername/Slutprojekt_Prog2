package game2_1.clientSide;

import game2_1.GameState;
import game2_1.serverSide.PlayerLogic;
import utility.Color;
import utility.ColorMode;
import utility.MathF;
import utility.Vector2;

import processing.core.PGraphics;

public class Player extends PlayerLogic {
    public Color col;
    public transient ParticleSystem particles;

    public Player(PlayerLogic player, byte id) {
        this(
                id,
                player.parent,
                player.pos,
                player.vel,
                new Color(ColorMode.HSVA, MathF.GoldenRatio * id % 1, 1, 1, 1),
                null,
                player.cursor
        );
    }
    public Player(byte id, GameState parent, Vector2 pos, Vector2 vel, Color color, ParticleSystem particleSystem, Vector2 cursor) {
        super(id, parent, pos, vel, cursor);

        this.col = color;
        createParticleSystem(particleSystem);
    }

    public void createParticleSystem() {
        if (particles != null)
            return;

        particles = new ParticleSystem(pos, col);
    }
    public void createParticleSystem(ParticleSystem ps) {
        if (particles != null) {
            return;
        }
        if (ps == null) {
            createParticleSystem();
            return;
        }

        particles = ps;
        particles.pos = pos;
        particles.color = col;
    }

    public void render(PGraphics g) {
        g.push();

        if (particles == null)
            createParticleSystem();
        particles.update();
        particles.render(g);

        g.stroke(col.getRed(), col.getGreen(), col.getBlue());
        g.strokeWeight(3);
        g.noFill();

        g.ellipse(pos.x, pos.y, r * 2, r * 2);

        g.strokeWeight(1);
        g.line(cursor.x - 10, cursor.y, cursor.x + 10, cursor.y);
        g.line(cursor.x, cursor.y - 10, cursor.x, cursor.y + 10);

        g.pop();
    }

}
