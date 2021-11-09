package NMM;

import NMM.Controller.Controller;
import NMM.Enums.GamePhase;
import NMM.Enums.PlayerColor;
import NMM.Model.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private GameManager manager;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("View/RootWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Nine Men's Morris");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        Player p1 = new Player(PlayerColor.WHITE, "Player 1");
        Player p2 = new Player(PlayerColor.BLACK, "Player 2");
        manager = new GameManager(p1, p2, GamePhase.PLACE, null);

        controller = loader.getController();
        controller.setGameManager(manager);

        manager.startGame();
    }


    public static void main(String[] args) { launch(args); }
}
