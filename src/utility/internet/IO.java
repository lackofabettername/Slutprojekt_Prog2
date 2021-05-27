package utility.internet;

import utility.Debug;
import utility.Utility;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Queue;

public abstract class IO<T extends Serializable> implements Runnable {
    public static final int ServerPort = 60000;
    public static final int ClientPort = 60001;

    public final InetAddress LocalAddress;

    protected final DatagramSocket socket;
    private final byte[] buff = new byte[65535];

    protected final Queue<T> receiveQueue;

    protected final Thread thread;

    ///////////////////

    protected IO(String name, int localPort, InetAddress remoteAddress, int remotePort) throws SocketException {
        this(name, localPort);

        socket.connect(remoteAddress, remotePort);
    }
    protected IO(String name, int localPort) throws SocketException {
        thread = new Thread(this, name);

        socket = new DatagramSocket(localPort);

        InetAddress temp;
        try {
            Debug.log("local Port    = " + localPort);
            Debug.log("local address = " + (temp = InetAddress.getLocalHost()));

        } catch (UnknownHostException ignored) {
            temp = null;
        }
        LocalAddress = temp;

        receiveQueue = new ArrayDeque<>();
    }

    @Override
    public void run() {
        try {
            while (!thread.isInterrupted()) {
                receive();
            }
        } catch (IOException e) {
            Debug.logError(e);
        }

        socket.close();
        Debug.log("IO [" + thread.getName() + "] closed");

        onClose();
    }

    public void close() {
        if (!thread.isInterrupted())
            thread.interrupt();
    }

    protected void receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            return;
        }

        try {
            T message = Utility.deserialize(buff);
            synchronized (receiveQueue) {
                receiveQueue.add(message);
            }
        } catch (Exception e) {
            Debug.logError(e);
        }

        afterReceive(packet.getData(), packet.getSocketAddress());
    }

    protected void afterReceive(byte[] data, SocketAddress address) {

    }
    public Queue<T> getReceiveQueue() {
        synchronized (receiveQueue) {
            return receiveQueue;
        }
    }
    public Queue<T> getAndClearReceiveQueue() {
        synchronized (receiveQueue) {
            Queue<T> temp = new ArrayDeque<>(receiveQueue);
            receiveQueue.clear();
            return temp;
        }
    }

    public void send(T object) {
        try {
            send((Utility.serialize(object)));
        } catch (IOException e) {
            Debug.logError(e);
        }
    }
    public void send(T... objects) {
        for (T object : objects) {
            send(object);
        }
    }
    private void send(byte[] message) {
        DatagramPacket packet = new DatagramPacket(message, message.length);

        try {
            socket.send(packet);
        } catch (IOException e) {
            Debug.logError(e);
            thread.interrupt();
        }
    }

    public void send(SocketAddress address, T object) {
        try {
            send(address, (Utility.serialize(object)));
        } catch (IOException e) {
            Debug.logError(e);
        }
    }
    public void send(SocketAddress address, T... objects) {
        for (T object : objects) {
            send(address, object);
        }
    }
    private void send(SocketAddress address, byte[] message) {
        DatagramPacket packet = new DatagramPacket(message, message.length);
        packet.setSocketAddress(address);

        try {
            socket.send(packet);
        } catch (IOException e) {
            Debug.logError(e);
            thread.interrupt();
        }
    }

    protected void onClose() {

    }

    public final void start() {
        thread.start();
    }
    public final Thread getThread() {
        return thread;
    }
}
