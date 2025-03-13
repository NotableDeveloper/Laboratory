package threads.legacy;

public class Worker extends Thread {
    private LinkedQueue waitingQueue;
    private LinkedQueue nextTaskQueue;

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            /*
                do something with Queue...
             */
        }
    }
}
