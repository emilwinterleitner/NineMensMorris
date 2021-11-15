package NMM.Model;

import java.util.ArrayList;
import java.util.List;

public class Merel {
    private final List<Tile> merel;

    public Merel (Tile a, Tile b, Tile c) {
        merel = new ArrayList<Tile>();
        merel.add(a);
        merel.add(b);
        merel.add(c);
    }

    public List<Tile> getTiles() {
        return merel;
    }
}
