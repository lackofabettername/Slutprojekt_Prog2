package game2_1.internet;

import game2_1.Game;
import utility.internet.AbstractServer;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Server extends AbstractServer<NetPacket> {
    Game parent;

    public Server(Game parent) throws SocketException {
        super("Server");
        this.parent = parent;
    }

    @Override
    protected void onClientJoin(Client<NetPacket> client) {
        parent.serverSide.onClientJoin(client.id());
    }
    @Override
    protected void onClientClose(Client<NetPacket> client) {
        super.onClientClose(client);
    }
}
