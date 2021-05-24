package game2_1.music;

import utility.Debug;

import javax.sound.sampled.*;
import java.io.*;

@SuppressWarnings("SameParameterValue")
public class MusicPlayer implements Runnable, Serializable {

    private transient Thread thread;
    private transient long delayedStart;

    // size of the byte buffer used to read/write the audio stream
    private static final int BUFFER_SIZE = 2048;
    private transient int FRAME_LENGTH;

    public final File audioFile;
    private transient BufferedInputStream audioInputStream;
    private transient AudioFormat format;
    private transient final byte[] bytesBuffer = new byte[BUFFER_SIZE];

    private transient SourceDataLine audioOutputLine;

    public float playbackRate;
    private transient float t;
    private transient int lastFrame, frame;

    protected transient volatile Status status;
    private transient long framePos, microSecondPos;

    protected transient long frameOffset, microSecondOffset;


    //region Constructors
    /**
     * @param audioFilePath Path of the audio file.
     * @param playbackRate  Speed of playback. 1 for normal speed, 2 for double etc
     */
    public MusicPlayer(String audioFilePath, float playbackRate) {
        this(new File(audioFilePath), playbackRate);
    }

    /**
     * @param audioFile    Audio file.
     * @param playbackRate Speed of playback. 1 for normal speed, 2 for double etc
     */
    public MusicPlayer(File audioFile, float playbackRate) {
        this.audioFile = audioFile;
        this.playbackRate = playbackRate;

        if (playbackRate <= 0) throw new IllegalStateException("playbackRate must be greater than 0");

        try {
            init();
        } catch (UnsupportedAudioFileException e) {
            Debug.logWarning("The specified audio file is not supported.");
            Debug.logError(e);
        } catch (LineUnavailableException e) {
            Debug.logWarning("Audio line for playback is unavailable.");
            Debug.logError(e);
        } catch (IOException e) {
            Debug.logWarning("Error playing the audio file.");
            Debug.logError(e);
        }
    }

    protected void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        audioInputStream = new BufferedInputStream(audioStream);

        format = audioStream.getFormat();
        FRAME_LENGTH = format.getFrameSize();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        audioOutputLine = (SourceDataLine) AudioSystem.getLine(info);
        audioOutputLine.open(format);

        //Debug.log(format);

        thread = new Thread(this, "MusicPlayer " + audioFile);

        status = Status.Ready;
    }

    @Serial
    private Object readResolve() {
        return new MusicPlayer(this.audioFile, this.playbackRate);
    }
    //endregion


    @Override
    public void run() {
        try {
            audioOutputLine.start();

            while (status != Status.Stopping && System.currentTimeMillis() < delayedStart) {
                status = Status.Waiting;

                Thread.onSpinWait();
                try {
                    //noinspection BusyWait
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }

            if (Waiting() || Ready()) {
                //Debug.log("Playback started.");
                status = Status.Playing;

                int bytesRead;
                while (playing() && (bytesRead = readFrame()) != -1) {
                    audioOutputLine.write(bytesBuffer, 0, bytesRead);

                    while (audioOutputLine.getBufferSize() - audioOutputLine.available() > BUFFER_SIZE * 2)
                        Thread.onSpinWait();

                    while (audioOutputLine.available() < BUFFER_SIZE)
                        Thread.onSpinWait();
                }

                audioOutputLine.drain();

                audioOutputLine.close();
                audioInputStream.close();

                //Debug.log("Playback completed.");
            }
        } catch (IOException e) {
            Debug.logWarning("Error playing the audio file.");
            Debug.logError(e);
        }

        status = Status.Stopped;
    }

    public void start() {
        thread.start();
    }
    public void start(long microSecondTimeStamp) {
        try {
            frameOffset = (long) (microSecondTimeStamp / 1E6 * format.getFrameRate());
            microSecondOffset = microSecondTimeStamp;

            audioInputStream.skip(frameOffset * FRAME_LENGTH);
        } catch (IOException e) {
            Debug.logError(e);
        }
        start();
    }
    public void start(long microSecondTimeStamp, long startDelay) {
        this.delayedStart = startDelay;
        start(microSecondTimeStamp);
    }

    public void stop() {
        status = Status.Stopping;
        thread.interrupt();
    }

    public Thread getThread() {
        return thread;
    }

    public long getFrame() {
        return frameOffset + (playing() ?
                (framePos = audioOutputLine.getLongFramePosition()) :
                framePos
        );
    }

    public long getMicrosecondPosition() {
        return microSecondOffset + (playing() ?
                (microSecondPos = audioOutputLine.getMicrosecondPosition()) :
                microSecondPos
        );
    }

    private int readFrame() throws IOException {
        int readSamples = 0;

        if (FRAME_LENGTH != 4) throw new UnsupportedOperationException("todo");

        //readSamples = audioInputStream.read(bytesBuffer);
        for (; readSamples < BUFFER_SIZE; readSamples += 4) {
            for (t += playbackRate; t >= 1; --t) {
                lastFrame = frame;
                frame = readFrame(audioInputStream.readNBytes(4));

                short left = (short) (frame & 65535);
                short right = (short) (frame >>> 16);

                short mono = (short) (((left + right) / 2) & 65535);

                frame = mono + (mono << 16);
            }

            if (t == 0) {
                writeFrame(readSamples, frame);
            } else {
                short lastLeft = (short) (lastFrame & 65535);
                short left = (short) (frame & 65535);

                short lastRigh = (short) (lastFrame >>> 16);
                short righ = (short) (frame >>> 16);

                left = (short) (lastLeft + t * (left - lastLeft));
                righ = (short) (lastRigh + t * (righ - lastRigh));

                writeFrame(readSamples, left + (righ << 16));
            }
        }

        return readSamples;
    }

    private void writeFrame(int index, int sample) {
        //noinspection PointlessArithmeticExpression
        bytesBuffer[index + 0] = (byte) sample;
        bytesBuffer[index + 1] = (byte) (sample >>> 8);
        bytesBuffer[index + 2] = (byte) (sample >>> 16);
        bytesBuffer[index + 3] = (byte) (sample >>> 24);
    }

    private int readFrame(byte[] temp) {
        int sampleA = (temp[0] & 255) + ((temp[1] & 255) << 8);
        int sampleB = (temp[2] & 255) + ((temp[3] & 255) << 8);
        return (sampleA + (sampleB << 16));
    }

    public long getStartDelay() {
        return delayedStart;
    }

    public Status getStatus() {
        return status;
    }

    public boolean Ready() {
        return status == Status.Ready;
    }
    public boolean Waiting() {
        return status == Status.Waiting;
    }
    public boolean playing() {
        return status == Status.Playing;
    }
    public boolean Stopping() {
        return status == Status.Stopping;
    }
    public boolean Stopped() {
        return status == Status.Stopped;
    }

    public enum Status {
        /**
         * The Musicplayer has loaded all resources and is ready to play
         */
        Ready,

        /**
         * The Musicplayer has been started with a delayed start and is waiting
         */
        Waiting,

        /**
         * The Musicplayer is playing
         */
        Playing,

        /**
         * The Musicplayer is stopping and can not start again
         */
        Stopping,

        /**
         * The Musicplayer has stopped and can not start again
         */
        Stopped
    }

    @Override
    public String toString() {
        return "MusicPlayer{" +
                "audioFile=" + audioFile +
                ", format=" + format +
                ", playbackRate=" + playbackRate +
                ", status=" + status +
                ", microSecondPos=" + getMicrosecondPosition() +
                '}';
    }
}
