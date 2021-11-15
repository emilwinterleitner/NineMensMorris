package NMM.Model;

import java.util.LinkedList;

public class History {
    private LinkedList<Move> history;

    private int currentIndex = -1;

    public History() {
        history = new LinkedList<>();
    }

    public void addMove(Move move) {
        history.add(move);
        currentIndex = history.size() - 1;
    }

    public Move undoMove() {
        currentIndex = currentIndex == 0 ? 0 : --currentIndex;
        return history.get(currentIndex);
    }

    public Move redoMove() {
        currentIndex = currentIndex >= history.size() - 1 ? history.size() - 1 : ++currentIndex;
        return history.get(currentIndex);
    }
}
