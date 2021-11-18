package NMM.Model;

import NMM.Enums.PlayerColor;

import java.io.Serializable;

public class Player implements Serializable {
    private final PlayerColor color;
    private final String name;
    private int tilesOnBoard;

    public PlayerColor getPlayerColor() { return color; }

    public String getPlayerName() { return name; }

    public Player(PlayerColor color, String name) {
        this.color = color;
        this.name = name;
        tilesOnBoard = 0;
    }

    public void addTile() {
        tilesOnBoard++;
        System.out.println("Added tile to: " + getPlayerName());
    }

    public void removeTile() {
        tilesOnBoard--;
        System.out.println("Removed tile from: " + getPlayerName());
    }

    public int getTilesOnBoard() {
        return tilesOnBoard;
    }
}
