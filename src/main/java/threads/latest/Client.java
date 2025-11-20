package threads.latest;

import java.util.concurrent.*;

public class Client {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        BlockingQueue<String> waitingQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> printingQueue = new LinkedBlockingQueue<>();

        Runnable writeTask = new WriteTask(waitingQueue, printingQueue);
        Runnable printTask = new PrintTask(printingQueue);

        executor.submit(writeTask);
        executor.submit(printTask);

        for (int i = 0; i < 1000; i++) {
            try {
                waitingQueue.put("Text");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            while(!waitingQueue.isEmpty() || !printingQueue.isEmpty()){
                Thread.sleep(100);
            }

            executor.shutdownNow();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
