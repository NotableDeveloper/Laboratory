package threads.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class Consumer implements Runnable {
    private final BlockingQueue<Runnable> queue;
    private final ExecutorService executorService;

    public Consumer(BlockingQueue<Runnable> queue, ExecutorService executorService) {
        this.queue = queue;
        this.executorService = executorService;
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
