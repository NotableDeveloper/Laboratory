import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class WriteTask implements Runnable {
    private final BlockingQueue<String> waitingQueue;
    private final BlockingQueue<String> printingQueue;

    public WriteTask(BlockingQueue<String> waitingQueue, BlockingQueue<String> printingQueue) {
        this.waitingQueue = waitingQueue;
        this.printingQueue = printingQueue;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try{
                String item = waitingQueue.take();
                StringBuilder token = new StringBuilder(item);
                String uuid = UUID.randomUUID().toString();
                token.append("|");
                token.append(uuid);
                printingQueue.put(token.toString());
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
