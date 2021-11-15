package NMM.GameManagerState;

import NMM.Enums.PlayerColor;
import NMM.Model.Board;

public class GameManagerRemoveState implements GameManagerState {

    @Override
    public boolean tilePressed(Board board, int row, int col, PlayerColor color) {
        return board.tryToRemoveTile(row, col, color);
    }
}
