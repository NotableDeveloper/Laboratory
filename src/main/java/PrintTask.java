import java.util.concurrent.BlockingQueue;

public class PrintTask implements Runnable {
    private final BlockingQueue<String> printingQueue;

    public PrintTask(BlockingQueue<String> printingQueue) {
        this.printingQueue = printingQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String token = printingQueue.take();
                System.out.println(token);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
