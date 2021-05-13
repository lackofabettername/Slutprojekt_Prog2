package utility;

import game2_1.GameState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static utility.Utility.primitiveToWrapperArray;

//todo: make the Resolution not final?
//      Perhaps add a byte to the header describing which resolution was used,
//      then dynamically changing the resolution for optimal compression.
public class DeltaCompression {
    /**
     * This number controls the "resolution" of the compression.
     * 1 means every byte is compared, 2 means bytes are compared in groups of two, etc.
     * Increasing this number makes the header smaller, but it may also require redundant bytes to be sent,
     * because if a single byte is different in a group, all of the bytes in that group need to be sent.
     * The minimum size of the message is the [length of base] / (8*resolution).
     */
    public static final int Resolution = 4;
    public static final int val = Byte.SIZE * Resolution;

    /**
     * Compresses data by only keeping the differences from the original.
     *
     * @param base    The base data, what was first.
     * @param message The altered data, this is compared to the base to create the output.
     * @return a compressed array of bytes, or null if the original message is shorter
     */
    public static byte[] compress(byte[] base, byte[] message) {
        int headerSize = (base.length - 1) / val + 1; //https://stackoverflow.com/a/503201
        ByteArrayOutputStream header = new ByteArrayOutputStream(headerSize);
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();

        byte headerTemp = 0;
        int i = -1;
        while (i++ < base.length - 1) {
            if ((i) % Resolution == 0) {
                headerTemp <<= 1;
            }

            if (i >= message.length) {
                headerTemp |= 1;//flag end of message
                break;
            }
            if (base[i] != message[i]) {
                headerTemp |= 1;//flag difference in message

                i = i / Resolution * Resolution;
                for (int stop = i + Resolution; i < stop; ++i) {
                    if (i >= message.length)
                        break;

                    compressed.write(message[i]);
                }
                --i;
            }

            if ((i + 1) % val == 0) {
                header.write(headerTemp);
                headerTemp = 0;//not needed, but helps for clarity
            }
        }

        while (header.size() < headerSize) {
            if (base.length < message.length)
                headerTemp <<= 1;   //lack: wtf! why is this needed? i've no idea

            headerTemp <<= (7 - i / Resolution % 8);
            //lack:    this ^ has to be a 7. Not sure why, i don't like it either.

            header.write(headerTemp);
            headerTemp = 0;
        }

        while (i < message.length)
            compressed.write(message[i++]);


        if (headerSize + compressed.size() > message.length)
            return null;

        try {
            header.write(compressed.toByteArray());
            return header.toByteArray();
        } catch (IOException e) {
            Debug.logError(e);
            return null;
        }
    }
    public static byte[] compress(GameState oldState, GameState newState) throws IOException {
        return compress(oldState.getSerialized(), newState.getSerialized());
    }

    /**
     * Uncompressed the data that was compressed by compress().
     * @param base The base data, the same data that was used to compress the message
     * @param compressed The delta that was produced by compress
     * @return The original data that was compressed in compress, the "message"
     */
    public static byte[] unCompress(byte[] base, byte[] compressed) {
        //https://stackoverflow.com/a/503201
        int headerSize = (base.length - 1) / val + 1;
        ByteArrayOutputStream oup = new ByteArrayOutputStream(headerSize * val);

        int j = headerSize;
        MainLoop:
        for (int i = 0; i < base.length; ) {
            if (((compressed[i / val] << (i / Resolution % 8)) & 128) == 0) {
                for (int stop = i + Resolution; i < stop && i < base.length; ++i) {
                    oup.write(base[i]);
                }
            } else {
                for (int stop = i + Resolution; i < stop; ++i) {
                    if (j >= compressed.length)
                        break MainLoop;

                    oup.write(compressed[j++]);
                }
            }
        }

        while (j < compressed.length)
            oup.write(compressed[j++]);

        return oup.toByteArray();
    }
    public static GameState unCompress(GameState oldState, byte[] compressed) throws IOException, ClassNotFoundException {
        return Utility.deserialize(
                unCompress(
                        oldState.getSerialized(), compressed
                )
        );
    }


    /**
     * Test method
     */
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Locale.setDefault(Locale.ENGLISH);

        ArrayList<Vector2> a = new ArrayList<>(List.of(new Vector2(0, 1), new Vector2(10, -1), new Vector2(1000, 2.45f)));
        ArrayList<Vector2> b = new ArrayList<>(List.of(new Vector2(2, 1), new Vector2(2.3451f, 1), new Vector2(-1000, 2.45f)));

        byte[] base = Utility.serialize(a);
        byte[] message = Utility.serialize(b);


        byte[] compressed = compress(base, message);
        byte[] unCompress = unCompress(base, compressed);


        Debug.logArrayCompare(primitiveToWrapperArray(base), primitiveToWrapperArray(message), primitiveToWrapperArray(compressed), primitiveToWrapperArray(unCompress));
        Debug.logAll(message.length, compressed.length, String.format("%2.1f", (double) compressed.length / message.length * 100) + "%");
    }
}
