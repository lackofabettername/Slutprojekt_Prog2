package game2_1.serverSide;

import game2_1.GameState;
import game2_1.events.InputEvent;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import utility.*;

import java.io.Serializable;
import java.util.*;

/**
 * The player. The users directly control instances of this class.
 */
public class PlayerLogic implements Serializable {
    public final transient GameState parent;
    public final transient byte id;

    public static final float r = 20;
    public Vector2 pos, vel;
    public Vector2 cursor;

    public byte weaponType;

    public final transient HashMap<Character, Movement> keyBinds;
    private final EnumSet<Movement> movement;

    //region Constructors
    public PlayerLogic(byte id, GameState parent) {//todo: remove this?
        this(
                id,
                parent,
                new Vector2(40),
                new Vector2(0, 0),
                new Vector2(),
                (byte) -1
        );
    }
    public PlayerLogic(byte id, GameState parent, Vector2 pos, Vector2 vel, Vector2 cursor, byte weaponType) {
        this.id = id;
        this.parent = parent;
        this.pos = pos;
        this.vel = vel;
        this.cursor = cursor;
        this.weaponType = weaponType;

        //todo: read from file?
        keyBinds = new HashMap<>(Map.of(
                'w', Movement.Up,
                'a', Movement.Left,
                's', Movement.Down,
                'd', Movement.Right
        ));
        movement = EnumSet.noneOf(Movement.class);
    }
    //endregion

    public void update(float deltaTime, Bounds2 bounds) {
        Vector2 movementForce = new Vector2();
        for (Movement movement : movement)
            movementForce.add(movement.force);

        movementForce.normalize();

        if (movementForce.dot(Vector2.normalize(vel)) < -0.5)
            movementForce.mult(3);
        movementForce.mult(2f);

        if (movementForce.magnitudeSqr() == 0)
            vel.mult(0.8f);
        vel.mult(0.94f);
        vel.add(movementForce);

        pos.add(vel);


        if (pos.x < bounds.x) vel.x *= -1;
        if (pos.x > bounds.x + bounds.w) vel.x *= -1;
        if (pos.y < bounds.y) vel.y *= -1;
        if (pos.y > bounds.y + bounds.h) vel.y *= -1;

        pos.x = MathF.bounce(pos.x, bounds.x, bounds.x + bounds.w);
        pos.y = MathF.bounce(pos.y, bounds.y, bounds.y + bounds.h);
    }

    public void checkIfHit(Collection<Projectile> projectiles) {
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

    public void handleEvent(InputEvent event) {
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
                case MouseButtonPressed -> shoot();
                case MouseButtonReleased -> {
                }
                case MouseWheel -> {
                }
            }
        }
    }

    private void shoot() {
        Projectile toAdd;

        if (parent.music.playing) {
            toAdd = new Projectile(
                    id,
                    parent.beats.getStrength(
                            weaponType,
                            parent.music.getMicrosecondPosition() / 1000
                    ),
                    pos.copy(),
                    Vector2.sub(cursor, pos).setMagnitude(15)
            );
        } else {
            toAdd = new Projectile(
                    id,
                    0,
                    pos.copy(),
                    Vector2.sub(cursor, pos).setMagnitude(15)
            );
        }
        //Debug.log("Added: " + toAdd);
        parent.projectiles.add(toAdd);
    }

    @Override
    public String toString() {
        return "Player{" +
                "pos=" + pos +
                ", vel=" + vel +
                '}';
    }

    private enum Movement {
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
