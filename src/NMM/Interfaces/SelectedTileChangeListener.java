package NMM.Interfaces;

import NMM.Enums.PlayerColor;
import NMM.Model.Tile;

public interface SelectedTileChangeListener {
    void selectedTileChanged(Tile t, PlayerColor color);
}
