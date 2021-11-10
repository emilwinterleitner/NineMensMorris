package NMM.Factory;

import NMM.Enums.PlayerColor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileFactory {
    public static ImageView getTile(PlayerColor color, boolean removable, boolean selected) {
        String path = "file:res/textures/" + color.toString().toLowerCase();
        if (removable)
            path += "_removable";
        if (selected)
            path += "_selected";
        path += ".png";
        Image img = new Image(path);

        return new ImageView(img);
    }
}
