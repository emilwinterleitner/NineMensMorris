package NMM.Model;

import NMM.Enums.PlayerColor;
import NMM.Interfaces.*;

import java.util.*;

public class Board {
    private static Board instance;

    private MerelManager merelManager;

    private Map<Tile, PlayerColor> gameBoard;

    private List<Tile> allTiles;
    private List<Tile> freeTiles;
    private List<Tile> allowedMoves;

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

    private Tile selectedTile;
    private Tile lastModifiedTile;

    private List<TilePlacedListener> tilePlacedListeners = new ArrayList<>();
    private List<TileSelectedListener> tileSelectedListeners = new ArrayList<>();
    private List<SelectedTileChangeListener> selectedTileChangeListeners = new ArrayList<>();
    private List<TileRemovedListener> tileRemovedListeners = new ArrayList<>();
    private List<AllowedMovesChangedListener> allowedMovesChangedListeners = new ArrayList<>();

    private Board() {
        allTiles = new ArrayList<>();
        allTiles.addAll(Arrays.asList(t00, t03, t06, t11, t13, t15, t22, t23, t24, t30, t31, t32,
            t34, t35, t36, t42, t43, t44, t51, t53, t55, t60, t63, t66));
        freeTiles = new ArrayList<>();
        freeTiles.addAll(Arrays.asList(t00, t03, t06, t11, t13, t15, t22, t23, t24, t30, t31, t32,
            t34, t35, t36, t42, t43, t44, t51, t53, t55, t60, t63, t66));

        gameBoard = new HashMap<>();
        allowedMoves = new ArrayList<>();
        merelManager = new MerelManager();
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

            lastModifiedTile = tile;
        }

        return result;
    }

    public void addTilePlacedListener(TilePlacedListener listener) { tilePlacedListeners.add(listener); }
    public void addTileSelectedListener(TileSelectedListener listener) { tileSelectedListeners.add(listener); }
    public void addSelectedTileChangeListener(SelectedTileChangeListener listener) { selectedTileChangeListeners.add(listener); }
    public void addTileRemovedListener(TileRemovedListener listener) { tileRemovedListeners.add(listener); }
    public void addAllowedMovesChangedListener(AllowedMovesChangedListener listener) { allowedMovesChangedListeners.add(listener); }

    public boolean tryToSelectTile(int row, int col, PlayerColor playerColor) {
        boolean result = false;

        Tile tile = getTile(row, col);

        PlayerColor occupiedBy = gameBoard.get(tile);

        if (occupiedBy == playerColor) {
            changeSelectedTile(tile, playerColor);
        } else if (occupiedBy == null && selectedTile != null) {
            if (allowedMoves.contains(tile)) {
                showAllowedMoves(false);
                tryToSetTile(row, col, playerColor);
                removeSelectedTile();
                result = true;
            }
        }

        return result;
    }

    public boolean tryToRemoveTile(int row, int col, PlayerColor color) {
        boolean result = false;

        Tile tile = getTile(row, col);

        PlayerColor occupiedBy = gameBoard.get(tile);

        if (occupiedBy != color && occupiedBy != null) {
            freeTiles.add(tile);
            gameBoard.remove(tile);
            for (TileRemovedListener trl : tileRemovedListeners) {
                trl.tileRemoved(tile);
            }
            result = true;
        }

        return result;
    }

    private void removeSelectedTile() {
        merelManager.removeMerel(selectedTile);
        freeTiles.add(selectedTile);
        gameBoard.remove(selectedTile);
        for (TileRemovedListener trl : tileRemovedListeners) {
            trl.tileRemoved(selectedTile);
        }
        selectedTile = null;
    }

    private void changeSelectedTile(Tile tile, PlayerColor playerColor) {
        if (!allowedMoves.isEmpty())
            showAllowedMoves(false);
        if (selectedTile != null) {
            for (SelectedTileChangeListener stcl : selectedTileChangeListeners) {
                stcl.selectedTileChanged(selectedTile, gameBoard.get(selectedTile));
            }
        }

        for (TileSelectedListener tsl : tileSelectedListeners) {
            tsl.tileSelected(tile, playerColor);
        }

        selectedTile = tile;
        getAllowedMoves();
        filterAllowedMoves();
        showAllowedMoves(true);
    }

    private void showAllowedMoves(boolean show) {
        for (AllowedMovesChangedListener amcl : allowedMovesChangedListeners) {
            amcl.allowedTilesChanged(allowedMoves, show);
        }
        if (!show)
            allowedMoves.clear();
    }

    private void filterAllowedMoves() {
        List<Tile> invalidMoves = new ArrayList<>();

        for (Tile t : allowedMoves) {
            if (gameBoard.get(t) != null) {
                invalidMoves.add(t);
            }
        }

        allowedMoves.removeAll(invalidMoves);
    }

    public void getAllowedMoves() {
        if (selectedTile.equals(t00)) {
            allowedMoves.add(t30);
            allowedMoves.add(t03);
        } else if (selectedTile.equals(t03)) {
            allowedMoves.add(t00);
            allowedMoves.add(t06);
            allowedMoves.add(t13);
        } else if (selectedTile.equals(t06)) {
            allowedMoves.add(t03);
            allowedMoves.add(t36);
        } else if (selectedTile.equals(t11)) {
            allowedMoves.add(t31);
            allowedMoves.add(t13);
        } else if (selectedTile.equals(t13)) {
            allowedMoves.add(t11);
            allowedMoves.add(t15);
            allowedMoves.add(t03);
            allowedMoves.add(t23);
        } else if (selectedTile.equals(t15)) {
            allowedMoves.add(t13);
            allowedMoves.add(t35);
        } else if (selectedTile.equals(t22)) {
            allowedMoves.add(t32);
            allowedMoves.add(t23);
        } else if (selectedTile.equals(t23)) {
            allowedMoves.add(t22);
            allowedMoves.add(t24);
            allowedMoves.add(t13);
        } else if (selectedTile.equals(t24)) {
            allowedMoves.add(t23);
            allowedMoves.add(t34);
        } else if (selectedTile.equals(t30)) {
            allowedMoves.add(t00);
            allowedMoves.add(t60);
            allowedMoves.add(t31);
        } else if (selectedTile.equals(t31)) {
            allowedMoves.add(t51);
            allowedMoves.add(t11);
            allowedMoves.add(t30);
            allowedMoves.add(t32);
        } else if (selectedTile.equals(t32)) {
            allowedMoves.add(t22);
            allowedMoves.add(t42);
            allowedMoves.add(t31);
        } else if (selectedTile.equals(t34)) {
            allowedMoves.add(t24);
            allowedMoves.add(t44);
            allowedMoves.add(t35);
        } else if (selectedTile.equals(t35)) {
            allowedMoves.add(t15);
            allowedMoves.add(t55);
            allowedMoves.add(t34);
            allowedMoves.add(t36);
        } else if (selectedTile.equals(t36)) {
            allowedMoves.add(t06);
            allowedMoves.add(t66);
            allowedMoves.add(t35);
        } else if (selectedTile.equals(t42)) {
            allowedMoves.add(t32);
            allowedMoves.add(t43);
        } else if (selectedTile.equals(t43)) {
            allowedMoves.add(t42);
            allowedMoves.add(t44);
            allowedMoves.add(t53);
        } else if (selectedTile.equals(t44)) {
            allowedMoves.add(t34);
            allowedMoves.add(t43);
        } else if (selectedTile.equals(t51)) {
            allowedMoves.add(t31);
            allowedMoves.add(t53);
        } else if (selectedTile.equals(t53)) {
            allowedMoves.add(t51);
            allowedMoves.add(t55);
            allowedMoves.add(t43);
            allowedMoves.add(t63);
        } else if (selectedTile.equals(t55)) {
            allowedMoves.add(t53);
            allowedMoves.add(t35);
        } else if (selectedTile.equals(t60)) {
            allowedMoves.add(t30);
            allowedMoves.add(t63);
        } else if (selectedTile.equals(t63)) {
            allowedMoves.add(t60);
            allowedMoves.add(t66);
            allowedMoves.add(t53);
        } else if (selectedTile.equals(t66)) {
            allowedMoves.add(t36);
            allowedMoves.add(t63);
        }
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

    public boolean checkForMerels() {
        boolean result = false;

        if (lastModifiedTile != null) {
            if (lastModifiedTile.equals(t00)) {
                result = checkLocation(t00, t30, t60, t00, t03, t06);
            } else if (lastModifiedTile.equals(t03)) {
                result = checkLocation(t00, t03, t06, t03, t13, t23);
            } else if (lastModifiedTile.equals(t06)) {
                result = checkLocation(t00, t03, t06, t06, t36, t66);
            } else if (lastModifiedTile.equals(t11)) {
                result = checkLocation(t11, t13, t15, t11, t31, t51);
            } else if (lastModifiedTile.equals(t13)) {
                result = checkLocation(t11, t13, t15, t03, t13, t23);
            } else if (lastModifiedTile.equals(t15)) {
                result = checkLocation(t11, t13, t15, t15, t35, t55);
            } else if (lastModifiedTile.equals(t22)) {
                result = checkLocation(t22, t23, t24, t22, t32, t42);
            } else if (lastModifiedTile.equals(t23)) {
                result = checkLocation(t22, t23, t24, t03, t13, t23);
            } else if (lastModifiedTile.equals(t24)) {
                result = checkLocation(t22, t23, t24, t24, t34, t44);
            } else if (lastModifiedTile.equals(t30)) {
                result = checkLocation(t00, t30, t60, t30, t31, t32);
            } else if (lastModifiedTile.equals(t31)) {
                result = checkLocation(t30, t31, t32, t11, t31, t51);
            } else if (lastModifiedTile.equals(t32)) {
                result = checkLocation(t30, t31, t32, t22, t32, t42);
            } else if (lastModifiedTile.equals(t34)) {
                result = checkLocation(t34, t35, t36, t24, t34, t44);
            } else if (lastModifiedTile.equals(t35)) {
                result = checkLocation(t34, t35, t36, t15, t35, t55);
            } else if (lastModifiedTile.equals(t36)) {
                result = checkLocation(t34, t35, t36, t06, t36, t66);
            } else if (lastModifiedTile.equals(t42)) {
                result = checkLocation(t42, t43, t44, t22, t32, t42);
            } else if (lastModifiedTile.equals(t43)) {
                result = checkLocation(t42, t43, t44, t43, t53, t63);
            } else if (lastModifiedTile.equals(t44)) {
                result = checkLocation(t42, t43, t44, t24, t34, t44);
            } else if (lastModifiedTile.equals(t51)) {
                result = checkLocation(t51, t53, t55, t11, t31, t51);
            } else if (lastModifiedTile.equals(t53)) {
                result = checkLocation(t51, t53, t55, t43, t53, t63);
            } else if (lastModifiedTile.equals(t55)) {
                result = checkLocation(t51, t53, t55, t15, t35, t55);
            } else if (lastModifiedTile.equals(t60)) {
                result = checkLocation(t00, t30, t60, t60, t63, t66);
            } else if (lastModifiedTile.equals(t63)) {
                result = checkLocation(t60, t63, t66, t43, t53, t63);
            } else if (lastModifiedTile.equals(t66)) {
                result = checkLocation(t60, t63, t66, t06, t36, t66);
            }
        }

        lastModifiedTile = null;

        return result;
    }

    private boolean checkLocation(Tile t1, Tile t2, Tile t3, Tile t4, Tile t5, Tile t6) {
        boolean merelFound = false;
        if (gameBoard.get(t1) != null && gameBoard.get(t2) != null && gameBoard.get(t3) != null) {
            if ((gameBoard.get(t1) == gameBoard.get(t2) && gameBoard.get(t2) == gameBoard.get(t3))) {
                merelManager.addMerel(new Merel(t1, t2, t3));
                merelFound = true;
            }
        }
        if (gameBoard.get(t4) != null && gameBoard.get(t5) != null && gameBoard.get(t6) != null) {
            if ((gameBoard.get(t4) == gameBoard.get(t5) && gameBoard.get(t5) == gameBoard.get(t6))) {
                merelManager.addMerel(new Merel(t4, t5, t6));
                merelFound = true;
            }
        }
        return merelFound;
    }

}
