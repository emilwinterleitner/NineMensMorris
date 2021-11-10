package NMM.Controller;

import NMM.Enums.PlayerColor;
import NMM.GameManager;
import NMM.Interfaces.CurrentPlayerListener;
import NMM.Interfaces.TilePlacedListener;
import NMM.Model.Player;
import NMM.Model.Tile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import NMM.Model.Board;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Controller implements CurrentPlayerListener, TilePlacedListener {
    private Board board;
    private GameManager manager;

    private ObservableList<Button> gameBoard = FXCollections.observableArrayList();

    @FXML
    private GridPane boardGrid;
    @FXML
    private Label currentPlayerLabel;
    @FXML Button btn00;
    @FXML Button btn03;
    @FXML Button btn06;
    @FXML Button btn11;
    @FXML Button btn13;
    @FXML Button btn15;
    @FXML Button btn22;
    @FXML Button btn23;
    @FXML Button btn24;
    @FXML Button btn30;
    @FXML Button btn31;
    @FXML Button btn32;
    @FXML Button btn34;
    @FXML Button btn35;
    @FXML Button btn36;
    @FXML Button btn42;
    @FXML Button btn43;
    @FXML Button btn44;
    @FXML Button btn51;
    @FXML Button btn53;
    @FXML Button btn55;
    @FXML Button btn60;
    @FXML Button btn63;
    @FXML Button btn66;

    private GridPane pane;

    public void handleNewGame(ActionEvent actionEvent) {
        System.out.println("New Game");
    }

    public void handleSave(ActionEvent actionEvent) {
        System.out.println("Save");
    }

    public void handleLoad(ActionEvent actionEvent) {
        System.out.println("Load");
    }

    public void setGameManager(GameManager manager) {
        this.manager = manager;
        manager.addCurrentPlayerListener(this::playerChanged);
    }

    @FXML
    private void initialize() {
        board = Board.getInstance();
        board.addTilePlacedListener(this::tilePlaced);

        btn00.setOnAction(event -> tileClicked(event.getTarget()));
        btn03.setOnAction(event -> tileClicked(event.getTarget()));
        btn06.setOnAction(event -> tileClicked(event.getTarget()));
        btn11.setOnAction(event -> tileClicked(event.getTarget()));
        btn13.setOnAction(event -> tileClicked(event.getTarget()));
        btn15.setOnAction(event -> tileClicked(event.getTarget()));
        btn22.setOnAction(event -> tileClicked(event.getTarget()));
        btn23.setOnAction(event -> tileClicked(event.getTarget()));
        btn24.setOnAction(event -> tileClicked(event.getTarget()));
        btn30.setOnAction(event -> tileClicked(event.getTarget()));
        btn31.setOnAction(event -> tileClicked(event.getTarget()));
        btn32.setOnAction(event -> tileClicked(event.getTarget()));
        btn34.setOnAction(event -> tileClicked(event.getTarget()));
        btn35.setOnAction(event -> tileClicked(event.getTarget()));
        btn36.setOnAction(event -> tileClicked(event.getTarget()));
        btn42.setOnAction(event -> tileClicked(event.getTarget()));
        btn43.setOnAction(event -> tileClicked(event.getTarget()));
        btn44.setOnAction(event -> tileClicked(event.getTarget()));
        btn51.setOnAction(event -> tileClicked(event.getTarget()));
        btn53.setOnAction(event -> tileClicked(event.getTarget()));
        btn55.setOnAction(event -> tileClicked(event.getTarget()));
        btn60.setOnAction(event -> tileClicked(event.getTarget()));
        btn63.setOnAction(event -> tileClicked(event.getTarget()));
        btn66.setOnAction(event -> tileClicked(event.getTarget()));

    }

    private void tileClicked(EventTarget target) {
        Node node = (Node) target;
        int row = GridPane.getRowIndex(node);
        int col = GridPane.getColumnIndex(node);
        manager.tilePressed(row, col);
    }

    @Override
    public void playerChanged(Player currentPlayer) {
        currentPlayerLabel.setText(currentPlayer.getPlayerName());
    }

    @Override
    public void tilePlaced(Tile t, PlayerColor color) {
        System.out.println("Tile placed");
        for (Node n : boardGrid.getChildren()) {
            if (GridPane.getRowIndex(n) == t.getY()
                && GridPane.getColumnIndex(n) == t.getX()) {
                Button b = (Button) n;
                Image img = new Image("file:res/textures/white.png");
                ImageView imgView = new ImageView(img);
                b.setGraphic(imgView);
            }
        }
    }
}
