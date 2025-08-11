package threads.executor;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final int taskCount;
    private final TaskQueue queue;

    public Producer(int taskCount) {
        this.taskCount = taskCount;
        this.queue = TaskQueue.getInstance();
    }

    @Override
    public void run() {
        for (int i = 0; i < taskCount; i++) {
            try {
                Task task = new Task(i);
                System.out.println("Producing task " + i);
                queue.put(task);
                Thread.sleep(100); // Simulate producer work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Producer finished producing tasks.");
    }
}
