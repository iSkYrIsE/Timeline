package main;

import Controllers.MainController;
import Utils.DatabaseController;
import ch.vorburger.exec.ManagedProcessException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    //final private Persistance persistance = Persistance.DB;
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    static public void setPrimaryStage(Stage primaryStage) {
        primaryStage.setMinHeight(MainController.WINDOW_HEIGHT);
        primaryStage.setMinWidth(700);
        primaryStage.setMaxHeight(MainController.WINDOW_HEIGHT);
        Main.primaryStage = primaryStage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ManagedProcessException {
        //db start
        try {
            DatabaseController.startDB();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // set stage
        this.primaryStage = primaryStage;

        String mainFxmlPathString = "/Views/Main.fxml";
        MainController controller = new MainController();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(mainFxmlPathString));

        primaryStage.setTitle("Sparkling new Timeline Manager");
        StackPane main = loader.load();
        main.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(main, controller.WINDOW_WIDTH, controller.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(controller.WINDOW_HEIGHT);
        primaryStage.setMinWidth(700);
        primaryStage.setMaxHeight(controller.WINDOW_HEIGHT);
        primaryStage.show();
    }

}

