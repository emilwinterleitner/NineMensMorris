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
                if (++tiles_placed > 18) {
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
        PlayerColor color = moveToUndo.getPlayerColor();
        Boolean isTileRemoved = moveToUndo.getIsTileRemoved();
        ArrayList<Tile> tiles = moveToUndo.getMove();
        int tileIdx = tiles.size() - 1;

        printMove(moveToUndo);

        if (isTileRemoved) {
            Tile tileToSet = tiles.get(tileIdx--);
            board.tryToSetTile(tileToSet.getY(), tileToSet.getX(),
                color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE);
            board.removeMerelFromHistory(tiles.get(tileIdx));
        }
        Tile tileToRemove = tiles.get(tileIdx--);
        board.removeTileFromHistory(tileToRemove);
        if (tileIdx == 0) {
            Tile tileToSet = tiles.get(tileIdx);
            board.tryToSetTile(tileToSet.getY(), tileToSet.getX(), color);
        }
        changePlayer();
    }

    public void Redo() {
        System.out.println("REDO");
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
}
