package utility.internet;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class InternetTest extends JFrame {
    AbstractClient<String> client;
    AbstractServer<String> server;

    InternetTest() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        JButton btnStartClient;
        JButton btnStartServer;

        JTextField txfAdress;
        JTextField txfPort;

        JTextField txfSend;
        JTextArea txaRecieve;

        JButton btnSendClient;
        JButton btnSendServer;

        add(btnStartClient = new JButton("StartClient"), c);
        c.gridx = 1;
        add(btnStartServer = new JButton("StartServer"), c);

        c.gridx = 0;
        c.gridy = 1;
        add(txfAdress = new JTextField(), c);
        c.gridx = 1;
        add(txfPort = new JTextField(), c);

        c.gridx = 0;
        c.gridy = 2;
        add(txfSend = new JTextField(), c);
        c.gridx = 1;
        add(txaRecieve = new JTextArea(), c);

        c.gridx = 0;
        c.gridy = 3;
        add(btnSendClient = new JButton("Send Client"), c);
        c.gridx = 1;
        add(btnSendServer = new JButton("Send Server"), c);


        btnStartClient.addActionListener(ignored -> {
            try {
                client = new AbstractClient<>("Test Client", InetAddress.getByName(txfAdress.getText()), Integer.parseInt(txfPort.getText())) {
                    @Override
                    public void run() {
                        super.run();
                    }
                };
                client.start();

                Thread.sleep(200);

                client.send("Client Connecting");
            } catch (SocketException | UnknownHostException | InterruptedException socketException) {
                socketException.printStackTrace();
            }
        });
        btnStartServer.addActionListener(ignored -> {
            try {
                server = new AbstractServer<>() {
                    @Override
                    public void run() {
                        super.run();
                    }
                };
                server.start();

                while (server.listeningPort == -1)
                    Thread.onSpinWait();

                txfAdress.setText(server.serverAddress.toString());
                txfPort.setText(server.listeningPort + "");
            } catch (UnknownHostException socketException) {
                socketException.printStackTrace();
            }
        });
        btnSendClient.addActionListener(ignored -> client.send(txfSend.getText()));
        btnSendServer.addActionListener(ignored -> {
            server.queue(txfSend.getText());
            server.sendQueued();
        });


        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        while (true) {
            if (client != null) {
                for (String s : client.getAndClearReceiveQueue()) {
                    txaRecieve.append("From Server: " + s + "\n");
                }
            }
            if (server != null) {
                for (String s : server.getAndClearReceiveQueue()) {
                    txaRecieve.append("From Client: " + s + "\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        new InternetTest();
    }
}
