package game2_1;

import game2_1.events.InputEvent;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import utility.*;

import processing.core.PGraphics;

import java.io.Serializable;
import java.util.*;

/**
 * The player. The users directly control instances of this class.
 */
public class Player implements Serializable {
    private final transient GameState parent;
    private final transient byte id;

    public static final float r = 20;
    Vector2 pos, vel;
    Color col;
    transient ParticleSystem particles;

    Vector2 cursor;

    public final transient HashMap<Character, Movement> keyBinds;
    private final EnumSet<Movement> movement;

    //region Constructors
    Player(byte id, GameState parent) {
        this(
                id,
                parent,
                new Vector2(40),
                new Vector2(0, 0),
                new Color(ColorMode.HSVA, MathF.GoldenRatio * id % 1, 1, 1, 1),
                null,
                new Vector2()
        );
    }
    Player(byte id, GameState parent, Vector2 pos, Vector2 vel, Color color, ParticleSystem particleSystem, Vector2 cursor) {
        this.id = id;
        this.parent = parent;
        this.pos = pos;
        this.vel = vel;
        this.col = color;
        this.particles = particleSystem;
        this.cursor = cursor;

        keyBinds = new HashMap<>(Map.of(
                'w', Movement.Up,
                'a', Movement.Left,
                's', Movement.Down,
                'd', Movement.Right
        ));
        movement = EnumSet.noneOf(Movement.class);
    }

    public void createParticleSystem() {
        if (particles != null)
            return;

        particles = new ParticleSystem(pos, col);
    }
    public void createParticleSystem(ParticleSystem ps) {
        if (particles != null || ps == null)
            return;

        particles = ps;
        particles.pos = pos;
        particles.color = col;
    }
    //endregion

    void update(float deltaTime, Bounds2 bounds) {
        Vector2 movementForce = new Vector2();
        for (Movement movement : movement)
            movementForce.add(movement.force);
        movementForce.normalize();
        if (movementForce.dot(Vector2.normalize(vel)) < 0)
            movementForce.mult(2);

        movementForce.mult(50f * deltaTime);

        //vel.add(Vector2.mult(Vector2.sub(pos, 450, 300), -0.001f));
        //vel.mult(1 - 0.8f * deltaTime);
        vel.mult(1 - 0.99999f * deltaTime);

        vel.add(movementForce);

        pos.add(vel);


        if (pos.x < bounds.x) vel.x *= -1;
        if (pos.x > bounds.x + bounds.w) vel.x *= -1;
        if (pos.y < bounds.y) vel.y *= -1;
        if (pos.y > bounds.y + bounds.h) vel.y *= -1;

        pos.x = MathF.bounce(pos.x, bounds.x, bounds.x + bounds.w);
        pos.y = MathF.bounce(pos.y, bounds.y, bounds.y + bounds.h);
    }

    void checkIfHit(Collection<Projectile> projectiles) {
        //TODO: more efficient search

        if (projectiles instanceof List temp) {
            for (ListIterator<Projectile> iterator = temp.listIterator(); iterator.hasNext(); ) {
                Projectile projectile = iterator.next();

                if (projectile == null) continue;
                if (projectile.owner == id) continue;
                if (projectile.delete) continue;

                if (projectile.checkHit(this)) {
                    iterator.set(null);

                    parent.scores.compute(projectile.owner, (ignored, oldScore) ->
                            (int) (projectile.strength * 100) + (oldScore != null ? oldScore : 0)
                    );

                    Debug.log("hit");
                }
            }
        } else {
            Debug.logWarning("Todo");
        }
    }

    void handleEvent(InputEvent event) {
        if (event instanceof KeyEvent keyEvent) {

            switch (keyEvent.Type) {
                case KeyTyped -> {
                }
                case KeyPressed -> {
                    if (keyBinds.containsKey(keyEvent.Key))
                        movement.add(keyBinds.get(keyEvent.Key));
                }
                case KeyReleased -> {
                    if (keyBinds.containsKey(keyEvent.Key))
                        movement.remove(keyBinds.get(keyEvent.Key));
                }
            }
        } else if (event instanceof MouseEvent mouseEvent) {

            switch (mouseEvent.Type) {
                case MouseMoved -> {
                    cursor.set(mouseEvent.MouseX, mouseEvent.MouseY);
                }
                case MouseDragged -> {
                }
                case MouseButtonClicked -> {
                }
                case MouseButtonPressed -> {
                    if (parent.music.playing) {
                        parent.projectiles.add(
                                new Projectile(
                                        id,
                                        parent.beats.getStrength(
                                                (byte) 0,
                                                parent.music.getMicrosecondPosition() / 1000
                                        ),
                                        pos.copy(),
                                        Vector2.sub(cursor, pos).setMagnitude(15)
                                )
                        );
                    } else {
                        parent.projectiles.add(
                                new Projectile(
                                        id,
                                        0,
                                        pos.copy(),
                                        Vector2.sub(cursor, pos).setMagnitude(15)
                                )
                        );
                    }
                }
                case MouseButtonReleased -> {
                }
                case MouseWheel -> {
                }
            }
        }
    }

    void render(PGraphics g) {
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

    @Override
    public String toString() {
        return "Player{" +
                "pos=" + pos +
                ", vel=" + vel +
                '}';
    }

    enum Movement {
        Up(0, -1),
        Down(0, 1),
        Left(-1, 0),
        Right(1, 0);

        Vector2 force;
        Movement(float x, float y) {
            force = new Vector2(x, y);
        }

        Movement oposite() {
            return switch (this) {
                case Up -> Down;
                case Down -> Up;
                case Left -> Right;
                case Right -> Left;
            };
        }
    }
}
