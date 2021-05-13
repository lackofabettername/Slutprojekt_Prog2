package utility.internet.temp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class receive {
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket(3000);
        byte[] buf = new byte[1024];

        System.out.println(InetAddress.getLocalHost().toString());

        DatagramPacket dp = new DatagramPacket(buf, 1024);
        ds.receive(dp);

        String str = new String(dp.getData(), 0, dp.getLength());

        System.out.println(str);

        ds.close();
    }
}
