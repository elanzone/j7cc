监控 Executor 框架
====

请回顾 [第四章 第1节 线程执行者介绍](../4线程执行者/01介绍.html)

在本节中，将学习能获得关于 ThreadPoolExecutor 类状态的哪些信息及如何获得。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt8.sect04 package中*


* Runnable 实现类 : Task : implements Runnable

    * 属性 milliseconds : long 类型 : 在 run() 方法中将睡这么多毫秒（为了精度，用了毫秒）
    * 在构造函数中将属性初始化为参数值

                private long milliseconds; // 将睡这么多毫秒

                public Task(long milliseconds) {
                    this.milliseconds = milliseconds;
                }

    * run() 方法 : 睡上 millisenconds 属性指定的时间(单位为毫秒)；睡前睡后输出任务开始、结束信息

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

* 控制类 : Main

    * main() 方法
        1. 用 Executors 的 newCachedThreadPool() 方法创建一个 ThreadPoolExecutor 对象 executor
        2. 创建 10 个任务并提交到 executor (每个任务将随机睡不到10秒)
        3. 每隔 1 秒调用 showLog() 方法输出 executor 的状态信息到终端；输出 5 次
        4. 调用 executor 的 shutdown() 方法停止 executor
        5. 每隔 1 秒调用 showLog() 方法输出 executor 的状态信息到终端；输出 5 次
        6. 调用 executor 的 awaitTermination() 方法等待所有任务完成后输出程序结束信息

                public static void main(String[] args) throws Exception {
                    // 6. 创建一个 Executor 对象 executor
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

                    // 7. 创建 10 个任务并提交到 executor (每个任务将随机睡不到10秒)
                    Random random = new Random();
                    for (int i = 0; i < 10; i++) {
                        Task task = new Task(random.nextInt(10000));
                        executor.submit(task);
                    }

                    // 8. 每隔 1 秒调用 showLog() 方法输出 executor 的状态信息到终端；输出 5 次
                    for (int i = 0; i < 5; i++) {
                        showLog(executor);
                        TimeUnit.SECONDS.sleep(1);
                    }

                    // 9. 停止 executor
                    executor.shutdown();

                    // 10. 每隔 1 秒调用 showLog() 方法输出 executor 的状态信息到终端；输出 5 次
                    for (int i = 0; i < 5; i++) {
                        showLog(executor);
                        TimeUnit.SECONDS.sleep(1);
                    }

                    // 11. 调用 executor 的 awaitTermination() 方法等待所有任务完成
                    executor.awaitTermination(1, TimeUnit.DAYS);

                    System.out.printf("Main: End of the program.\n");
                }

    * showLog() 方法：输出线程池大小、任务数和 executor 的状态等信息

                private static void showLog(ThreadPoolExecutor executor) {
                    System.out.printf("*********************");
                    System.out.printf("Main: Executor Log");
                    System.out.printf("Main: Executor: Core Pool Size: %d\n", executor.getCorePoolSize());
                    System.out.printf("Main: Executor: Pool Size: %d\n", executor.getPoolSize());
                    System.out.printf("Main: Executor: Active Count: %d\n", executor.getActiveCount());
                    System.out.printf("Main: Executor: Task Count: %d\n", executor.getTaskCount());
                    System.out.printf("Main: Executor: Completed Task Count: %d\n", executor.getCompletedTaskCount());
                    System.out.printf("Main: Executor: Shutdown: %s\n", executor.isShutdown());
                    System.out.printf("Main: Executor: Terminating: %s\n", executor.isTerminating());
                    System.out.printf("Main: Executor: Terminated: %s\n", executor.isTerminated());
                    System.out.printf("*********************\n");
                }


### 讲解

本节中，实现了一个将执行线程阻塞随机毫秒的任务。然后提交了10个任务到执行者，在等待它们完成时，输出关于执行者状态的信息到终端。
使用了以下方法来获得 Executor 对象的状态:

* getCorePoolSize(): 返回一个整型数字，表示线程的核心数。<br/>
                     它是执行者在没有执行任何任务时，内部线程池中的线程的数目（也是内部线程池中的最小线程数）。

* getPoolSize(): 返回一个整型数字，表示内部线程池的实际大小。
* getActiveCount(): 返回一个整型数字，表示当前正在执行任务的线程数。
* getTaskCount(): 返回一个长整型(long)数字，表示计划执行的任务数。
* getCompletedTaskCount(): 返回一个长整型(long)数字，表示已经被执行者执行并完成了的任务数。
* isShutdown(): 返回一个 Boolean 值，表示执行者的 shutdown() 方法是否被调用以结束执行。
* isTerminating(): 返回一个 Boolean 值，表示执行者的 shutdown() 方法是否已被调用但尚未结束。
* isTerminated(): 返回一个 Boolean 值，表示执行者是否已结束执行。




