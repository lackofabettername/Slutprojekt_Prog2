package utility.internet;

import utility.Debug;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public abstract class AbstractClient<T extends Serializable> extends IO<T> {

    public AbstractClient(String name, int localPort, InetAddress remoteAddress, int remotePort) throws SocketException {
        super(name, localPort, remoteAddress, remotePort);
    }
    public AbstractClient(String name, int localPort, InetAddress remoteAddress) throws SocketException {
        this(name, localPort, remoteAddress, ServerPort);
    }
    public AbstractClient(String name, InetAddress remoteAddress) throws SocketException {
        this(name, ClientPort, remoteAddress, ServerPort);
    }

    public static void main(String[] args) throws SocketException {
        Scanner in = new Scanner(System.in);

        InetAddress address = null;
        while (address == null) {
            System.out.print("address > ");

            try {
                address = InetAddress.getByName(in.nextLine());
            } catch (UnknownHostException e) {
                Debug.logError(e);
            }
        }

        AbstractClient<String> client = new AbstractClient<>("AbstractClient", address) {
        };

        client.thread.start();

        //in = new Scanner(System.in);
        //while (true) {
        //    System.out.print("Input > ");
        //    client.send(in.nextLine());
        //}

        JFrame frame = new JFrame();
        frame.setLayout(new GridLayout(2, 2));

        JTextField input = new JTextField();
        JButton send = new JButton("Send");
        JTextArea output = new JTextArea();

        frame.add(input);
        frame.add(send);
        frame.add(new JScrollPane(output));

        send.addActionListener(e -> client.send(input.getText().trim()));

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        new Thread(() -> {
            while (client.thread.isAlive()) {
                if (!client.receiveQueue.isEmpty())
                    output.append(client.receiveQueue.poll().trim() + "\n");
            }
        }, "AbstractClient listener").start();


        int val = in.nextInt();
        Debug.log("Sending " + val + " packets");
        for (int i = 0; i < val; ++i) {
            client.send("debug " + i);

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Debug.logError(e);
            }
        }
    }
}
