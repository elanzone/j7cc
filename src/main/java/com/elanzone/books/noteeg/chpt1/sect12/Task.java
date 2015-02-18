package com.elanzone.books.noteeg.chpt1.sect12;

import java.util.Random;

public class Task implements Runnable {

    @Override
    public void run() {
        float result;
        Random random = new Random(Thread.currentThread().getId());
        while (true) {
            result = 1000 / ((int) (random.nextDouble() * 1000));
            System.out.printf("%d : %f\n", Thread.currentThread().getId(), result);
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%d : Interrupted\n", Thread.currentThread().getId());
                return;
            }
        }
    }

}
