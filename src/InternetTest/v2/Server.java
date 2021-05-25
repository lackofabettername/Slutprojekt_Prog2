package InternetTest.v2;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;

public class Server implements Runnable {
    public static final int ServerPort = 60000;

    DatagramSocket socket;
    HashSet<SocketAddress> clients;

    Thread thread;

    Server() throws SocketException, UnknownHostException {
        this(ServerPort);

        System.out.println("Server port: " + ServerPort);
    }

    Server(int localPort) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(localPort);
        clients = new HashSet<>();
        thread = new Thread(this);

        System.out.println("Server Address: " + InetAddress.getLocalHost().toString());
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

        for (SocketAddress client : clients) {
            packet.setSocketAddress(client);
            socket.send(packet);
            System.out.println("Sent: " + text + " to " + client);
        }
    }

    void receive() throws IOException {
        byte[] buff = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        String message = new String(packet.getData(), 0, packet.getLength());
        SocketAddress sender = packet.getSocketAddress();

        System.out.println("Received: " + message + " from " + sender);

        if (!clients.contains(sender)) {
            clients.add(sender);
            System.out.println("New client connected");
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        Server server = new Server();
        server.start();

        while (true) {
            System.out.println("Enter message to send");
            server.send(in.next());
        }
    }
}
