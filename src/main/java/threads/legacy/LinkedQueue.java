package threads.legacy;

import java.util.LinkedList;

public class LinkedQueue {
    private String name;
    private LinkedList<String> queue;

    public LinkedQueue(String name) {
        this.name = name;
        queue = new LinkedList();
    }

    public synchronized void put(String item){
        queue.add(item);
        notifyAll();
    }

    public synchronized String get() throws InterruptedException {
        while(queue.isEmpty()){
            wait();
        }

        String result = queue.removeFirst();
        notifyAll();

        return result;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
