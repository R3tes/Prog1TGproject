package com.example.prog1tgproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1210, 640);
        Image icon = new Image("/app-logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("ImageViewer");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            if (ImageChanger.timer != null) {
                ImageChanger.timer.cancel();
                ImageChanger.timer.purge();
                ImageChanger.timer = null;
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}