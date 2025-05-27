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

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Фургон кави");
        primaryStage.setScene(scene);
        
        // Завантаження іконки з ресурсів
        Image icon = new Image(getClass().getResourceAsStream("img/icon.png"));
        if (icon != null) {
            primaryStage.getIcons().add(icon);
        }
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}