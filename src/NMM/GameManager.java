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

    private Move move;

    private List<CurrentPlayerListener> currentPlayerListeners = new ArrayList<CurrentPlayerListener>();

    private List<GamePhaseListener> gamePhaseListeners = new ArrayList<GamePhaseListener>();

    private Board board;

    private GamePhase phase;

    private History history;

    private int tiles_placed;

    public GameManager(Player p1, Player p2, GamePhase phase, History hist) {
        board = Board.getInstance();

        player1 = p1;
        player2 = p2;

        tiles_placed = 0;

        gameManagerState = new GameManagerPlaceState();

        if (hist != null)
            history = hist;
        else
            history = new History();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public void addCurrentPlayerListener(CurrentPlayerListener listener) {
        currentPlayerListeners.add(listener);
    }

    public void addGamePhaseListener(GamePhaseListener listener) {
        gamePhaseListeners.add(listener);
    }

    public Board getBoard() {
        return board;
    }

    public GamePhase getPhase() {
        return phase;
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

    public void startGame() {
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
                System.out.println("Placed: " + ++tiles_placed);
                if (tiles_placed > 17) {
                    gameManagerState = new GameManagerMoveState();
                    phase = GamePhase.MOVE;
                    notifyGamePhaseChanged();
                }
            }

            if (gameManagerState instanceof GameManagerRemoveState) {
                move.setIsTileRemoved();
                if (phase == GamePhase.MOVE)
                    gameManagerState = new GameManagerMoveState();
                else
                    gameManagerState = new GameManagerPlaceState();
            }

            if (board.checkForMerel())
                gameManagerState = new GameManagerRemoveState();

            if (!(gameManagerState instanceof GameManagerRemoveState))
                endTurn();
        }
    }

    private void endTurn() {
        history.addMove(move);
        changePlayer();
    }

    private void notifyPlayerChanged() {
        for (CurrentPlayerListener cpl : currentPlayerListeners)
            cpl.playerChanged(currentPlayer);
    }

    private void notifyGamePhaseChanged() {
        for (GamePhaseListener gpl : gamePhaseListeners) {
            gpl.gamePhaseChanged(phase);
        }
    }

    public void Undo() {
        Move moveToUndo = history.undoMove();

        if (moveToUndo != null) {
            PlayerColor color = moveToUndo.getPlayerColor();
            Boolean isTileRemoved = moveToUndo.getIsTileRemoved();
            ArrayList<Tile> tiles = moveToUndo.getMove();
            int tileIdx = tiles.size() - 1;

            printMove(moveToUndo);

            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                tiles_placed--;
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

            printMove(moveToRedo);

            if (tiles.size() == 1 || tiles.size() == 2 && isTileRemoved) {
                if (phase == GamePhase.PLACE) {
                    if (++tiles_placed > 17) {
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

    private void printMove(Move moveToPrint) {
        System.out.println("Played By: " + moveToPrint.getPlayerColor());
        System.out.println("Removed Tile: " + moveToPrint.getIsTileRemoved());

        for (Tile t: moveToPrint.getMove()) {
            System.out.println("Tile: (" + t.getX() + ", " + t.getY() + ")");
        }
        System.out.println("");
        System.out.println("");
    }

    public void Save() {
        history.setGameBoard(board.getGameBoard());
        history.setMerelManager(board.getMerelManager());
        history.setCurrentGamePhase(phase);
        history.setPlayer1(player1);
        history.setPlayer2(player2);
        history.setCurrentPlayer(currentPlayer.getPlayerColor());
        try {
            FileOutputStream fileOut =
                new FileOutputStream("/tmp/savegame.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(history);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/savegame.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void Load() {
        try {
            FileInputStream fileIn = new FileInputStream("/tmp/savegame.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            history = (History) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        board.setGameBoard(history.getGameBoard());
        board.setMerels(history.getMerelManager());
        phase = history.getCurrentGamePhase();
        player1 = history.getPlayer1();
        player2 = history.getPlayer2();
        currentPlayer = player1.getPlayerColor() == history.getCurrentPlayer() ? player1 : player2;
        notifyGamePhaseChanged();
        notifyPlayerChanged();
    }
}
