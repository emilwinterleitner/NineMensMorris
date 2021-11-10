package NMM.Interfaces;

import NMM.Model.Tile;

import java.util.List;

public interface AllowedMovesChangedListener {
    void allowedTilesChanged(List<Tile> tiles, boolean allowed);
}
