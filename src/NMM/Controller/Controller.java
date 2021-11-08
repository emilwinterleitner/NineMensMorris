package NMM.Controller;

import javafx.event.ActionEvent;
import NMM.Model.Board;
import javafx.fxml.FXML;

public class Controller {
    private Board board;

    public void handleNewGame(ActionEvent actionEvent) {
        System.out.println("New Game");
    }

    public void handleSave(ActionEvent actionEvent) {
        System.out.println("Save");
    }

    public void handleLoad(ActionEvent actionEvent) {
        System.out.println("Load");
    }

    @FXML
    private void initialize() {
        board = Board.getInstance();
    }
}
