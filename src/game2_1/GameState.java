package game2_1;

import game2_1.clientSide.Player;
import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;
import game2_1.serverSide.PlayerLogic;
import game2_1.serverSide.Projectile;
import utility.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores the state of the game
 */
public class GameState implements Serializable {
    //The value of these are lost in the lerp
    public boolean gameRunning;
    public byte readyPlayers;

    public transient MusicPlayer music;
    public BeatHandler beats;//Todo: only send when it changes, to save bandwidth
    public String songPath;

    public float updateCount;
    public HashMap<Byte, PlayerLogic> players;

    public ArrayList<Projectile> projectiles;

    public HashMap<Byte, Integer> scores;

    //region Constructors
    public GameState(BeatHandler beats, String songPath, float updateCount, HashMap<Byte, PlayerLogic> players, ArrayList<Projectile> projectiles, HashMap<Byte, Integer> scores) {
        this.beats = beats;
        this.songPath = songPath;
        this.updateCount = updateCount;
        this.players = players;
        this.projectiles = projectiles;
        this.scores = scores;
    }
    public GameState(String songPath) {
        this(BeatHandler.load(songPath + "beats.txt"), songPath, 0, new HashMap<>(), new ArrayList<>(), new HashMap<>());
    }
    public GameState() {
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

        //region Beats
        temp.beats = previous.beats;
        //endregion

        //region Song path
        temp.songPath = previous.songPath;
        //endregion

        //region FrameCount
        temp.updateCount = MathF.lerp(t, previous.updateCount, current.updateCount);
        //endregion

        //region Players
        current.players.forEach((id, currentPlayerLogic) -> {
            PlayerLogic previousPlayerLogic = previous.players.getOrDefault(id, currentPlayerLogic);
            temp.players.put(id, new PlayerLogic(
                    id,
                    temp,
                    Vector2.lerp(t, previousPlayerLogic.pos, currentPlayerLogic.pos).round(),
                    Vector2.lerp(t, previousPlayerLogic.vel, currentPlayerLogic.vel),
                    Vector2.lerp(t, previousPlayerLogic.cursor, currentPlayerLogic.cursor),
                    previousPlayerLogic.weaponType
            ));
        });
        //endregion

        //region Projectiles
        for (int i = 0, j = 0; i < current.projectiles.size(); ++i) {
            Projectile currentProjectile = current.projectiles.get(i);
            Projectile previousProjectile;

            if (currentProjectile == null) {
                continue;
            }

            do {
                previousProjectile = j < previous.projectiles.size() ? previous.projectiles.get(j++) : currentProjectile;
            } while (previousProjectile == null || previousProjectile.id < currentProjectile.id);

            temp.projectiles.add(new Projectile(
                    currentProjectile.owner,
                    currentProjectile.strength,
                    Vector2.lerp(t, previousProjectile.pos, currentProjectile.pos),
                    Vector2.lerp(t, previousProjectile.vel, currentProjectile.vel),
                    currentProjectile.id
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
        //region Beats
        beats = target.beats;
        //endregion

        //region Song path
        if (!songPath.equals(target.songPath)) {
            if (music != null && music.getStatus() != MusicPlayer.Status.Ready) {
                if (!music.Waiting()) {
                    throw new IllegalStateException("This should not happen");
                    //TODO: disconnect client instead of crashing
                }

                long startDelay = music.getStartDelay();
                music.stop();
                try {
                    music.getThread().join();
                } catch (InterruptedException e) {
                    Debug.logError(e);
                    throw new IllegalStateException("This should not happen");
                    //TODO: disconnect client instead of crashing
                }

                music = new MusicPlayer(target.songPath + "song.wav", music.playbackRate);
                music.start(0, startDelay);
            }
        }
        songPath = target.songPath;
        //endregion

        //region FrameCount
        updateCount = MathF.lerp(t, updateCount, target.updateCount);
        //endregion

        //region Players
        target.players.forEach((id, targetPlayer) -> {
            if (players.containsKey(id)) {
                PlayerLogic currentPlayer = players.get(id);

                currentPlayer.pos.lerp(t, targetPlayer.pos);
                currentPlayer.vel.lerp(t, targetPlayer.vel);
                currentPlayer.cursor.lerp(t, targetPlayer.cursor);
            } else {
                players.put(id, new Player(targetPlayer, id));
                Debug.logWarning("Added new PlayerLogic");
            }
        });
        //endregion

        //region Projectiles
        int i = 0, j = 0;
        for (; i < target.projectiles.size(); ++i) {

            Projectile targetProjectile = target.projectiles.get(i);
            Projectile currentProjectile = null;

            if (targetProjectile == null) {
                continue;
            }

            if (j < projectiles.size()) {
                while (j < projectiles.size()) {
                    currentProjectile = projectiles.get(j++);

                    if (currentProjectile == null || currentProjectile.id < targetProjectile.id) {
                        projectiles.remove(--j);
                    } else
                        break;
                }


                //noinspection ConstantConditions
                if (targetProjectile.id < currentProjectile.id) {
                    projectiles.add(Debug.log(j - 1), targetProjectile);
                } else if (j <= projectiles.size()) {
                    if (j - 1 < 0) continue;
                    projectiles.set(
                            j - 1,
                            new Projectile(
                                    targetProjectile.owner,
                                    targetProjectile.strength,
                                    Vector2.lerp(t, currentProjectile.pos, targetProjectile.pos),
                                    Vector2.lerp(t, currentProjectile.vel, targetProjectile.vel),
                                    targetProjectile.id
                            )
                    );
                }
            } else {
                projectiles.add(targetProjectile);
                ++j;
            }
        }
        for (; j < projectiles.size(); ++j) {
            Projectile currentProjectile = projectiles.get(j);

            if (i == 0 || currentProjectile.id > target.projectiles.get(i - 1).id) {
                projectiles.remove(j--);
            }
        }
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
                ", updateCount=" + updateCount +
                ", players=" + players +
                ", projectiles=" + projectiles +
                '}';
    }
}
