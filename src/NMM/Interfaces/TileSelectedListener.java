package NMM.Interfaces;

import NMM.Enums.PlayerColor;
import NMM.Model.Tile;

public interface TileSelectedListener {
    void tileSelected(Tile t, PlayerColor color);
}
