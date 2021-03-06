package NMM.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MerelManager implements Serializable {
    private List<Merel> merels;

    public MerelManager() {
        merels = new ArrayList<>();
    }

    public void addMerel(Merel merel) {
        merels.add(merel);
    }

    public void removeMerel(Tile tile) {
        Merel merel = null;

        for (Merel m : merels) {
            for (Tile t : m.getTiles()) {
                if (tile.equals(t)) {
                    merel = m;
                    break;
                }
            }
        }

        merels.remove(merel);
    }

    public boolean isPartOfMerel(Tile tile) {
        boolean result = false;

        for (Merel m : merels) {
            for (Tile t : m.getTiles()) {
                if (t.equals(tile))
                    result = true;
            }
        }

        return result;
    }
}
