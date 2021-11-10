package NMM;

import NMM.Enums.GamePhase;
import NMM.GameManagerState.GameManagerMoveState;
import NMM.GameManagerState.GameManagerPlaceState;
import NMM.GameManagerState.GameManagerState;
import NMM.Interfaces.CurrentPlayerListener;
import NMM.Interfaces.GamePhaseListener;
import NMM.Model.Board;
import NMM.Model.History;
import NMM.Model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private GameManagerState gameManagerState;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player opponentPlayer;

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
    }

    public void startGame() {
        currentPlayer = player1;
        phase = GamePhase.PLACE;
        notifyPlayerChanged();
        notifyGamePhaseChanged();
    }

    public void tilePressed(int row, int col) {
        boolean validMove = gameManagerState.tilePressed(board, row, col, currentPlayer.getPlayerColor());



        if (gameManagerState instanceof GameManagerPlaceState) {
            if (++tiles_placed > 17) {
                gameManagerState = new GameManagerMoveState();
                phase = GamePhase.MOVE;
                notifyGamePhaseChanged();
            }
        }

        if (validMove)
            endTurn();
    }

    private void endTurn() {
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
}
