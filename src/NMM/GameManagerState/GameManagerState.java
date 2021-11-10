package NMM.GameManagerState;

import NMM.Enums.PlayerColor;
import NMM.Model.Board;

public interface GameManagerState {
    boolean tilePressed(Board board, int row, int col, PlayerColor color);
}
