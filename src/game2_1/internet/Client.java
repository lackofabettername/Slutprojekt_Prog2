package game2_1.internet;

import game2_1.events.InputEvent;
import utility.Debug;
import utility.internet.AbstractClient;

import java.net.InetAddress;
import java.net.SocketException;

public class Client extends AbstractClient<NetPacket> {
    public volatile byte id;

    public Client(String name, InetAddress address, int port) throws SocketException {
        super(name, address, port);
        id = -1;
    }

    public void queue(InputEvent event) {
        if (id == -1) {
            Debug.logWarning("id has not been assigned");
            return;
        }
        //if (id == -1) throw new IllegalStateException("id has not been assigned");
        queue(new NetPacket(NetPacketType.ClientInput, id, event));
    }
}
