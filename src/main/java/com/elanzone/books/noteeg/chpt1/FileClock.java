package com.elanzone.books.noteeg.chpt1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FileClock implements Runnable {

    @Override
    public void run() {

        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                // sleep 1 秒
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // 如果被中断，则输出中断信息
                System.out.println("The FileClock has been interrupted");
            }
        }

    }
}
