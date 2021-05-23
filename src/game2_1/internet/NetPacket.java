package game2_1.internet;

import utility.Debug;

import java.io.Serializable;

public record NetPacket(
        NetPacketType type,
        long timeStamp,
        byte sender,
        Serializable object
) implements Serializable, Comparable<NetPacket> {
    public NetPacket(NetPacketType type, byte sender, Serializable object) {
        this(type, System.currentTimeMillis(), sender, object);
    }
    public NetPacket(NetPacketType type, int sender, Serializable object) {
        this(type, System.currentTimeMillis(), (byte) sender, object);
        if (sender != (byte) sender) Debug.logWarning("sender ID is not a byte");
    }

    public static final NetPacket EmptyPacket = new NetPacket(NetPacketType.Empty, (byte) -1, null);

    @Override
    public int compareTo(NetPacket packet) {
        return (int) (timeStamp() - packet.timeStamp());
    }
}
