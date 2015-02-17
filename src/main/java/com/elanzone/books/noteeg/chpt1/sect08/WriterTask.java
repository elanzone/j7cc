package com.elanzone.books.noteeg.chpt1.sect08;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class WriterTask implements Runnable {

    private Deque<Event> deque;

    public WriterTask(Deque<Event> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {

        for (int i = 1; i < 100; i++) {
            String eventName = String.format("The thread %s has generated an event", Thread.currentThread().getId());
            deque.addFirst(new Event(eventName));
            System.out.printf("%s: deque's size: %d\n", eventName, deque.size());

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
