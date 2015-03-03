package com.elanzone.books.noteeg.chpt7.sect05;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        MyThreadFactory factory=new MyThreadFactory("MyThreadFactory");

        ExecutorService executor = Executors.newCachedThreadPool(factory);

        MyTask task = new MyTask();
        executor.submit(task);

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: End of the example.\n");
    }
}
