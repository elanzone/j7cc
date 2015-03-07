package com.elanzone.books.noteeg.chpt8.sect06;


import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Task implements Runnable {

    @Override
    public void run() {
        // 12. 获得类名对应的 Logger
        Logger logger = MyLogger.getLogger(this.getClass().getName());

        // 13. 用 entering() 方法输出一条表示方法开始执行的日志信息
        logger.entering(Thread.currentThread().getName(), "run()");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 14. 用 exiting() 方法输出一条表示方法结束执行的日志信息
        logger.exiting(Thread.currentThread().getName(), "run()", Thread.currentThread());
    }

}
