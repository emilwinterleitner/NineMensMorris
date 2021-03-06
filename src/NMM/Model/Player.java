package NMM.Model;

import NMM.Enums.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private final PlayerColor color;
    private final String name;
    private int tilesOnBoardCount;
    private List<Tile> tilesOnBoard;

    public PlayerColor getPlayerColor() { return color; }

    public String getPlayerName() { return name; }

    public Player(PlayerColor color, String name) {
        this.color = color;
        this.name = name;
        tilesOnBoardCount = 0;
        tilesOnBoard = new ArrayList<>();
    }

    public void addTile(Tile t) {
        tilesOnBoardCount++;
        tilesOnBoard.add(t);
    }

    public void removeTile(Tile t) {
        tilesOnBoardCount--;
        tilesOnBoard.remove(t);
    }

    public int getTilesOnBoardCount() {
        return tilesOnBoardCount;
    }

    public List<Tile> getTilesOnBoard() {
        return tilesOnBoard;
    }
}
