/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.application.Platform;

/**
 * Holds anything related to multi-threading
 *
 * @author Richard
 */
public class Concurrent {

    private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    public static ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Runs a given task on the JavaFX thread, and blocks until the task is
     * done.
     *
     * @param task that needs to be run on the JavaFX thread
     */
    public static void runOnJavaFXThreadSynchronously(Runnable task) {

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                task.run();
                latch.countDown();
            });
            // Wait until the JavaFX thread is done to avoid synchronization
            // issues
            try {
                latch.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
