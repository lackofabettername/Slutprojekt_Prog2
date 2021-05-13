package game2_1.internet;

import game2_1.Game;
import utility.internet.AbstractServer;

import java.net.UnknownHostException;

public class Server extends AbstractServer<NetPacket> {
    Game parent;

    public Server(Game parent) throws UnknownHostException {
        super();
        this.parent = parent;
    }

    @Override
    protected void onClientJoin(ClientHandler client) {
        sendTo(new NetPacket(
                        NetPacketType.Information,
                        (byte) 0,
                        new ClientInfo(
                                client.id,
                                parent.serverSide.ups,
                                parent.serverSide.gameState
                                )),
                client.id
        );
        parent.serverSide.onClientJoin(client.id);
    }
    @Override
    protected void onClientClose(ClientHandler client) {
        super.onClientClose(client);
    }
}
