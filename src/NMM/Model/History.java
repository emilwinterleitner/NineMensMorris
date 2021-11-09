package NMM.Model;

import java.util.LinkedList;

public class History {
    private LinkedList<Move> history;

    public History() {
        history = new LinkedList<>();
    }

    public void addMove(Move move) {
        history.add(move);
    }

    public void undoMove() {

    }
}
