package com.elanzone.books.noteeg.chpt7.sect05;


import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {

    private int counter;
    private String prefix;

    public MyThreadFactory(String prefix) {
        this.prefix = prefix;
        counter = 1;
    }

    @Override
    public Thread newThread(Runnable r) {
        MyThread myThread = new MyThread(r, prefix + "-" + counter);
        counter++;
        return myThread;
    }
}
