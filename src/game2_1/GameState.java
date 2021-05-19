package game2_1;

import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;
import game2_1.serverSide.PlayerLogic;
import game2_1.serverSide.Projectile;
import utility.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Stores the state of the game
 */
public class GameState implements Serializable {
    public transient MusicPlayer music;
    public transient BeatHandler beats;
    public String songPath;

    public float frameCount;
    public HashMap<Byte, PlayerLogic> players;

    public static final int ProjectilesBufferSize = 6;
    public RollingBuffer<Projectile> projectiles;

    public HashMap<Byte, Integer> scores;

    //region Constructors
    public GameState(BeatHandler beats, String songPath, float frameCount, HashMap<Byte, PlayerLogic> players, RollingBuffer<Projectile> projectiles, HashMap<Byte, Integer> scores) {
        this.beats = beats;
        this.songPath = songPath;
        this.frameCount = frameCount;
        this.players = players;
        this.projectiles = projectiles;
        this.scores = scores;
    }
    public GameState(String songPath) {
        this(BeatHandler.load(songPath), songPath, 0, new HashMap<>(), new RollingBuffer<>(ProjectilesBufferSize), new HashMap<>());
    }
    public GameState() {
        this(null, null, -1, new HashMap<>(), new RollingBuffer<>(ProjectilesBufferSize), new HashMap<>());
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
        current.players.forEach((id, currentPlayerLogic) -> {
            PlayerLogic previousPlayerLogic = previous.players.getOrDefault(id, currentPlayerLogic);
            temp.players.put(id, new PlayerLogic(
                    id,
                    temp,
                    Vector2.lerp(t, previousPlayerLogic.pos, currentPlayerLogic.pos).round(),
                    Vector2.lerp(t, previousPlayerLogic.vel, currentPlayerLogic.vel),
                    Vector2.lerp(t, previousPlayerLogic.cursor, currentPlayerLogic.cursor)
            ));
        });
        //endregion

        //region Projectiles
        //temp.projectiles.setPointer(current.projectiles.getPointer());
        temp.projectiles.setPointer(0);
        //previous.projectiles.setPointer(current.projectiles.getPointer());
        previous.projectiles.setPointer(0);
        current.projectiles.setPointer(0);
        Utility.forEach(current.projectiles, previous.projectiles, (currentProjectile, previousProjectile) -> {
            if (currentProjectile == null) {
                return;
            }

            if (previousProjectile == null)
                previousProjectile = currentProjectile;
            else if (!currentProjectile.id.equals(previousProjectile.id)) {
                Debug.logWarning("bad" + System.currentTimeMillis() / 1E3);
            }

            temp.projectiles.add(new Projectile(
                    currentProjectile.owner,
                    currentProjectile.strength,
                    Vector2.lerp(t, previousProjectile.pos, currentProjectile.pos),
                    Vector2.lerp(t, previousProjectile.vel, currentProjectile.vel),
                    previousProjectile.id
            ));
        });
//        for (int i = 0; i < ProjectilesBufferSize; ++i) {
//            Projectile currentProjectile = current.projectiles[i];
//            Projectile previousProjectile;
//
//            if (currentProjectile == null) {
//                continue;
//            }
//
//            previousProjectile = Utility.orDefault(previous.projectiles[i], currentProjectile);
//
//            temp.projectiles[i] = new Projectile(
//                    currentProjectile.owner,
//                    currentProjectile.strength,
//                    Vector2.lerp(t, previousProjectile.pos, currentProjectile.pos),
//                    Vector2.lerp(t, previousProjectile.vel, currentProjectile.vel)
//            );
//        }
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
                PlayerLogic currentPlayer = players.get(id);

                currentPlayer.pos.lerp(t, targetPlayer.pos);
                currentPlayer.vel.lerp(t, targetPlayer.vel);
                currentPlayer.cursor.lerp(t, targetPlayer.cursor);
            } else {
                players.put(id, targetPlayer);
                Debug.logWarning("Added new PlayerLogic");
            }
        });
        //endregion

        //fixme
        //region Projectiles
//        //for (int i = 0; i < ProjectilesBufferSize; ++i) {
//        //    Projectile targetProjectile = target.projectiles[i];
//        //    Projectile currentProjectile = null;
////
//        //    if (targetProjectile == null) {
//        //        continue;
//        //    }
////
//        //    currentProjectile = projectiles[i];
////
//        //    if (currentProjectile == null) {
//        //        projectiles[i] = targetProjectile;
//        //    } else {
//        //        currentProjectile.pos.lerp(t, targetProjectile.pos);
//        //        currentProjectile.vel.lerp(t, targetProjectile.vel);
//        //    }
//        //}
//        Debug.logWarning("TODO");
////        for (int i = 0, j = 0; i < target.projectiles.size(); ++i) {
////            Projectile targetProjectile = target.projectiles.get(i);
////            Projectile currentProjectile;
////
////            if (targetProjectile == null) {
////                ++j;
////                continue;
////            }
////
////            do {
////                currentProjectile = j < projectiles.size() ? projectiles.get(j) : targetProjectile;
////                ++j;
////            } while (currentProjectile == null);
////
////            projectiles.add(new Projectile(
////                    targetProjectile.owner,
////                    targetProjectile.strength,
////                    Vector2.lerp(t, currentProjectile.pos, targetProjectile.pos),
////                    Vector2.lerp(t, currentProjectile.vel, targetProjectile.vel)
////            ));
////        }
        //endregion

        //region Scores
        target.scores.forEach((id, targetScore) -> {
            int currentScore = scores.getOrDefault(id, targetScore);
            scores.put(id,
                    MathF.lerp(t, currentScore, targetScore)
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
