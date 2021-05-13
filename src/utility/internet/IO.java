package utility.internet;

import utility.Debug;
import utility.Utility;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class IO<T extends Serializable> implements Runnable {
    protected final DatagramSocket socket;
    private final byte[] buff = new byte[65535];

    protected final Queue<T> receiveQueue;
    protected final Queue<byte[]> sendQueue;

    protected final Thread thread;

    ///////////////////

    private final DelayQueue<Delay> networkLag;

    public final double packetLoss = 0.;
    public final int networkLatency = 0;

    protected IO(String name, InetAddress address, int port) throws SocketException {
        this(name);

        socket.connect(address, port);
    }
    protected IO(String name) throws SocketException {
        thread = new Thread(this, name);

        socket = new DatagramSocket();
        socket.setSoTimeout(100);

        try {
            Debug.log("local address = " + InetAddress.getLocalHost());
        } catch (UnknownHostException ignored) {
        }
        Debug.log("socket local Port = " + socket.getLocalPort());

        receiveQueue = new ArrayDeque<>();
        sendQueue = new ArrayDeque<>();

        networkLag = new DelayQueue<>();
    }


    @Override
    public void run() {

        while (!thread.isInterrupted()) {
            try {
                receive();
            } catch (IOException | ClassNotFoundException e) {
                Debug.logError(e);
            }
        }

        socket.close();
        //Debug.log("IO closed");

        onClose();
    }

    public final void close() {
        if (!thread.isInterrupted())
            thread.interrupt();
    }


    protected void receive() throws IOException, ClassNotFoundException {
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        Delay temp = networkLag.poll();

        if (temp == null) {
            try {
                socket.receive(packet);

                if (Math.random() < packetLoss)
                    return;

                networkLag.add(new Delay(buff, packet.getLength(), packet.getSocketAddress(), (int) (Math.random() * networkLatency)));
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                Debug.logError(e);
                thread.interrupt();
            }
        } else {

            System.arraycopy(temp.data, 0, buff, 0, temp.data.length);

            T message = Utility.deserialize(buff);
            try {
                synchronized (receiveQueue) {
                    receiveQueue.add(message);
                }
            } catch (Exception e) {
                Debug.logAll("error", Thread.currentThread(), message, e.getMessage());
                throw e;
            }
            //Debug.log("IO, receive: " + message + " | " + localSequence + ", " + remoteSequence + ", " + acknowledgedSequence + ", " + Integer.toBinaryString(unacknowledgedBitField) + ", " + Integer.toBinaryString(ackBitField));

            afterReceive(temp.data, temp.address);
        }
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


    public final void queue(T object) {
        try {
            sendQueue.add(Utility.serialize(object));
        } catch (IOException e) {
            Debug.logError(e);
        }
    }
    public final void queue(T... objects) {
        for (T object : objects) {
            queue(object);
        }
    }
    public final void queue(byte[] data) {
        sendQueue.add(data);
    }
    public final void queue(byte[]... data) {
        sendQueue.addAll(Arrays.asList(data));
    }

    public final void send(T object) {
        try {
            send((Utility.serialize(object)));
        } catch (IOException e) {
            Debug.logError(e);
        }
    }
    public final void send(T... objects) {
        for (T object : objects) {
            send(object);
        }
    }
    public final void send(byte[]... messages) {
        for (byte[] message : messages) {
            send(message);
        }
    }

    public final boolean sendQueued() {
        if (!thread.isAlive() || !socket.isConnected()) {
            return false;
        }

        while (sendQueue.size() > 0) {
            byte[] message = sendQueue.poll();
            send(message);
        }

        return true;
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


    protected void onClose() {

    }


    public final void start() {
        thread.start();
    }
    public final Thread getThread() {
        return thread;
    }


    class Delay implements Delayed {
        byte[] data;
        SocketAddress address;
        long time;

        Delay(byte[] buff, int length, SocketAddress address, long time) {
            data = new byte[length];
            System.arraycopy(buff, 0, data, 0, length);
            this.address = address;

            this.time = System.currentTimeMillis() + time;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.currentTimeMillis(), unit);
        }
        @Override
        public int compareTo(Delayed o) {
            return (int) this.getDelay(TimeUnit.MILLISECONDS) - (int) o.getDelay(TimeUnit.MILLISECONDS);
        }
    }
}
