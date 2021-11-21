package NMM;

import NMM.Enums.GamePhase;
import NMM.Enums.PlayerColor;
import NMM.GameManagerState.*;
import NMM.Interfaces.CurrentPlayerListener;
import NMM.Interfaces.GamePhaseListener;
import NMM.Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private GameManagerState gameManagerState;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player opponentPlayer;
    private GamePhase phase;
    private History history;
    private Board board;
    private Move move;

    private List<CurrentPlayerListener> currentPlayerListeners = new ArrayList<CurrentPlayerListener>();
    private List<GamePhaseListener> gamePhaseListeners = new ArrayList<GamePhaseListener>();

    private int tilesPlaced;

    public GameManager() {
        board = Board.getInstance();
        board.reset();
    }

    //region Game Flow

    public void startGame() {
        player1 = new Player(PlayerColor.WHITE, "Player 1");
        player2 = new Player(PlayerColor.BLACK, "Player 2");

        tilesPlaced = 0;

        gameManagerState = new GameManagerPlaceState();

        history = new History();
        currentPlayer = player1;
        opponentPlayer = player2;
        phase = GamePhase.PLACE;
        notifyPlayerChanged();
        notifyGamePhaseChanged();
        move = new Move(currentPlayer.getPlayerColor());
    }

    public void tilePressed(int row, int col) {
        boolean validMove = gameManagerState.tilePressed(board, row, col, currentPlayer.getPlayerColor());

        if (validMove) {
            // Handle phase 1 (Place)
            if (gameManagerState instanceof GameManagerPlaceState) {
                if (++tilesPlaced > 17) {
                    gameManagerState = new GameManagerMoveState();
                    phase = GamePhase.MOVE;
                    notifyGamePhaseChanged();
                }
            } else if (gameManagerState instanceof GameManagerMoveState) {
                // Handle phase 2 (Move)
                // add selected (origin) tile to history
                move.addTile(board.getOriginTile());
                currentPlayer.removeTile(board.getOriginTile());
            }
            // add destination tile to history
            Tile t = board.getTile(row, col);
            move.addTile(t);

            // Handle removing of opponent tiles
            if (gameManagerState instanceof GameManagerRemoveState) {
                opponentPlayer.removeTile(board.getTile(row, col));
                move.setIsTileRemoved();

                // Check if the opponent player still has enough figures left
                if (!checkAndHandleGameWon()) {
                    // return to old state
                    if (tilesPlaced > 17) {
                        gameManagerState = new GameManagerMoveState();
                        phase = GamePhase.MOVE;
                    } else {
                        gameManagerState = new GameManagerPlaceState();
                        phase = GamePhase.PLACE;
                    }
                }
                notifyGamePhaseChanged();
            } else {
                currentPlayer.addTile(t);
                if (board.checkForMerel()) {
                    gameManagerState = new GameManagerRemoveState();
                    phase = GamePhase.REMOVE;
                    notifyGamePhaseChanged();
                }
            }

            if (!(gameManagerState instanceof GameManagerRemoveState))
                endTurn();
        }
    }

    // Game is won if a player has less than 3 figures left
    private boolean checkAndHandleGameWon() {
        boolean won = false;

        if (opponentPlayer.getTilesOnBoardCount() < 3 && tilesPlaced > 17) {
            gameManagerState = new GameManagerWonState();
            phase = GamePhase.WON;
            won = true;
        }

        return won;
    }

    private void endTurn() {
        history.addMove(move);
        if (!(gameManagerState instanceof GameManagerPlaceState || gameManagerState instanceof GameManagerWonState)) {
            if (!checkIfOpponentCanMove()) {
                gameManagerState = new GameManagerWonState();
                phase = GamePhase.WON;
                notifyGamePhaseChanged();
            }
        }
        if (!(gameManagerState instanceof GameManagerWonState))
            changePlayer();
    }

    private boolean checkIfOpponentCanMove() {
        List<Tile> tiles = opponentPlayer.getTilesOnBoard();
        System.out.println("Opponent has " + board.getAllowedMovesCount(tiles) + " possible moves");
        return board.getAllowedMovesCount(tiles) > 0;
    }

    private void changePlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
            opponentPlayer = player1;
        } else {
            currentPlayer = player1;
            opponentPlayer = player2;
        }

        for (CurrentPlayerListener cpl : currentPlayerListeners)
            cpl.playerChanged(currentPlayer);

        move = new Move(currentPlayer.getPlayerColor());

        //board.printMap();
    }

    //endregion

    //region Undo / Redo

    public void Undo() {
        Move moveToUndo = null;
        PlayerColor color;
        Player player;
        Player opponent;

        // Handle special case that the winning move is reverted
        if (phase == GamePhase.WON) {
            gameManagerState = new GameManagerMoveState();
            phase = GamePhase.MOVE;
            notifyGamePhaseChanged();
            changePlayer();
        }

        // If undo is pressed during an unfinished move
        if (phase == GamePhase.REMOVE) {
            moveToUndo = move;
            color = moveToUndo.getPlayerColor();
            player = color == player1.getPlayerColor() ? player1 : player2;
            opponent = color == player1.getPlayerColor() ? player2 : player1;
            changePlayer();
            if (tilesPlaced > 17) {
                gameManagerState = new GameManagerMoveState();
                phase = GamePhase.MOVE;
            } else {
                gameManagerState = new GameManagerPlaceState();
                phase = GamePhase.PLACE;
            }
            notifyGamePhaseChanged();
        } else {
            moveToUndo = history.undoMove();
            color = moveToUndo.getPlayerColor();
            player = color == player1.getPlayerColor() ? player1 : player2;
            opponent = color == player1.getPlayerColor() ? player2 : player1;
        }

        if (moveToUndo != null) {
            boolean isTileRemoved = moveToUndo.getIsTileRemoved();
            ArrayList<Tile> tiles = moveToUndo.getMove();
            int tileIdx = tiles.size() - 1;

            // Check if phase has to be updated (transition phase 1 to 2)
            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                tilesPlaced--;
                if (phase == GamePhase.MOVE) {
                    gameManagerState = new GameManagerPlaceState();
                    phase = GamePhase.PLACE;
                    notifyGamePhaseChanged();
                }
            }

            // If a tile has been removed, re-add it
            if (isTileRemoved) {
                Tile tileToSet = convertTile(tiles.get(tileIdx--));
                board.tryToSetTile(tileToSet.getY(), tileToSet.getX(),
                    color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE);
                board.removeMerelFromHistory(tiles.get(tileIdx));
                opponent.addTile(tileToSet);
            }

            // This tile for sure has to be removed no matter if phase 1 or 2
            Tile tileToRemove = convertTile(tiles.get(tileIdx--));
            board.removeMerelFromHistory(tileToRemove);
            board.removeTileFromHistory(tileToRemove);
            player.removeTile(tileToRemove);

            // If a tile is left then this move occurred in phase 2 -> origin tile has to be set
            if (tileIdx == 0) {
                Tile tileToSet = convertTile(tiles.get(0));
                board.tryToSetTile(tileToSet.getY(), tileToSet.getX(), color);
                player.addTile(tileToSet);
            }

            changePlayer();
        }
    }

    public void Redo() {
        Move moveToRedo = history.redoMove();

        if (moveToRedo != null) {
            PlayerColor color = moveToRedo.getPlayerColor();
            Player player = color == player1.getPlayerColor() ? player1 : player2;
            Player opponent = color == player1.getPlayerColor() ? player2 : player1;
            boolean isTileRemoved = moveToRedo.getIsTileRemoved();
            ArrayList<Tile> tiles = moveToRedo.getMove();
            int tileIdx = tiles.size() - 1;

            // Check if phase has to be updated (transition from phase 1 to 2)
            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                if (++tilesPlaced > 17) {
                    gameManagerState = new GameManagerMoveState();
                    phase = GamePhase.MOVE;
                    notifyGamePhaseChanged();
                }
            }

            // If a tile has been removed, remove it
            if (isTileRemoved) {
                Tile tileToRemove = convertTile(tiles.get(tileIdx--));
                board.removeTileFromHistory(tileToRemove);
                opponent.removeTile(tileToRemove);
            }

            // This tile for sure has to be set no matter which phase
            Tile tileToSet = convertTile(tiles.get(tileIdx--));
            board.tryToSetTile(tileToSet.getY(), tileToSet.getX(), color);
            player.addTile(tileToSet);

            // If a move occurred in phase 2 then remove the origin tile
            if (tileIdx == 0) {
                Tile tileToRemove = convertTile(tiles.get(0));
                board.removeMerelFromHistory(tileToRemove);
                board.removeTileFromHistory(tileToRemove);
                player.removeTile(tileToRemove);
            }

            // if a tile has been removed, then a merel now has to exist
            if (isTileRemoved) {
                board.addMerelFromHistory(tileToSet);
                if (checkAndHandleGameWon())
                    notifyGamePhaseChanged();
            }

            if (phase != GamePhase.WON)
                changePlayer();
        }
    }

    // used to get the exact tile objects from the new game board
    private Tile convertTile(Tile tile) {
        return board.getTile(tile.getY(), tile.getX());
    }

    //endregion

    //region Load / Save

    public void Load() {
        try {
            FileInputStream fileIn = new FileInputStream("/tmp/savegame.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            history = (History) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        board.setGameBoard(history.getGameBoard());
        board.setMerels(history.getMerelManager());
        phase = history.getCurrentGamePhase();
        player1 = history.getPlayer1();
        player2 = history.getPlayer2();
        currentPlayer = player1.getPlayerColor() == history.getCurrentPlayer() ? player1 : player2;
        opponentPlayer = player1.getPlayerColor() == history.getCurrentPlayer() ? player2 : player1;
        tilesPlaced = history.getTilesPlaced();

        if (phase == GamePhase.PLACE)
            gameManagerState = new GameManagerPlaceState();
        else if (phase == GamePhase.MOVE)
            gameManagerState = new GameManagerMoveState();
        else
            gameManagerState = new GameManagerWonState();

        move = new Move(currentPlayer.getPlayerColor());

        notifyGamePhaseChanged();
        notifyPlayerChanged();
    }

    public void Save() {
        if (phase != GamePhase.REMOVE) {
            history.setGameBoard(board.getGameBoard());
            history.setMerelManager(board.getMerelManager());
            history.setCurrentGamePhase(phase);
            history.setPlayer1(player1);
            history.setPlayer2(player2);
            history.setCurrentPlayer(currentPlayer.getPlayerColor());
            history.setTilesPlaced(tilesPlaced);

            try {
                FileOutputStream fileOut =
                    new FileOutputStream("/tmp/savegame.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(history);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //endregion

    //region Listeners

    public void addCurrentPlayerListener(CurrentPlayerListener listener) { currentPlayerListeners.add(listener); }
    public void addGamePhaseListener(GamePhaseListener listener) { gamePhaseListeners.add(listener); }

    public void removeCurrentPlayerListener(CurrentPlayerListener listener) { currentPlayerListeners.remove(listener); }
    public void removeGamePhaseListener(GamePhaseListener listener) { gamePhaseListeners.remove(listener); }

    private void notifyPlayerChanged() {
        for (CurrentPlayerListener cpl : currentPlayerListeners)
            cpl.playerChanged(currentPlayer);
    }

    private void notifyGamePhaseChanged() {
        for (GamePhaseListener gpl : gamePhaseListeners) {
            gpl.gamePhaseChanged(phase);
        }
    }

    //endregion

    //region Debug

    private void printMove(Move moveToPrint) {
        System.out.println("Played By: " + moveToPrint.getPlayerColor());
        System.out.println("Removed Tile: " + moveToPrint.getIsTileRemoved());

        for (Tile t: moveToPrint.getMove()) {
            System.out.println("Tile: (" + t.getX() + ", " + t.getY() + ")");
        }
        System.out.println("");
        System.out.println("");
    }

    //endregion
}
