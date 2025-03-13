package threads.legacy;

import java.util.LinkedList;

public class LinkedQueue {
    private String name;
    private int count;
    private LinkedList<String> queue;

    public LinkedQueue(String name) {
        this.name = name;
        this.count = 0;
        queue = new LinkedList();
    }

    public synchronized void put(String item){
        queue.add(item);
        count++;
        notify();
    }

    public synchronized String get() throws InterruptedException {
        while(count <= 0){
            wait();
        }

        String result = queue.removeFirst();
        count--;
        notifyAll();

        return result;
    }
}
