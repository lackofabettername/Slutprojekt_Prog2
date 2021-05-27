package game2_1.internet;

import utility.Debug;

import java.io.Serializable;

/**
 * This is what the Server and Client sends.
 * @see Server
 * @see Client
 */
public record NetPacket(NetPacketType type, long timeStamp, byte sender, Serializable object) implements Serializable, Comparable<NetPacket> {
    /**
     * The server's standard ID.
     */
    public static final byte SERVER = 0;
    /**
     * Unknown sender.
     */
    public static final byte UNKNOWN = -1;

    public NetPacket(NetPacketType type, byte sender, Serializable object) {
        this(type, System.currentTimeMillis(), sender, object);
    }
    public NetPacket(NetPacketType type, int sender, Serializable object) {
        this(type, System.currentTimeMillis(), (byte) sender, object);
        if (sender != (byte) sender) Debug.logWarning("sender ID is not a byte");
    }

    /**
     * A blank packet with no data and no sender.
     */
    public static final NetPacket EMPTY_PACKET = new NetPacket(NetPacketType.EMPTY, (byte) -1, null);

    /**
     * Used for sorting NetPackets. They're sorted based on their timestamps.
     */
    @Override
    public int compareTo(NetPacket packet) {
        return (int) (timeStamp() - packet.timeStamp());
    }
}
