package threads.legacy;

import java.util.UUID;

public class WriteWorker extends Thread {
    private LinkedQueue waitingQueue;
    private LinkedQueue printingQueue;

    public WriteWorker(LinkedQueue waitingQueue, LinkedQueue printingQueue) {
        this.waitingQueue = waitingQueue;
        this.printingQueue = printingQueue;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try{
                StringBuilder token = new StringBuilder(waitingQueue.get());
                String uuid = UUID.randomUUID().toString();
                token.append("|");
                token.append(uuid);
                printingQueue.put(token.toString());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }
}
