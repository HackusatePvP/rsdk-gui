package me.piitex.rsdk;

import javafx.application.Application;
import javafx.stage.Stage;


public class Launch extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        RSDK rsdk = new RSDK();
    }
}
