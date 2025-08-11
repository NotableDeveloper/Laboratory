package threads.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ExecutorClient {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        int taskCount = 10;

        Thread producerThread = new Thread(new Producer(taskCount));
        Thread consumerThread = new Thread(new Consumer(executorService));

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for the queue to be empty
        while (!TaskQueue.getInstance().isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Shutdown executor
        executorService.shutdown();
        consumerThread.interrupt(); // Interrupt consumer thread to stop it
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("All tasks have been processed.");
    }
}
