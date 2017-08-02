package com.tylerj.mdkdownloader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MDKDownloader v2.0");
        primaryStage.setResizable(false);

        // TODO Fix broken icon.
        primaryStage.getIcons().add(new Image("file:icon.png"));

        MDKDownloader mdkDownloader = new MDKDownloader();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 400, 200);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label modNameLabel = new Label("Mod Name:");
        grid.add(modNameLabel, 0, 1);

        TextField modNameTextField = new TextField();
        modNameTextField.setMinWidth(250);
        grid.add(modNameTextField, 1, 1);

        Label forgeVersionLabel = new Label("Forge Version:");
        grid.add(forgeVersionLabel, 0, 2);

        ComboBox forgeVersionsCombobox = new ComboBox();
        forgeVersionsCombobox.setMinWidth(250);
        grid.add(forgeVersionsCombobox, 1, 2);

        Label downloadDirectory = new Label("Directory:");
        grid.add(downloadDirectory, 0, 3);

        TextField downloadDirectoryTextEdit = new TextField();
        downloadDirectoryTextEdit.setMinWidth(250);
        grid.add(downloadDirectoryTextEdit, 1, 3);

        Button prepareEnviromentButton = new Button();
        prepareEnviromentButton.setMinWidth(250);
        grid.add(prepareEnviromentButton, 1, 5);

        for (String s : mdkDownloader.GetMDKVersions()) {
            forgeVersionsCombobox.getItems().add(s);
        }

        prepareEnviromentButton.setText("Download MDK");
        prepareEnviromentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    mdkDownloader.PrepareEnvironment(forgeVersionsCombobox.getValue().toString(), downloadDirectoryTextEdit.getText() + "/", modNameTextField.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}