package ch.andre.financemanager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FinanceManagerApp extends Application{

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Label titleLabel = new Label("Finanace Manager");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox header = new HBox(titleLabel);
        header.setPadding(new Insets(20));

        root.setTop(header);

        Label balanceTitle = new Label("Kontostand");
        Label balanceValue = new Label("0.00 CHF");

        VBox balanceBox = new VBox(10);
        balanceBox.getChildren().addAll(
                balanceTitle,
                balanceValue
        );
        balanceBox.setPadding(new Insets(20));

        root.setCenter(balanceBox);

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Finance Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
