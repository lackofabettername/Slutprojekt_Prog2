//package game2_1.internet;
//
//import game2_1.Game;
//import utility.Debug;
//import utility.Utility;
//import utility.internet_OLD.AbstractClient;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.SocketException;
//
//public class Client_OLD extends AbstractClient<NetPacket> {
//    Game parent;
//
//    public Client_OLD(Game parent, String name, InetAddress address, int port) throws SocketException {
//        super(name, address, port);
//        this.parent = parent;
//    }
//
//    @Override
//    protected byte[] encode(NetPacket packet) {
//        try {
//            return Utility.serialize(packet);
//        } catch (IOException e) {
//            throw new NullPointerException();
//        }
//    }
//    @Override
//    protected NetPacket decode(byte[] data, int offset, int length) {
//        try {
//            return Utility.deserialize(data, offset);
//        } catch (IOException | ClassNotFoundException e) {
//            Debug.logError(e);
//            return null;
//        }
//    }
//}
