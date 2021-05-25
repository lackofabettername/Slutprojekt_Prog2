package utility.internet;

import utility.Debug;

import java.io.Serializable;
import java.net.*;
import java.util.*;

public abstract class AbstractServer<T extends Serializable> extends IO<T> {
    protected final HashMap<Byte, Client<T>> clients;
    protected final HashMap<SocketAddress, Byte> addressIDMap;

    public AbstractServer(String name) throws SocketException {
        super(name, ServerPort);

        clients = new HashMap<>();
        addressIDMap = new HashMap<>();
    }

    @Override
    protected void afterReceive(byte[] data, SocketAddress address) {
        boolean clientIsNew;
        synchronized (clients) {
            clientIsNew = true;
            for (Map.Entry<Byte, Client<T>> entry : clients.entrySet()) {
                Client<T> client = entry.getValue();
                if (client.address.equals(address)) {
                    clientIsNew = false;
                    break;
                }
            }
        }

        byte id;
        if (clientIsNew) {
            id = (byte) clients.size();
            Client<T> newClient = new Client<>(id, "", address);
            synchronized (clients) {
                clients.put(id, newClient);
                addressIDMap.put(address, id);
            }

            onClientJoin(newClient);
        } else {
            id = addressIDMap.get(address);
        }

        synchronized (receiveQueue) {
            synchronized (clients) {
                clients.get(id).receiveQueue.add(receiveQueue.remove());
            }
            if (receiveQueue.size() > 0)
                Debug.logWarning("This should not happen. [AbstractServer]");
        }
    }

    public final void send(T message) {
        synchronized (clients) {
            clients.forEach((id, clientHandler) -> send(clientHandler.address, message));
        }
    }
    public final void sendTo(T message, int id) {
        send(clients.get((byte) id).address, message);
    }

    public Queue<T> getReceiveQueue() {
        Queue<T> out = new ArrayDeque<>();
        synchronized (clients) {
            clients.forEach((id, clientHandler) -> out.addAll(clientHandler.getReceiveQueue()));
        }
        return out;
    }
    public Queue<T> getAndClearReceiveQueue() {
        Queue<T> out = new ArrayDeque<>();
        synchronized (clients) {
            clients.forEach((id, clientHandler) -> out.addAll(clientHandler.getAndClearReceiveQueue()));
        }
        return out;
    }

    public Queue<T> getReceiveQueue(int id) {
        synchronized (clients) {
            return clients.get((byte) id).getReceiveQueue();
        }
    }
    public Queue<T> getAndClearReceiveQueue(int id) {
        synchronized (clients) {
            return clients.get((byte) id).getAndClearReceiveQueue();
        }
    }

    protected void onClientJoin(Client<T> client) {

    }
    protected void onClientClose(Client<T> client) {
        synchronized (clients) {
            clients.remove(client.id);
        }
    }

    public final int getClientCount() {
        synchronized (clients) {
            return clients.size();
        }
    }

    protected record Client<T>(
            byte id,
            String name,
            SocketAddress address,
            Queue<T> receiveQueue
    ) {

        protected Client(byte id, String name, SocketAddress address) {
            this(id, name, address, new ArrayDeque<>());
        }

        Queue<T> getReceiveQueue() {
            synchronized (receiveQueue) {
                return receiveQueue;
            }
        }
        Queue<T> getAndClearReceiveQueue() {
            synchronized (receiveQueue) {
                Queue<T> temp = new ArrayDeque<>(receiveQueue);
                receiveQueue.clear();
                return temp;
            }
        }

        //@Override
        //public boolean equals(Object o) {
        //    if (this == o) return true;
        //    if (o == null) return false;
//
        //    if (Byte.valueOf(id).equals(o)) return true;
        //    if (address.equals(o)) return true;
        //    if (o.getClass() != this.getClass()) return false;
        //
        //    Client client = (Client) o;
        //    return client.id == id &&
        //            client.name.equals(name) &&
        //            client.address.equals(address);
        //}
    }
}
