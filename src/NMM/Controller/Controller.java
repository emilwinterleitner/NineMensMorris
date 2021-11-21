package NMM.Controller;

import NMM.Enums.GamePhase;
import NMM.Enums.PlayerColor;
import NMM.Factory.TileFactory;
import NMM.GameManager;
import NMM.Interfaces.*;
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
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller implements CurrentPlayerListener, TilePlacedListener, GamePhaseListener, TileSelectedListener,
    SelectedTileChangeListener, TileRemovedListener, AllowedMovesChangedListener, GameBoardChangedListener {
    private Board board;
    private GameManager manager;

    @FXML
    private GridPane boardGrid;
    @FXML
    private Label currentPlayerLabel;
    @FXML
    private Label currentPhaseLabel;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnRedo;
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

    @FXML
    private void initialize() {
        board = Board.getInstance();
        board.addTilePlacedListener(this);
        board.addTileSelectedListener(this);
        board.addSelectedTileChangeListener(this);
        board.addTileRemovedListener(this);
        board.addAllowedMovesChangedListener(this);
        board.addGameBoardChangedListener(this);

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

        btnUndo.setOnAction(event -> manager.Undo());
        btnRedo.setOnAction(event -> manager.Redo());
    }


    //region Menu

    public void handleNewGame(ActionEvent actionEvent) {
        resetGameBoard();
        setGameManager(new GameManager());
        manager.startGame();
    }

    public void handleSave(ActionEvent actionEvent) {
        manager.Save();
    }

    public void handleLoad(ActionEvent actionEvent) {
        setGameManager(new GameManager());
        manager.Load();
    }

    //endregion

    public void setGameManager(GameManager manager) {
        if (manager != null) {
            if (this.manager != null) {
                manager.removeCurrentPlayerListener(this);
                manager.removeGamePhaseListener(this);
            }
            this.manager = manager;
            manager.addCurrentPlayerListener(this);
            manager.addGamePhaseListener(this);
        }
    }

    private void tileClicked(EventTarget target) {
        Node node = (Node) target;
        int row = GridPane.getRowIndex(node);
        int col = GridPane.getColumnIndex(node);
        manager.tilePressed(row, col);
    }

    //region Interfaces

    @Override
    public void playerChanged(Player currentPlayer) {
        currentPlayerLabel.setText(currentPlayer.getPlayerName());
    }

    @Override
    public void tilePlaced(Tile t, PlayerColor color) {
        Button b = (Button) getNode(t.getX(), t.getY());
        b.setGraphic(TileFactory.getTile(color, false, false));
    }

    @Override
    public void gamePhaseChanged(GamePhase gamePhase) {
        currentPhaseLabel.setText(gamePhase.toString());
    }

    @Override
    public void tileSelected(Tile t, PlayerColor color) {
        Button b = (Button) getNode(t.getX(), t.getY());
        b.setGraphic(TileFactory.getTile(color, false, true));
    }

    @Override
    public void selectedTileChanged(Tile t, PlayerColor color) {
        Button b = (Button) getNode(t.getX(), t.getY());
        b.setGraphic(TileFactory.getTile(color, false, false));
    }

    @Override
    public void tileRemoved(Tile t) {
        Button b = (Button) getNode(t.getX(), t.getY());
        b.setGraphic(null);
    }

    @Override
    public void allowedTilesChanged(List<Tile> tiles, boolean show) {
        for (Tile t : tiles) {
            Button b = (Button) getNode(t.getX(), t.getY());
            if (show)
                b.setGraphic(TileFactory.getTile(PlayerColor.GREEN, false, false));
            else
                b.setGraphic(null);
        }
    }

    @Override
    public void gameBoardChanged(Map<Tile, PlayerColor> gameBoard) {
        resetGameBoard();

        for (Tile t : gameBoard.keySet()) {
            Button b = (Button) getNode(t.getX(), t.getY());
            b.setGraphic(TileFactory.getTile(gameBoard.get(t), false, false));
        }
    }

    //endregion

    private Node getNode(int x, int y) {
        Node node = null;

        for (Node n : boardGrid.getChildren()) {
            if (GridPane.getRowIndex(n) == y
                && GridPane.getColumnIndex(n) == x) {
                node = n;
                break;
            }
        }

        return node;
    }

    public void resetGameBoard() {
        for (Node n : boardGrid.getChildren()) {
            Button b = (Button) n;
            b.setGraphic(null);
        }
    }
}
