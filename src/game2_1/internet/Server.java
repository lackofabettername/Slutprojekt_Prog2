package game2_1.internet;

import game2_1.serverSide.GameLogic;
import utility.internet.AbstractServer;

import java.net.SocketException;

/**
 * The implementation of {@link utility.internet.AbstractClient} the game uses, it sends only NetPackets.
 */
public class Server extends AbstractServer<NetPacket> {
    private final GameLogic parent;

    /**
     *
     * @param parent The GameLogic that uses this server.
     * @throws SocketException Thrown by the DatagramSocket.
     */
    public Server(GameLogic parent) throws SocketException {
        super("Server");
        this.parent = parent;
    }

    @Override
    protected void onClientJoin(Client<NetPacket> client) {
        parent.onClientJoin(client.id());
    }
    @Override
    protected void onClientClose(Client<NetPacket> client) {
        //TODO: tell the parent to disconnect the client.
        super.onClientClose(client);
    }
}
