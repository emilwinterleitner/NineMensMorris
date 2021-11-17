package NMM;

import NMM.Enums.GamePhase;
import NMM.Enums.PlayerColor;
import NMM.GameManagerState.GameManagerMoveState;
import NMM.GameManagerState.GameManagerPlaceState;
import NMM.GameManagerState.GameManagerRemoveState;
import NMM.GameManagerState.GameManagerState;
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
        phase = GamePhase.PLACE;
        notifyPlayerChanged();
        notifyGamePhaseChanged();
        move = new Move(currentPlayer.getPlayerColor());
    }

    public void tilePressed(int row, int col) {
        boolean validMove = gameManagerState.tilePressed(board, row, col, currentPlayer.getPlayerColor());

        if (validMove) {
            if (gameManagerState instanceof GameManagerMoveState)
                move.addTile(board.getOriginTile());

            move.addTile(board.getTile(row, col));

            if (gameManagerState instanceof GameManagerPlaceState) {
                currentPlayer.addTile();
                if (++tilesPlaced > 17) {
                    gameManagerState = new GameManagerMoveState();
                    phase = GamePhase.MOVE;
                    notifyGamePhaseChanged();
                }
            }

            if (gameManagerState instanceof GameManagerRemoveState) {
                opponentPlayer.removeTile();
                move.setIsTileRemoved();
                if (tilesPlaced > 17) {
                    gameManagerState = new GameManagerMoveState();
                    phase = GamePhase.MOVE;
                } else {
                    gameManagerState = new GameManagerPlaceState();
                    phase = GamePhase.PLACE;
                }
                notifyGamePhaseChanged();
            }

            if (board.checkForMerel()) {
                gameManagerState = new GameManagerRemoveState();
                phase = GamePhase.REMOVE;
                notifyGamePhaseChanged();
            }

            if (!(gameManagerState instanceof GameManagerRemoveState))
                endTurn();
        }
    }

    private void endTurn() {
        history.addMove(move);
        changePlayer();
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
    }

    //endregion

    //region Undo / Redo

    public void Undo() {
        Move moveToUndo = null;

        if (phase == GamePhase.REMOVE) {
            moveToUndo = move;
            changePlayer();
            if (tilesPlaced > 17) {
                gameManagerState = new GameManagerMoveState();
                phase = GamePhase.MOVE;
                notifyGamePhaseChanged();
            } else {
                gameManagerState = new GameManagerPlaceState();
                phase = GamePhase.PLACE;
                notifyGamePhaseChanged();
            }
        } else {
            moveToUndo = history.undoMove();
        }
        if (moveToUndo != null) {
            PlayerColor color = moveToUndo.getPlayerColor();
            Boolean isTileRemoved = moveToUndo.getIsTileRemoved();
            ArrayList<Tile> tiles = moveToUndo.getMove();
            int tileIdx = tiles.size() - 1;

            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                tilesPlaced--;
                currentPlayer.removeTile();
                if (phase == GamePhase.MOVE) {
                    gameManagerState = new GameManagerPlaceState();
                    phase = GamePhase.PLACE;
                    notifyGamePhaseChanged();
                }
            }

            if (isTileRemoved) {
                Tile tileToSet = tiles.get(tileIdx--);
                board.tryToSetTile(tileToSet.getY(), tileToSet.getX(),
                    color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE);
                board.removeMerelFromHistory(tiles.get(tileIdx));
                opponentPlayer.addTile();
            }
            Tile tileToRemove = tiles.get(tileIdx--);
            board.removeTileFromHistory(tileToRemove);
            if (tileIdx == 0) {
                Tile tileToSet = tiles.get(0);
                board.tryToSetTile(tileToSet.getY(), tileToSet.getX(), color);
            }
            changePlayer();
        }
    }

    public void Redo() {
        Move moveToRedo = history.redoMove();

        if (moveToRedo != null) {
            PlayerColor color = moveToRedo.getPlayerColor();
            Boolean isTileRemoved = moveToRedo.getIsTileRemoved();
            ArrayList<Tile> tiles = moveToRedo.getMove();
            int tileIdx = tiles.size() - 1;

            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                if (phase == GamePhase.PLACE) {
                    currentPlayer.addTile();
                    if (++tilesPlaced > 17) {
                        gameManagerState = new GameManagerMoveState();
                        phase = GamePhase.MOVE;
                        notifyGamePhaseChanged();
                    }
                }
            }

            if (isTileRemoved) {
                Tile tileToRemove = tiles.get(tileIdx--);
                board.removeTileFromHistory(tileToRemove);
                board.addMerelFromHistory(tiles.get(tileIdx));
                opponentPlayer.removeTile();
            }
            Tile tileToSet = tiles.get(tileIdx--);
            board.tryToSetTile(tileToSet.getY(), tileToSet.getX(), color);
            if (tileIdx == 0) {
                Tile tileToRemove = tiles.get(0);
                board.removeTileFromHistory(tileToRemove);
            }
            changePlayer();
        }
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
        tilesPlaced = history.getTilesPlaced();
        notifyGamePhaseChanged();
        notifyPlayerChanged();

        if (phase == GamePhase.PLACE)
            gameManagerState = new GameManagerPlaceState();
        else
            gameManagerState = new GameManagerMoveState();

        move = new Move(currentPlayer.getPlayerColor());
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
