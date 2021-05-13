package game2_1.music;

import utility.Debug;
import utility.Utility;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class BeatHandler implements Serializable {//TODO replace with ArrayList or ArrayDequeue and use Collections.sort
    private final HashMap<Byte, LinkedList<Beat>> beats;
    private final transient HashMap<Byte, Integer> beatIndex;

    //Debug
    public float bpm;
    public int startOffset;

    //region Constructors
    public BeatHandler(HashMap<Byte, LinkedList<Beat>> beats) {
        this.beats = beats;
        this.beatIndex = new HashMap<>();
    }
    public BeatHandler() {
        beats = new HashMap<>();

        var temp = new ArrayDeque<Beat>();
        for (int i = 0; i < 1000; ++i) {
            //temp.add(new Beat((long) (i * (1000 / (60f / 60))), 1));
        }

        beats.put((byte) 0, new LinkedList<>(temp));
        beats.put((byte) 1, new LinkedList<>(temp));
        beats.put((byte) 2, new LinkedList<>(temp));

        beatIndex = new HashMap<>();
    }
    public static BeatHandler load(String filePath) {
        try {
            FileInputStream in = new FileInputStream(filePath);
            return Utility.deserialize(in.readAllBytes());
        } catch (IOException | ClassCastException | ClassNotFoundException e) {
            //Debug.logError(e);
            Debug.logWarning("No beat file found");
            Debug.logWarning("\t\"" + filePath + "\"");

            return new BeatHandler();
        }
    }

    public void save(String filePath) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        beats.forEach((weaponType, list) -> Collections.sort(list));
        out.write(Utility.serialize(this));
    }

    @Serial
    public Object readResolve() {
        return new BeatHandler(this.beats);
    }
    //endregion

    public void forEach(BiConsumer<Byte, Queue<Beat>> consumer) {
        beats.forEach(consumer);
    }

    public void addBeat(byte type, long timeStamp, float strength) {
        LinkedList<Beat> beats = this.beats.get(type);

        if (beats.size() == 0) {
            beats.add(new Beat(timeStamp, strength));
            return;
        }

        beats.add(new Beat(timeStamp, strength));
//        Beat[] temp = beats.toArray(new Beat[0]);
//        int i = temp.length / 2;
//        for (int j = i / 2; j >= 1; j = (j - 1) / 2 + 1) {
//            Beat beat = temp[i];
//
//            if (beat.timeStamp == timeStamp)
//                return;
//
//            if (beat.timeStamp() < timeStamp)
//                i += j;
//            else
//                i -= j;
//            i = MathF.clamp(i, 0, temp.length);
//
//            if (j == 1)
//                break;
//        }
//
//        if (temp[i].timeStamp() < timeStamp)
//            beats.add(i + 1, new Beat(timeStamp, strength));
//        else
//            beats.add(i, new Beat(timeStamp, strength));
//        //beats.get(type).add(new Beat(timeStamp, strength));
    }

    public void removeBeat(byte type, long timeStamp) {
        beats.get(type).removeIf(beat -> beat.timeStamp == timeStamp);
    }

    public int amountOfTypes() {
        return beats.size();
    }

    public float getStrength(byte weaponType, long timeStamp) {
        LinkedList<Beat> beats = this.beats.get(weaponType);

        Beat closestBeat = beats.get(
                beatIndex.compute(weaponType, (ignored, index) -> {
                    if (index == null)
                        index = 0;

                    Beat left = beats.get(index);
                    Beat rght = left;

                    if (left.timeStamp() >= timeStamp)
                        return index;

                    --index;
                    do {
                        ++index;

                        left = rght;

                        if (beats.size() <= index + 1)
                            return index;

                        rght = beats.get(index + 1);
                    } while (rght.timeStamp() < timeStamp);

                    if (timeStamp - left.timeStamp() < rght.timeStamp() - timeStamp)
                        return index;

                    return index + 1;
                })
        );

        float delta = Math.abs(closestBeat.timeStamp() - timeStamp) / 1000f;//TODO: Better score formula
        float strength = 1 - delta / (delta + 0.2f);

        //Debug.logAll(timeStamp, closestBeat.timeStamp());
        //Debug.logAll(delta, strength);

        //noinspection ConstantConditions
        if (false)
            strength *= closestBeat.strength();

        return strength;
    }



    public record Beat(long timeStamp, float strength) implements Serializable, Comparable<Beat> {
        @Override
        public int compareTo(Beat o) {
            return (int) (timeStamp() - o.timeStamp());
        }
    }
}
