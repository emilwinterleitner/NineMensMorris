package NMM;

import NMM.Enums.GamePhase;
import NMM.Interfaces.CurrentPlayerListener;
import NMM.Model.Board;
import NMM.Model.History;
import NMM.Model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player opponentPlayer;

    private List<CurrentPlayerListener> currentPlayerListeners = new ArrayList<CurrentPlayerListener>();

    private Board board;

    private GamePhase phase;

    private History history;

    private int tiles_placed;

    public GameManager(Player p1, Player p2, GamePhase phase, History hist) {
        board = Board.getInstance();

        player1 = p1;
        player2 = p2;

        this.phase = phase;

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

        for (CurrentPlayerListener cpl : currentPlayerListeners)
            cpl.playerChanged(currentPlayer);
    }

    public void tilePressed(int row, int col) {
        board.tryToSetTile(row, col, currentPlayer.getPlayerColor());
    }
}
