package utility.internet.temp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class send {
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket();

        String str = "Welcome java";

        Scanner in = new Scanner(System.in);
        InetAddress ip = InetAddress.getByName(in.nextLine());
        DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 3000);
        ds.send(dp);

        ds.close();
    }
}
