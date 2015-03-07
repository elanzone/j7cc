package com.elanzone.books.noteeg.chpt8.sect04;


import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    private long milliseconds; // 将睡这么多毫秒

    public Task(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public void run() {
        // 睡上 millisenconds 属性指定的时间；睡前睡后输出任务开始、结束信息
        System.out.printf("%s: Begin\n",Thread.currentThread().getName());
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: End\n",Thread.currentThread().getName());
    }
}
