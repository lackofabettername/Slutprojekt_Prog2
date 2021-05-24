package game2_1.music;

import utility.Debug;

import java.io.Serial;

public class DummyMusicPlayer extends MusicPlayer {
    private transient long frameStart, microStart;

    //region Constructors
    public DummyMusicPlayer(String filePath, float playbackRate) {
        super(filePath, playbackRate);
    }

    @Override
    protected void init() {
    }

    @Serial
    private Object readResolve() {
        return new MusicPlayer(this.audioFile, playbackRate);
    }
    //endregion

    @Override
    public void run() {
        Debug.logWarning("Tried to start dummy music player");
    }

    private long microToFrame(long micro) {
        return micro * 44100 / 1_000_000;
    }

    @Override
    public void start() {
        microStart = System.nanoTime() / 1000;
        frameStart = microToFrame(microStart);
        status = Status.Playing;
    }
    @Override
    public void start(long microSecondTimeStamp) {
        frameOffset = (long) (microSecondTimeStamp / 1E6 * 44100);
        microSecondOffset = microSecondTimeStamp;
        start();
    }
    @Override
    public void start(long microSecondTimeStamp, long startDelay) {
        start(microSecondTimeStamp - startDelay *1000);
    }

    @Override
    public long getFrame() {
        return frameOffset + microToFrame(System.nanoTime() / 1000 - microStart);
    }
    @Override
    public long getMicrosecondPosition() {
        return microSecondOffset + System.nanoTime() / 1000 - microStart;
    }

    @Override
    public String toString() {
        return "DummyMusicPlayer{" +
                "audioFile=" + audioFile +
                ", playbackRate=" + playbackRate +
                ", status=" + status +
                ", microSecondPos=" + getMicrosecondPosition() +
                '}';
    }
}
