package NMM.GameManagerState;

import NMM.Enums.PlayerColor;
import NMM.Model.Board;

public class GameManagerPlaceState implements GameManagerState {
    @Override
    public boolean tilePressed(Board board, int row, int col, PlayerColor color) {
        return board.tryToSetTile(row, col, color);
    }
}
