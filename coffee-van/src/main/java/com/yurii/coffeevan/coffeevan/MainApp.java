package com.yurii.coffeevan.coffeevan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.Parent;

import java.io.IOException;

// запуск програми
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Фургон кави ☕");
        primaryStage.setScene(scene);
//        primaryStage.getIcons().add(new Image("C:/LP/4/kyrsova/coffee-van/src/main/resources/com/yurii/coffeevan/coffeevan/img/icon.png")); // Необов’язково, якщо є іконка
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}