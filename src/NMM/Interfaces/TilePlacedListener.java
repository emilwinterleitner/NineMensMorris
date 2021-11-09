package NMM.Interfaces;

import NMM.Enums.PlayerColor;
import NMM.Model.Tile;

public interface TilePlacedListener {
    void tilePlaced(Tile t, PlayerColor color);
}
