package com.elanzone.books.noteeg.chpt8.sect04;


import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

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

    // 13. 输出线程池大小、任务数和 executor 的状态等信息
    private static void showLog(ThreadPoolExecutor executor) {
        System.out.printf("*********************\n");
        System.out.printf("Main: Executor Log:\n");
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

}
