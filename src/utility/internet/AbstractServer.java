package utility.internet;

import utility.Debug;
import utility.style.Font;
import utility.style.Foreground;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public abstract class AbstractServer<T extends Serializable> implements Runnable {
    Thread thread;
    public final InetAddress serverAddress;
    transient int listeningPort = -1;

    HashMap<Byte, ClientHandler> clients;
    private volatile boolean closing;

    PriorityQueue<T> receiveQueue;

    public AbstractServer() throws UnknownHostException {
        thread = new Thread(this);
        serverAddress = InetAddress.getLocalHost();

        clients = new HashMap<>();

        receiveQueue = new PriorityQueue<>();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                Debug.log("Opening new socket");

                ClientHandler newClient = new ClientHandler(++clientCount);
                newClient.start();
                clients.put(newClient.id, newClient);
                onClientJoin(newClient);

                Debug.log("Client connected");
            } catch (SocketException e) {
                Debug.logError(e);
                thread.interrupt();
            }
        }


        close();
    }

    public final void close() {
        if (closing)
            return;
        closing = true;

        if (!thread.isInterrupted())
            thread.interrupt();

        Debug.logDecorated("Closing clients...", Foreground.Magenta);
        Debug.offsetOutput(1);

        for (Iterator<Map.Entry<Byte, ClientHandler>> iterator = clients.entrySet().iterator(); iterator.hasNext(); ) {
            ClientHandler client = iterator.next().getValue();

            Debug.logDecorated("Closing ", false, Foreground.Magenta);
            Debug.logDecorated(client.name, false, Font.Bold, Foreground.Magenta);
            Debug.logDecorated("...", true, Foreground.Magenta);
            client.thread.interrupt();
            try {
                client.thread.join(250);
                Debug.logDecorated(client.name, false, Font.Bold, Foreground.Magenta);
                Debug.logDecorated(" Closed", true, Foreground.Magenta);
                iterator.remove();
            } catch (Exception e) {
                Debug.logError(e);
            }
        }

        Debug.offsetOutput(-1);
        Debug.logDecorated("Server closed", Foreground.Magenta);
    }


    public void queue(T message) {
        clients.forEach((id, clientHandler) -> clientHandler.queue(message));
    }
    public void queueTo(T message, int id) {
        if (id != (byte) id) throw new IllegalArgumentException("id Must be byte");
        clients.get((byte)id).queue(message);
    }
    public void sendQueued() {
        clients.forEach((id, clientHandler) -> clientHandler.sendQueued());
    }


    public void sendTo(T message, int id) {
        if (id != (byte) id) throw new IllegalArgumentException("id Must be a byte");
        clients.get((byte)id).send(message);
    }


    public Queue<T> getReceiveQueue() {
        clients.forEach((id, clientHandler) -> receiveQueue.addAll(clientHandler.getReceiveQueue()));
        return receiveQueue;
    }
    public Queue<T> getAndClearReceiveQueue() {
        clients.forEach((id, clientHandler) -> receiveQueue.addAll(clientHandler.getAndClearReceiveQueue()));

        Queue<T> temp = new ArrayDeque<>(receiveQueue);
        receiveQueue.clear();
        return temp;
    }


    protected void onClientJoin(ClientHandler client) {

    }
    protected void onClientClose(ClientHandler client) {
        if (closing)
            return;

        clients.remove(client);
        try {
            client.thread.join(250);
        } catch (InterruptedException e) {
            Debug.logError(e);
        }
    }


    public final void start() {
        thread.start();
    }
    public final Thread getThread() {
        return thread;
    }

    public final int getOpenPort() {
        return listeningPort;
    }
    public final int getClientCount() {
        return clients.size();
    }


    byte clientCount;

    protected final class ClientHandler extends IO<T> {
        public final byte id;
        private String name;

        protected ClientHandler(byte id) throws SocketException {
            super("AbstractClient " + clients.size());
            name = thread.getName();
            this.id = id;

            listeningPort = socket.getLocalPort();

            while (!socket.isConnected()) {
                try {
                    receive();
                } catch (IOException | ClassNotFoundException e) {
                    Debug.logError(e);
                }
            }
        }

        protected final void setName(String name) {
            this.name = name;
            thread.setName("game.Client: " + name);
        }
        protected final String getName() {
            return name;
        }

        @Override
        protected final void afterReceive(byte[] data, SocketAddress address) {
            if (!socket.isConnected()) {
                try {
                    socket.connect(address);
                } catch (SocketException e) {
                    Debug.logError(e);
                }
            }
        }


        @Override
        protected final void onClose() {
            AbstractServer.this.onClientClose(this);
        }
    }

//    public static void main(String[] args) throws SocketException, UnknownHostException {
//        AbstractServer<String> server = new AbstractServer<>() {
//        };
//
//        server.thread.Start();
//
//        new Thread(() -> {
//            while (true) {
//                for (AbstractServer<String>.ClientHandler client : server.clients) {
//                    while (client.receiveQueue.size() > 0) {
//                        String temp = "received: " + client.receiveQueue.poll();
//                        //Debug.log("server send: " + temp);
//                        client.queue(temp);
//                        client.sendQueued();
//                    }
//                }
//
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    Debug.logError(e);
//                }
//            }
//        }).Start();
//    }
}
