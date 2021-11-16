package NMM.Interfaces;

import NMM.Enums.PlayerColor;
import NMM.Model.Tile;

import java.util.Map;

public interface GameBoardChangedListener {
    void gameBoardChanged(Map<Tile, PlayerColor> gameBoard);
}
