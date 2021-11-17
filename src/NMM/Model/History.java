package NMM.Model;

import NMM.Enums.GamePhase;
import NMM.Enums.PlayerColor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class History implements Serializable {
    private List<Move> history;

    private Map<Tile, PlayerColor> gameBoard;
    private MerelManager merelManager;
    private GamePhase currentGamePhase;
    private Player player1;
    private Player player2;
    private PlayerColor currentPlayer;

    private int currentIndex = -1;

    public History() {
        history = new LinkedList<>();
    }

    public void addMove(Move move) {
        if (currentIndex != history.size() - 1)
            history = history.subList(0, currentIndex + 1);
        history.add(move);
        currentIndex = history.size() - 1;
    }

    public Move undoMove() {
        Move move = null;

        if (currentIndex >= 0)
            move = history.get(currentIndex--);

        return move;
    }

    public Move redoMove() {
        Move move = null;

        if (currentIndex <= history.size() - 2)
            move = history.get(++currentIndex);

        return move;
    }

    public Map<Tile, PlayerColor> getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Map<Tile, PlayerColor> gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MerelManager getMerelManager() {
        return merelManager;
    }

    public void setMerelManager(MerelManager merelManager) {
        this.merelManager = merelManager;
    }

    public GamePhase getCurrentGamePhase() {
        return currentGamePhase;
    }

    public void setCurrentGamePhase(GamePhase currentGamePhase) {
        this.currentGamePhase = currentGamePhase;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerColor currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
