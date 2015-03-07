package com.elanzone.books.noteeg.chpt8.sect03;


import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    private int time;   // 每阶段需要花的时间（单位为秒）
    private Phaser phaser;

    public Task(int time, Phaser phaser) {
        this.time = time;
        this.phaser = phaser;
    }

    @Override
    public void run() {
        // 5. 报到
        phaser.arrive();

        // 6. 阶段1
        //    1) 输出阶段1开始的信息到终端；
        //    2) 睡上 time 属性指定的时间；
        //    3) 输出阶段1结束的信息到终端；
        //    4) 调用 phaser 属性的 arriveAndAwaitAdvance() 方法等待其他任务
        System.out.printf("%s: Entering phase 1.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 1.\n", Thread.currentThread().getName());
        phaser.arriveAndAwaitAdvance();


        // 7. 阶段2 和 阶段3 （业务逻辑参考阶段1）；
        //    注意在第3阶段结束时，调用 arriveAndDeregister() 方法，而不是 arriveAndAwaitAdvance() 方法
        // 7.1 阶段2
        System.out.printf("%s: Entering phase 2.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 2.\n", Thread.currentThread().getName());
        phaser.arriveAndAwaitAdvance();

        // 7.2 阶段3
        System.out.printf("%s: Entering phase 3.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 3.\n", Thread.currentThread().getName());
        phaser.arriveAndDeregister();  // 注意 Deregister
    }
}
