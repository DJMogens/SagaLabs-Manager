package com.example.sagalabsmanager;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        //Creates the initial scene
        Scene scene = new Scene(new Pane());

        ViewSwitcher.setScene(scene);
        ViewSwitcher.switchView(View.LOGIN);

        stage.setScene(scene);
        stage.setTitle("SagaLabs-Manager");
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}