package NMM.Model;

import NMM.Enums.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;

public class Move implements Serializable {
    private final PlayerColor player;
    private ArrayList<Tile> move;

    private boolean isTileRemoved;

    public Move (PlayerColor color) {
        player = color;
        move = new ArrayList<>();
        isTileRemoved = false;
    }

    public void addTile(Tile t) {
        move.add(t);
    }

    public ArrayList<Tile> getMove() {
        return move;
    }

    public PlayerColor getPlayerColor() {
        return player;
    }

    public void setIsTileRemoved() {
        isTileRemoved = true;
    }

    public boolean getIsTileRemoved() {
        return isTileRemoved;
    }
}
