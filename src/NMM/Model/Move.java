package NMM.Model;

import NMM.Enums.PlayerColor;

import java.util.ArrayList;

public class Move {
    private final PlayerColor player;
    private ArrayList<Tile> move;

    public Move (PlayerColor color) {
        player = color;
        move = new ArrayList<>();
    }

    public void addTile(Tile t) {
        move.add(t);
    }

    public ArrayList<Tile> getMove() {
        return move;
    }

    public PlayerColor getPlayer() {
        return player;
    }
}
