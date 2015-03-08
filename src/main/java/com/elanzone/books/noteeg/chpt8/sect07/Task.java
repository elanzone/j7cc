package com.elanzone.books.noteeg.chpt8.sect07;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Task implements Runnable {

    private ReentrantLock lock;

    public Task(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            lock.unlock(); // 当被中断或其他原因抛出异常时不会释放锁
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
