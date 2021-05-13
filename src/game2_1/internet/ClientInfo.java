package game2_1.internet;

import game2_1.GameState;

import java.io.Serializable;

public record ClientInfo(
        byte clientId,
        int serverUPS,
        GameState gameState
) implements Serializable {
}
