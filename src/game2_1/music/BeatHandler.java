package game2_1.music;

import utility.Debug;
import utility.Utility;
import utility.style.Foreground;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Use load() and save() when reading/writing to a file
 */
public class BeatHandler implements Serializable{
    //TODO replace with ArrayList or ArrayDequeue and use Collections.sort
    private final HashMap<Byte, LinkedList<Beat>> beats;
    private final transient HashMap<Byte, Integer> beatIndex;

    //Debug
    public float bpm;
    public int startOffset;

    //region Constructors
    public BeatHandler(HashMap<Byte, LinkedList<Beat>> beats, float bpm, int startOffset) {
        this.beats = beats;
        this.beatIndex = new HashMap<>();
        this.bpm = bpm;
        this.startOffset = startOffset;
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

    //final transient fields are stuck as null when deserialized.
    //Construct a new instance with non null final transient fields.
    @Serial
    public Object readResolve() {
        return new BeatHandler(this.beats, this.bpm, this.startOffset);
    }

    //TODO: Clean up this mess
    public static BeatHandler load(String filePath) {
        try {
            Scanner in = new Scanner(new File(filePath));

            try {
                int state = 0;

                HashMap<Byte, LinkedList<Beat>> beats = new HashMap<>();
                float bpm = -1;
                int startOffset = -1;

                float strength;
                float val = -1;
                long timeStamp = 0;
                LinkedList<Beat> queue = new LinkedList<>();
                for (String data = in.next(); in.hasNext(); data = in.next()) {
                    if (data.equals("{"))
                        continue;

                    switch (state) {
                        case 0 -> {
                            if (val == -1) {
                                val = Float.parseFloat(data);
                            } else {
                                if (data.equals("=")) {
                                    queue = new LinkedList<>();
                                    state = 1;
                                } else {
                                    bpm = val;
                                    startOffset = Integer.parseInt(data);
                                    state = 3;
                                }
                            }
                        }
                        case 1 -> {
                            if (data.equals("}")) {
                                beats.put((byte) val, queue);
                                state = 0;
                                val = -1;
                            } else {
                                timeStamp = Long.parseLong(data.replaceAll("\\D", ""));
                                state = 2;
                            }
                        }
                        case 2 -> {
                            strength = Float.parseFloat(data.replaceAll("[^\\d^.]", ""));
                            Beat beat = new Beat(timeStamp, strength);

                            queue.add(beat);
                            state = 1;
                        }
                    }
                }

                return new BeatHandler(beats, bpm, startOffset);
            } catch (Exception e) {
                Debug.logWarning("There was an error when reading the beatfile");
                return new BeatHandler();
            }
        } catch (FileNotFoundException e) {
            Debug.logWarning("No beat file found");
            Debug.logWarning("\t\"" + filePath + "\"");

            return new BeatHandler();
        }
    }

    public void save(String filePath) throws IOException {
        beats.forEach((weaponType, list) -> Collections.sort(list));

        File file = new File(filePath);
        file.createNewFile();
        var out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(filePath)));
        //out.print(beats.toString());

        out.println("{");
        beats.forEach((type, queue) -> {
            out.print("\t");
            out.println(type + " = {");

            for (Beat beat : queue) {
                out.print("\t\t");
                out.println(beat);
            }

            out.print("\t");
            out.println("}");
        });

        out.println("\t" + bpm);
        out.println("\t" + startOffset);
        out.println("}");

        out.flush();
        out.close();
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

    /**
     * @param weaponType
     * @param timeStamp
     * @return The strength in the range of 0 to 1
     */
    public float getStrength(byte weaponType, long timeStamp) {
        LinkedList<Beat> beats = this.beats.get(weaponType);

        if (beats.isEmpty())
            return 0;

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


    @Override
    public boolean equals(Object o) {//Autogenerated
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeatHandler that = (BeatHandler) o;

        if (Float.compare(that.bpm, bpm) != 0) return false;
        if (startOffset != that.startOffset) return false;
        if (!beats.equals(that.beats)) return false;
        return Objects.equals(beatIndex, that.beatIndex);
    }
    @Override
    public int hashCode() {//Autogenerated
        int result = beats.hashCode();
        result = 31 * result + (beatIndex != null ? beatIndex.hashCode() : 0);
        result = 31 * result + (bpm != +0.0f ? Float.floatToIntBits(bpm) : 0);
        result = 31 * result + startOffset;
        return result;
    }
}
