package InternetTest.v1;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client implements Runnable {
    DatagramSocket socket;
    Thread thread;

    Client(int localPort, InetAddress remoteAddress, int remotePort) throws SocketException {
        socket = new DatagramSocket(localPort);
        socket.connect(remoteAddress, remotePort);

        thread = new Thread(this);
    }

    void start() {
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (!thread.isInterrupted()) {
                receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void send(String text) throws IOException {
        byte[] buff = text.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.send(packet);

        System.out.println("Sent: " + text);
    }

    void receive() throws IOException {
        byte[] buff = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        String message = new String(packet.getData(), 0, packet.getLength());
        SocketAddress sender = packet.getSocketAddress();

        System.out.println("Received: " + message + " from " + sender);
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        int localPort;
        while (true) {
            try {
                System.out.println("Enter localPort");
                localPort = Integer.parseInt(in.nextLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        InetAddress remoteAddress;
        while (true) {
            try {
                System.out.println("Enter Address");
                remoteAddress = InetAddress.getByName(in.nextLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int remotePort;
        while (true) {
            try {
                System.out.println("Enter remotePort");
                remotePort = Integer.parseInt(in.nextLine());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Client client = new Client(localPort, remoteAddress, remotePort);
        client.start();

        while (true) {
            System.out.println("Enter message to send");
            client.send(in.next());
        }
    }
}
