package com.elanzone.books.noteeg.chpt8.sect03;


import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        // 9. 创建一个有 3 个参与者的 Phaser 对象
        Phaser phaser = new Phaser(3);

        // 10. 创建 3 个线程来执行 3 项任务
        for (int i = 0; i < 3; i++) {
            Task task = new Task(i + 1, phaser); // 每阶段执行时间为 i+1 秒
            Thread thread = new Thread(task);
            thread.start();
        }

        // 11. 循环 10 次输出 phaser 对象的信息
        for (int i = 0; i < 10; i++) {
            // 12. 输出当前阶段、注册的参与人数、已到达人数、未到达人数
            System.out.printf("********************\n");
            System.out.printf("Main: Phaser Log\n");
            System.out.printf("Main: Phaser: Phase: %d\n", phaser.getPhase());
            System.out.printf("Main: Phaser: Registered Parties: %d\n", phaser.getRegisteredParties());
            System.out.printf("Main: Phaser: Arrived Parties: %d\n", phaser.getArrivedParties());
            System.out.printf("Main: Phaser: Unarrived Parties: %d\n", phaser.getUnarrivedParties());
            System.out.printf("********************\n");

            // 睡上 1 秒
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
