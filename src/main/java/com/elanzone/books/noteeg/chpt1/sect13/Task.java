package com.elanzone.books.noteeg.chpt1.sect13;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
