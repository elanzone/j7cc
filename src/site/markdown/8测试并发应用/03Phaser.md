监控 Phaser 类
====

请回顾 [第三章 第6节 并发分阶段任务介绍](../3线程同步工具/06并发分阶段任务.html)

在本节中，您将学习可获得哪些关于一个 Phaser 类的状态的信息以及如何获得。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt8.sect03 package中*


* Runnable 实现类: Task : implements Runnable

    * 属性 time : int 类型 : 每阶段需要花的时间（单位为秒）
    * 属性 phaser : Phaser 对象 : 用来同步各阶段
    * 构造函数将各属性初始化为参数值

                private int time;   // 每阶段需要花的时间（单位为秒）
                private Phaser phaser;

                public Task(int time, Phaser phaser) {
                    this.time = time;
                    this.phaser = phaser;
                }

    * run() 方法 : 实现3阶段的任务处理
        1. 调用 phaser 的 arrive() 方法报到
        2. 在每阶段:
            * 睡上 time 属性指定的秒数
            * 睡前睡后输出阶段开始、结束的信息
            * 阶段 1、2 结束后调用 phaser 的 arriveAndAwaitAdvance() 方法告知 phaser 已到达并等待和其他参与者同步
            * 阶段 3 结束后调用 phaser 的 arriveAndDeregister() 方法告知 phaser 已到达并从 phaser 注销

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

* 控制类 : Main

    1. 创建一个有 3 个参与者的 Phaser 对象
    2. 创建 3 个线程来执行 3 项任务
    3. 循环 10 次输出 phaser 对象的信息。每次输出当前阶段、注册的参与人数、已到达人数、未到达人数后睡上1秒

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


### 讲解

在本节中，在 Task 类中实现了一个分阶段的任务。此分阶段任务有3个阶段并用一个 Phaser 接口来与其他 Task 对象同步。
main类启动3个任务并在任务执行各自阶段时打印出 phaser 对象的状态相关信息到终端。
使用了以下方法来获得 phaser 对象的状态:

* getPhase(): 返回一个 phaser 对象的实际阶段
* getRegisteredParties(): 返回使用一 phaser 对象作为同步机制的任务数量
* getArrivedParties(): 返回已到达实际阶段结尾的任务数量
* getUnarrivedParties(): 返回尚未到达实际阶段结尾的任务数量

