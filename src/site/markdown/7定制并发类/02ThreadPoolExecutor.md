定制 ThreadPoolExecutor 类
====

Executor框架此机制能让您分离线程的创建和执行。
它基于 Executor 和 ExecutorService 接口，以及实现这2个接口的 ThreadPoolExecutor 类。
它有个内部的线程池并提供方法让您提交各种任务使其在线程池中执行。这些任务是：

* Runnable 接口以实现不返回结果的任务
* Callable 接口以实现返回结果的任务

两种情况，您只能提交任务到执行者。执行者使用线程池中的一个线程或者创建一个新的线程来执行那些任务。
执行者也决定任务什么时刻执行。


### 任务

覆盖 ThreadPoolExecutor 类的某些方法来计算在执行者中执行的任务的执行时间，并在执行完成时把关于执行者的统计信息输出到终端。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt7.sect02 package中*


* ThreadPoolExecutor 扩展类 ：MyExecutor

    * 属性 startTimes : ConcurrentHashMap<String, Date> 对象 : 记录线程池中线程的hash值与启动时间
    * 在构造函数中初始化属性

                private ConcurrentHashMap<String, Date> startTimes;

                public MyExecutor(int corePoolSize, int maximumPoolSize,
                                  long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
                    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                    startTimes = new ConcurrentHashMap<>();
                }

    * 覆盖 shutdown() 方法。输出执行任务的信息后调用父类的 shutdown() 方法。

                @Override
                public void shutdown() {
                    System.out.printf("MyExecutor: Going to shutdown.\n");
                    System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
                    System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
                    System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
                    super.shutdown();
                }

    * 覆盖 shutdownNow() 方法。输出执行任务的信息后调用父类的 shutdownNow() 方法。

                @Override
                public List<Runnable> shutdownNow() {
                    System.out.printf("MyExecutor: Going to immediately shutdown.\n");
                    System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
                    System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
                    System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
                    return super.shutdownNow();
                }

    * 覆盖 beforeExecute() 方法。
        1. 输出将执行任务的线程名称和hash值。
        2. 用任务的hash值作为key，保存启动时间到 HashMap

                @Override
                protected void beforeExecute(Thread t, Runnable r) {
                    System.out.printf("MyExecutor: A task is beginning: %s : %s\n", t.getName(), r.hashCode());
                    startTimes.put(String.valueOf(r.hashCode()), new Date());
                }

    * 覆盖 afterExecute() 方法
        1. 输出任务的结果信息
        2. 计算任务的执行时间（将当前时间减去保存在HashMap中的线程的启动时间）

                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    Future<?> result = (Future<?>) r;
                    try {
                        System.out.printf("*********************************\n");
                        System.out.printf("MyExecutor: A task is finishing.\n");
                        System.out.printf("MyExecutor: Result: %s\n", result.get());
                        Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
                        Date finishDate = new Date();
                        long diff = finishDate.getTime() - startDate.getTime();
                        System.out.printf("MyExecutor: Duration: %d\n", diff);
                        System.out.printf("*********************************\n");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }


* Callable<String> 类 : SleepTwoSecondsTask

    * call() 方法： 睡上 2 秒后将当前时间转成字符串返回

                @Override
                public String call() throws Exception {
                    TimeUnit.SECONDS.sleep(2);
                    return new Date().toString();
                }


* 控制类 : Main

    1. 创建一个 MyExecutor 对象名为 myExecutor （注意线程池大小为2，最大到4）
    2. 创建 10 个 SleepTwoSecondsTask 并提交到 myExecutor
    3. 输出前 5 个线程的结果
    4. shutdown myExecutor
    5. 输出后 5 个线程的结果
    6. 等待所有线程结束后输出结束信息


                    MyExecutor myExecutor = new MyExecutor(
                            2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>()
                    );

                    List<Future<String>> results = new ArrayList<>();

                    for (int i = 0; i < 10; i++) {
                        SleepTwoSecondsTask task = new SleepTwoSecondsTask();
                        Future<String> result = myExecutor.submit(task);
                        results.add(result);
                    }

                    for (int i = 0; i < 5; i++) {
                        try {
                            String result = results.get(i).get();
                            System.out.printf("Main: Result for Task %d : %s\n", i, result);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    myExecutor.shutdown();

                    for (int i = 5; i < 10; i++) {
                        try {
                            String result = results.get(i).get();
                            System.out.printf("Main: Result for Task %d : %s\n", i, result);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        myExecutor.awaitTermination(1, TimeUnit.DAYS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.printf("Main: End of the program.\n");


### 讲解

本节中扩展了 ThreadPoolExecutor 类实现了定制执行者并覆盖了4个方法。

* beforeExecute() 和 afterExecute() 方法用来计算任务的执行时间。
    * beforeExecute() 方法在任务执行前执行，我们用了 HashMap 来保存任务的开始时间
    * afterExecute() 方法在任务执行后执行。从HashMap中获得开始时间，计算当前实际时间和开始时间的差来得到任务的执行时长。
* shutdown() 和 shutdownNow() 方法：输出在执行者中运行的任务的统计信息
    * getCompletedTaskCount() ：已执行完的任务
    * getActiveCount() ：此时在运行中的任务数
    * getQueue().size() : 在等待中的任务数。executor保存等待任务的阻塞中队列的大小。

实现了 Callable 接口的 SleepTwoSecondsTask 类让执行线程睡上2秒；
在 Main 类中，把 10 个任务送到了执行者并用执行者和其他类演示了它们的特性。

