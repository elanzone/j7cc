package com.elanzone.books.noteeg.chpt7.sect09;


import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    private MyLock lock;
    private String name;

    public Task(String name, MyLock lock) {
        this.name = name;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        System.out.printf("Task: %s: Take the lock\n", name);
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.printf("Task: %s: Free the lock\n", name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
