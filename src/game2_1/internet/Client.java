package game2_1.internet;

import game2_1.events.InputEvent;
import utility.Debug;
import utility.internet.AbstractClient;

import java.net.InetAddress;
import java.net.SocketException;

public class Client extends AbstractClient<NetPacket> {
    public volatile byte id;

    public Client(InetAddress address) throws SocketException {
        super("Client", address);
        id = -1;
    }

    public void send(InputEvent event) {
        if (id == -1) {
            Debug.logWarning("id has not been assigned");
            return;
        }
        //if (id == -1) throw new IllegalStateException("id has not been assigned");
        send(new NetPacket(NetPacketType.ClientInput, id, event));
    }
}
