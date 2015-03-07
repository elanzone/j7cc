package com.elanzone.books.noteeg.chpt8.sect06;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = MyLogger.getLogger("Core");
        // 17. 用 entering() 方法输出一条表示主程序开始执行的日志信息
        logger.entering("Core", "main()", args);

        Thread threads[] = new Thread[5];

        for (int i = 0; i < threads.length; i++) {
            logger.log(Level.INFO, "Launching thread: " + i);
            Task task = new Task();
            threads[i] = new Thread(task);
            logger.log(Level.INFO, "Thread created: " + threads[i].getName());
            threads[i].start();
        }

        // 20. 输出 INFO 级别的日志，表示已创建了线程
        logger.log(Level.INFO, "Ten Threads created. Waiting for its finalization");

        // 21. 调用 join() 方法等待5个线程结束。每个线程计数后，输出日志表示线程已结束
        for (Thread thread : threads) {
            try {
                thread.join();
                logger.log(Level.INFO, "Thread has finished its execution", thread);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Exception", e);
            }
        }

        // 22. 用 exiting() 方法输出一条表示主程序结束执行的日志信息
        logger.exiting("Core", "main()");
    }
}
