package threads.legacy;

public class Client {
    public static void main(String[] args) {
        LinkedQueue waitingQueue = new LinkedQueue("Waiting");
        LinkedQueue printingQueue = new LinkedQueue("Printing");

        WriteWorker writeWorker = new WriteWorker(waitingQueue, printingQueue);

        PrintWorker printWorker = new PrintWorker(printingQueue);

        writeWorker.start();
        printWorker.start();

        for(int i = 0; i < 1000; i++){
            waitingQueue.put("Text");
        }

        try {
            while(!waitingQueue.isEmpty() || !printingQueue.isEmpty()){
                Thread.sleep(100);
            }

            writeWorker.interrupt();
            printWorker.interrupt();

            writeWorker.join();
            printWorker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
