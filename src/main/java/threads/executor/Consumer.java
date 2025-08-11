package threads.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class Consumer implements Runnable {
    private final ExecutorService executorService;
    private final TaskQueue queue;

    public Consumer(ExecutorService executorService) {
        this.executorService = executorService;
        this.queue = TaskQueue.getInstance();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = queue.take();
                executorService.submit(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Consumer stopping.");
    }
}
