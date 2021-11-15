package NMM.Model;

import java.util.LinkedList;
import java.util.List;

public class History {
    private List<Move> history;

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
        currentIndex = Math.max(currentIndex, 0);
        return history.get(currentIndex--);
    }

    public Move redoMove() {
        currentIndex = currentIndex >= history.size() - 1 ? history.size() - 2 : currentIndex;
        return history.get(++currentIndex);
    }
}
