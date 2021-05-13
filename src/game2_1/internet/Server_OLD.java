//package game2_1.internet;
//
//import game2_1.Game;
//import utility.Debug;
//import utility.Utility;
//import utility.internet_OLD.AbstractServer;
//
//import java.io.IOException;
//import java.net.UnknownHostException;
//
//public class Server_OLD extends AbstractServer<NetPacket> {
//    Game parent;
//
//    public Server_OLD(Game parent) throws UnknownHostException {
//        super();
//        this.parent = parent;
//    }
//
//    @Override
//    protected byte[] encode(NetPacket packet, ClientHandler client) {
//        try {
//            return Utility.serialize(packet);
//        } catch (IOException e) {
//            Debug.logError(e);
//            throw new NullPointerException();
//        }
//    }
//    @Override
//    protected NetPacket decode(byte[] data, int offset, int length, ClientHandler client) {
//        try {
//            return Utility.deserialize(data, offset);
//        } catch (IOException | ClassNotFoundException e) {
//            Debug.logError(e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onClientJoin(ClientHandler client) {
//        parent.serverSide.onClientJoin();
//        super.onClientJoin(client);
//    }
//    @Override
//    protected void onClientClose(ClientHandler client) {
//        super.onClientClose(client);
//    }
//}
