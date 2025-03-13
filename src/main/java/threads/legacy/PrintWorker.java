package threads.legacy;

public class PrintWorker extends Thread {
    private LinkedQueue printingQueue;


    public PrintWorker(LinkedQueue printingQueue) {
        this.printingQueue = printingQueue;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                String token = printingQueue.get();
                System.out.println(token);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
