package game2_1;

import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;
import utility.Color;
import utility.MathF;
import utility.Utility;
import utility.Vector2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores the state of the game
 */
public class GameState implements Serializable {
    public transient MusicPlayer music;
    public transient BeatHandler beats;
    public String songPath;

    public float frameCount;
    public HashMap<Byte, Player> players;
    public ArrayList<Projectile> projectiles;
    public HashMap<Byte, Integer> scores;

    //region Constructors
    GameState(BeatHandler beats, String songPath, float frameCount, HashMap<Byte, Player> players, ArrayList<Projectile> projectiles, HashMap<Byte, Integer> scores) {
        this.beats = beats;
        this.songPath = songPath;
        this.frameCount = frameCount;
        this.players = players;
        this.projectiles = projectiles;
        this.scores = scores;
    }
    GameState(String songPath) {
        this(BeatHandler.load(songPath), songPath, -1, new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }
    GameState() {
        this(null, null, -1, new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }
    //endregion

    public byte[] getSerialized() throws IOException {
        return Utility.serialize(this);
    }

    /**
     * Linearly interpolates between two GameStates
     *
     * @param t        The "weight" between the two GameStates, 0 is previous, 1 is current.
     * @param previous the first GameState
     * @param current  the second GameState
     * @return A new GameState somewhere between previous and current based on t
     */
    public static GameState lerp(float t, GameState previous, GameState current) {
        GameState temp = new GameState();

        //region Music
        temp.music = previous.music;
        //endregion

        //region Beats
        temp.beats = previous.beats;
        //endregion

        //region Song path
        temp.songPath = previous.songPath;
        //endregion

        //region FrameCount
        temp.frameCount = MathF.lerp(t, previous.frameCount, current.frameCount);
        //endregion

        //region Players
        current.players.forEach((id, currentPlayer) -> {
            Player previousPlayer = previous.players.getOrDefault(id, currentPlayer);
            temp.players.put(id, new Player(
                    id,
                    temp,
                    Vector2.lerp(t, previousPlayer.pos, currentPlayer.pos).round(),
                    Vector2.lerp(t, previousPlayer.vel, currentPlayer.vel),
                    Color.lerp(t, previousPlayer.col, currentPlayer.col),
                    null,
                    Vector2.lerp(t, previousPlayer.cursor, currentPlayer.cursor)
            ));
            temp.players.get(id).createParticleSystem(previousPlayer.particles);
        });
        //endregion

        //fixme
        //region Projectiles
        for (int i = 0, j = 0; i < current.projectiles.size(); ++i) {
            Projectile currentProjectile = current.projectiles.get(i);
            Projectile previousProjectile;

            if (currentProjectile == null) {
                ++j;
                continue;
            }

            do {
                previousProjectile = j < previous.projectiles.size() ? previous.projectiles.get(j) : currentProjectile;
                ++j;
            } while (previousProjectile == null);

            temp.projectiles.add(new Projectile(
                    currentProjectile.owner,
                    currentProjectile.strength,
                    Vector2.lerp(t, previousProjectile.pos, currentProjectile.pos),
                    Vector2.lerp(t, previousProjectile.vel, currentProjectile.vel)
            ));
        }
        //endregion

        //region Scores
        current.scores.forEach((id, currentScore) -> {
            int previousScore = previous.scores.getOrDefault(id, currentScore);
            temp.scores.put(id,
                    MathF.lerp(t, previousScore, currentScore)
            );
        });
        //endregion

        return temp;
    }

    public void lerp(float t, GameState target) {
        //region FrameCount
        frameCount = MathF.lerp(t, frameCount, target.frameCount);
        //endregion

        //region Players
        target.players.forEach((id, targetPlayer) -> {
            if (players.containsKey(id)) {
                Player currentPlayer = players.get(id);
                currentPlayer.pos.lerp(t, targetPlayer.pos);
                currentPlayer.vel.lerp(t, targetPlayer.vel);
                currentPlayer.col.lerp(t, targetPlayer.col);
                currentPlayer.cursor.lerp(t, targetPlayer.cursor);
            } else {
                players.put(id, targetPlayer);
            }
            //Player currentPlayer = players.getOrDefault(id, targetPlayer);
            //players.put(id, new Player(
            //        id,
            //        this,
            //        Vector2.lerp(t, currentPlayer.pos, targetPlayer.pos).round(),
            //        Vector2.lerp(t, currentPlayer.vel, targetPlayer.vel),
            //        Color.lerp(t, currentPlayer.color, targetPlayer.color),
            //        null,
            //        Vector2.lerp(t, currentPlayer.cursor, targetPlayer.cursor)
            //));
            ////temp.players.get(id).createParticleSystem(currentPlayer.particles);
        });
        //endregion

        //fixme
        //region Projectiles
        for (int i = 0, j = 0; i < target.projectiles.size(); ++i) {
            Projectile targetProjectile = target.projectiles.get(i);
            Projectile currentProjectile = null;

            if (targetProjectile == null) {
                ++j;
                continue;
            }

            for (int attempts = 0; currentProjectile == null; ++attempts) {
                currentProjectile = j < projectiles.size() ?
                        projectiles.get(j) :
                        targetProjectile;
                if (attempts > 0) {
                    projectiles.remove(j--);
                }

                ++j;
            }
//            do {
//                projectiles.remove(0);
//                --j;
//
//                currentProjectile = j < projectiles.size() ?
//                        projectiles.get(j) :
//                        targetProjectile;
//                ++j;
//            } while (currentProjectile == null);

            currentProjectile.pos.lerp(t, targetProjectile.pos);
            currentProjectile.vel.lerp(t, targetProjectile.vel);
            projectiles.set(j, currentProjectile);
        }
//        for (int i = 0, j = 0; i < target.projectiles.size(); ++i) {
//            Projectile targetProjectile = target.projectiles.get(i);
//            Projectile currentProjectile;
//
//            if (targetProjectile == null) {
//                ++j;
//                continue;
//            }
//
//            do {
//                currentProjectile = j < projectiles.size() ? projectiles.get(j) : targetProjectile;
//                ++j;
//            } while (currentProjectile == null);
//
//            projectiles.add(new Projectile(
//                    targetProjectile.owner,
//                    targetProjectile.strength,
//                    Vector2.lerp(t, currentProjectile.pos, targetProjectile.pos),
//                    Vector2.lerp(t, currentProjectile.vel, targetProjectile.vel)
//            ));
//        }
        //endregion

        //region Scores
        target.scores.forEach((id, currentScore) -> {
            int previousScore = scores.getOrDefault(id, currentScore);
            scores.put(id,
                    MathF.lerp(t, previousScore, currentScore)
            );
        });
        //endregion
    }

    @Override
    public String toString() {
        return "GameState{" +
                "beats=" + beats +
                ", music=" + music +
                ", frameCount=" + frameCount +
                ", players=" + players +
                ", projectiles=" + projectiles +
                '}';
    }
}
