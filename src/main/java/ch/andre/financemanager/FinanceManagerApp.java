package ch.andre.financemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FinanceManagerApp extends Application{

    @Override
    public void start(Stage stage) {
        Label label = new Label("Finance Manager");

        Scene scene = new Scene(label, 400, 250);

        stage.setTitle("Finance Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
