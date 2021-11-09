package NMM.Model;

import NMM.Enums.PlayerColor;

public class Player {
    private final PlayerColor color;
    private final String name;

    public PlayerColor getPlayerColor() { return color; }

    public String getPlayerName() { return name; }

    public Player(PlayerColor color, String name) {
        this.color = color;
        this.name = name;
    }
}
