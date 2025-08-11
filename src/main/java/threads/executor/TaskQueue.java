package threads.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A singleton class to manage the task queue.
 */
public class TaskQueue {
    private static final TaskQueue INSTANCE = new TaskQueue();
    private final BlockingQueue<Runnable> queue;

    private TaskQueue() {
        // The queue capacity is hardcoded here
        queue = new LinkedBlockingQueue<>(10);
    }

    public static TaskQueue getInstance() {
        return INSTANCE;
    }

    public void put(Runnable task) throws InterruptedException {
        queue.put(task);
    }

    public Runnable take() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
