监控 Fork/Join 池
====

请回顾 [第五章 第1节 Fork/Join框架介绍](../5forkjoin框架/01介绍.html)

在本节中，将学习能获得关于 ForkJoinPool 类状态的哪些信息及如何获得。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt8.sect05 package中*


* Task 类 : 扩展 RecursiveAction

    * 属性 array : int 数组 : 待处理的数据
    * 属性 start, end : int 类型 : 待处理区块的起始、结束值
    * 构造函数

                private int array[];
                private int start, end;

                public Task(int[] array, int start, int end) {
                    this.array = array;
                    this.start = start;
                    this.end = end;
                }

    * compute() 方法: 经典的分治算法。
        * 规模阈值为 100
        * 每遍历一个元素，睡上5毫秒

                @Override
                protected void compute() {
                    if (end - start > 100) {
                        int mid = (start + end) / 2;
                        Task task1 = new Task(array, start, mid);
                        Task task2 = new Task(array, mid, end);
                        task1.fork();
                        task2.fork();
                        task1.join();
                        task2.join();
                    } else {
                        for (int i = start; i < end; i++) {
                            array[i]++;
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

* 控制类 : Main

    * main() 方法
        1. 创建一个 ForkJoinPool 对象 pool
        2. 创建一个大小为 10000 的 int 数组供处理
        3. 创建一个处理整个数组的 Task 任务并提交到 pool
        4. 每秒调用一次 showLog() 方法输出 pool 的状态, 直到任务结束
        5. 停止池并等待所有任务结束
        6. 调用 showLog() 方法输出 pool 的状态后输出程序结束信息

                public static void main(String[] args) throws Exception {
                    ForkJoinPool pool = new ForkJoinPool();

                    int array[] = new int[10000];
                    Task task1 = new Task(array, 0, array.length);
                    pool.execute(task1);

                    while (!task1.isDone()) {
                        showLog(pool);
                        TimeUnit.SECONDS.sleep(1);
                    }

                    pool.shutdown();
                    pool.awaitTermination(1, TimeUnit.DAYS);

                    showLog(pool);
                    System.out.printf("Main: End of the program.\n");
                }

    * showLog() 方法: 接受一个 ForkJoinPool 对象作为参数，输出它的状态及在执行中的线程、任务信息

                private static void showLog(ForkJoinPool pool) {
                    System.out.printf("**********************\n");
                    System.out.printf("Main: Fork/Join Pool log\n");
                    System.out.printf("Main: Fork/Join Pool: Parallelism: %d\n", pool.getParallelism());
                    System.out.printf("Main: Fork/Join Pool: Pool Size: %d\n", pool.getPoolSize());
                    System.out.printf("Main: Fork/Join Pool: Active Thread Count: %d\n", pool.getActiveThreadCount());
                    System.out.printf("Main: Fork/Join Pool: Running Thread Count: %d\n", pool.getRunningThreadCount());
                    System.out.printf("Main: Fork/Join Pool: Queued Submission: %d\n", pool.getQueuedSubmissionCount());
                    System.out.printf("Main: Fork/Join Pool: Queued Tasks: %d\n", pool.getQueuedTaskCount());
                    System.out.printf("Main: Fork/Join Pool: Queued Submissions: %s\n", pool.hasQueuedSubmissions());
                    System.out.printf("Main: Fork/Join Pool: Steal Count: %d\n", pool.getStealCount());
                    System.out.printf("Main: Fork/Join Pool: Terminated : %s\n", pool.isTerminated());
                    System.out.printf("**********************\n");
                }



### 讲解

在本节中，实现了一个任务用 ForkJoinPool 类和扩展了 RecursiveAction（能在 ForkJoinPool 类中执行的一中任务） 的 Task 类来增加一个数组的元素值。
当各任务在处理数组时，输出 ForkJoinPool 类的状态信息到终端。使用了以下方法来获得 ForkJoinPool 类的状态:

* getPoolSize(): 返回一个 int 值，表示 ForkJoinPool 的内部池中的线程数量
* getParallelism(): 返回 ForkJoinPool 的并行级别（默认和处理器数量相等）
* getActiveThreadCount(): 返回当前在执行任务的线程数量
* getRunningThreadCount(): 返回当前没有被任何同步机制阻塞的工作中的线程数量
* getQueuedSubmissionCount(): 返回已提交到池但还没有开始执行的任务数量
* getQueuedTaskCount(): 返回已提交到池并且已开始执行的任务数量
* hasQueuedSubmissions(): 返回一个 Boolean 值表示池中是否有尚未开始执行排队中的任务数量
* getStealCount(): 返回一个长整型数值，表示工作线程从其他线程偷任务的次数
* isTerminated(): 返回一个 Boolean 值表示 fork/join 池是否已结束执行








