package game2_1.internet;

import game2_1.GameState;

import java.io.Serializable;

//TODO: Move the BeatHandler instance here and make it transient in GameState?
/**
 * Sent from the server to the client once the client connect. It holds the information that only needs to be sent once.
 * @see NetPacket
 * @see Client
 * @see Server
 */
public record ClientInfo(byte clientId, int serverUPS, GameState gameState) implements Serializable {
}
