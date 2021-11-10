package NMM.GameManagerState;

import NMM.Enums.PlayerColor;
import NMM.Model.Board;

public class GameManagerMoveState implements GameManagerState {
    @Override
    public boolean tilePressed(Board board, int row, int col, PlayerColor color) {
        return board.tryToSelectTile(row, col, color);
    }
}
