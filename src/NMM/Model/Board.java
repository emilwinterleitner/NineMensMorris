package NMM.Model;

import NMM.Enums.PlayerColor;
import NMM.Interfaces.TilePlacedListener;
import NMM.Interfaces.TileSelectedListener;

import java.util.*;

public class Board {
    private static Board instance;

    private Map<Tile, PlayerColor> gameBoard;

    private List<Tile> allTiles;
    private List<Tile> freeTiles;

    // All tiles of the game board,
    private Tile t00 = new Tile(0, 0);
    private Tile t03 = new Tile(0, 3);
    private Tile t06 = new Tile(0, 6);
    private Tile t11 = new Tile(1, 1);
    private Tile t13 = new Tile(1, 3);
    private Tile t15 = new Tile(1, 5);
    private Tile t22 = new Tile(2, 2);
    private Tile t23 = new Tile(2, 3);
    private Tile t24 = new Tile(2, 4);
    private Tile t30 = new Tile(3, 0);
    private Tile t31 = new Tile(3, 1);
    private Tile t32 = new Tile(3, 2);
    private Tile t34 = new Tile(3, 4);
    private Tile t35 = new Tile(3, 5);
    private Tile t36 = new Tile(3, 6);
    private Tile t42 = new Tile(4, 2);
    private Tile t43 = new Tile(4, 3);
    private Tile t44 = new Tile(4, 4);
    private Tile t51 = new Tile(5, 1);
    private Tile t53 = new Tile(5, 3);
    private Tile t55 = new Tile(5, 5);
    private Tile t60 = new Tile(6, 0);
    private Tile t63 = new Tile(6, 3);
    private Tile t66 = new Tile(6, 6);

    private List<TilePlacedListener> tilePlacedListeners = new ArrayList<>();
    private List<TileSelectedListener> tileSelectedListeners = new ArrayList<>();

    private Board() {
        allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(t00, t03, t06, t11, t13, t15, t22, t23, t24, t30, t31, t32,
            t34, t35, t36, t42, t43, t44, t51, t53, t55, t60, t63, t66));
        freeTiles = new ArrayList<>();
        freeTiles.addAll(Arrays.asList(t00, t03, t06, t11, t13, t15, t22, t23, t24, t30, t31, t32,
            t34, t35, t36, t42, t43, t44, t51, t53, t55, t60, t63, t66));

        gameBoard = new HashMap<>();
    }

    public static Board getInstance() {
        if (instance == null)
            instance = new Board();
        return instance;
    }

    public boolean tryToSetTile(int row, int col, PlayerColor playerColor) {
        boolean result = false;
        Tile tile = null;

        tile = getTile(row, col);
        result = gameBoard.get(tile) == null;

        if (result) {
            for (TilePlacedListener tpl : tilePlacedListeners) {
                tpl.tilePlaced(tile, playerColor);
            }

            freeTiles.remove(tile);

            gameBoard.put(tile, playerColor);
        }

        return result;
    }

    public void addTilePlacedListener(TilePlacedListener listener) { tilePlacedListeners.add(listener); }
    public void addTileSelectedListener(TileSelectedListener listener) { tileSelectedListeners.add(listener); }

    public boolean tryToSelectTile(int row, int col, PlayerColor playerColor) {
        boolean result = false;
        Tile tile = null;

        tile = getTile(row, col);

        PlayerColor occupiedBy = gameBoard.get(tile);
        if (occupiedBy == playerColor) {
            for (TileSelectedListener tsl : tileSelectedListeners) {
                tsl.tileSelected(tile, playerColor);
            }
            result = true;
        }

        return result;
    }

    private Tile getTile(int row, int col) {
        Tile requestedTile = null;

        for (Tile t : allTiles) {
            if (t.getX() == col && t.getY() == row) {
                requestedTile = t;
                break;
            }
        }

        return requestedTile;
    }
}
