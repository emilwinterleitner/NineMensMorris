package NMM;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View/RootWindow.fxml"));
        primaryStage.setTitle("Nine Men's Morris");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        String OS;

        OS = System.getProperty("os.name").toLowerCase();

        // worked in swing in the past - maybe we dont need it afterall
        if (OS.contains("mac")) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Nine Men's Morris"); // does not seem to work in IDE
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        launch(args);
    }
}
