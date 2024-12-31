package me.piitex.rsdk.utils;

import javafx.application.Platform;

public class Tasks {

    public static void runAsync(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void runJavaFXThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public static void runSync(Runnable runnable) {
        runnable.run();
    }
}
