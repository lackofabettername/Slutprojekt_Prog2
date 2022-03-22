package game2_1.internet;

import game2_1.events.InputEvent;
import utility.Debug;
import utility.internet.AbstractClient;

import java.net.InetAddress;
import java.net.SocketException;

/**
 * The implementation of {@link AbstractClient} the game uses, it sends only NetPackets.
 */
public class Client extends AbstractClient<NetPacket> {
    /**
     * The client's id.
     */
    public volatile byte id;


    /**
     * Creates a new client
     * <ul>
     *     <li>remote port: {@link utility.internet.IO#ClientPort}.</li>
     * </ul>
     * @param localPort The client's local port
     * @param address The server's address.
     * @throws SocketException Thrown by the DataGramsocket.
     * @see java.net.DatagramSocket
     */
    public Client(int localPort, InetAddress address) throws SocketException {
        super("Client", localPort, address);
        id = -1;
    }

    /**
     * Creates a new client with IO's standard ports.
     * <ul>
     *     <li>local port:  {@link utility.internet.IO#ClientPort}.</li>
     *     <li>remote port: {@link utility.internet.IO#ClientPort}.</li>
     * </ul>
     * @param address The server's address.
     * @throws SocketException Thrown by the DataGramsocket.
     * @see java.net.DatagramSocket
     */
    public Client(InetAddress address) throws SocketException {
        super("Client", address);
        id = -1;
    }

    /**
     * This doesn't sent anything if {@link #id} isn't assigned (id = -1).
     * @param event
     */
    public void send(InputEvent event) {
        if (id == -1) {
            Debug.logWarning("id has not been assigned");
            return;
        }
        send(new NetPacket(NetPacketType.CLIENT_INPUT, id, event));
    }
}
