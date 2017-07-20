package com.tylerj.mdkdownloader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.xml.soap.Text;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MDKDownloader v1.0");

        MDKDownloader mdkDownloader = new MDKDownloader();

        StackPane root = new StackPane();
        Label programLabel = new Label();

        Label modNameLabel = new Label();
        TextField modNameTextField = new TextField();

        Label comboBoxLabel = new Label();
        ComboBox comboBox = new ComboBox();

        Button btn = new Button();

        btn.setText("Download MDK");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    mdkDownloader.PrepareEnvironment("forge-1.12-14.21.1.2415-mdk", System.getProperty("user.home") + "/", "MyFirstMod");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        for (String s : mdkDownloader.GetMDKVersions()) {
            comboBox.getItems().add(s);
        }

        programLabel.alignmentProperty();

        root.getChildren().add(programLabel);

        root.getChildren().add(btn);
        //root.getChildren().add(comboBox);

        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.show();
    }
}