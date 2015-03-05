实现 ThreadFactory 接口为Fork/Join框架生成定制线程
====

Fork/Join 框架是 Executor 和 ExecutorService 接口的一个实现，
这些接口让您可以执行 Callable 和 Runnable 任务而不用管理执行它们的线程。

Fork/Join 框架的目标是执行可以分成较小部分的任务。它的主要成分如下：

* 一个特定种类的任务：由 ForkJoinTask 类实现
* 两个操作
    * 将一个任务分成子任务（fork操作）
    * 等待那些子任务的结束（join操作）
* 一个算法：工作窃取算法。优化线程池的线程的使用。
    当一个任务在等待子任务时，执行它的线程被用于执行另一个线程。


Fork/Join 框架的主类是 ForkJoinPool 类。在内部，它有以下两个元素：

* 等待被执行的任务队列
* 执行任务的一池线程

在本节中，您将学习如何实现一个定制的工作线程用于一个 ForkJoinPool 类以及如何用一个工厂来使用它。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt7.sect07 package中*


* 工作线程类：MyWorkerThread ：extends ForkJoinWorkerThread

    * 静态属性 taskCounter ：ThreadLocal<Integer> 对象 ：用来统计线程执行的任务数
    * 构造函数

                private static ThreadLocal<Integer> taskCounter = new ThreadLocal<Integer>();

                protected MyWorkerThread(ForkJoinPool pool) {
                    super(pool);
                }

    * 覆盖 onStart() 方法：
        * 线程启动时将调用此方法
        * 在此方法中调用父类的 onStart() 方法后设置 taskCounter 值为0

                @Override
                protected void onStart() {
                    super.onStart();

                    System.out.printf("MyWorkerThread %d: Initializing task counter.\n", getId());
                    taskCounter.set(0);
                }

    * 覆盖 onTermination() 方法
        * 线程结束时将调用此方法
        * 在此方法中输出 taskCounter 值后调用父类的 onTermination() 方法

                @Override
                protected void onTermination(Throwable exception) {
                    System.out.printf("MyWorkerThread %d: %d\n", getId(), taskCounter.get());
                    super.onTermination(exception);
                }

    * addTask() 方法：taskCounter 计数器加1

                public void addTask() {
                    int counter = taskCounter.get().intValue();
                    counter++;
                    taskCounter.set(counter);
                }

* 工作线程工厂类 : MyWorkerThreadFactory : implements ForkJoinPool.ForkJoinWorkerThreadFactory

    * 覆盖 newThread() 方法：返回 MyWorkerThread 对象

                @Override
                public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                    return new MyWorkerThread(pool);
                }

* Recursive 任务类 : MyRecursiveTask : extends RecursiveTask<Integer>

    * 属性：int数组（模拟将要处理的数据数组）array 及起始值 start、结束值 end
    * 构造函数将各属性初始化为参数值

                private int array[];
                private int start, end;

                public MyRecursiveTask(int[] array, int start, int end) {
                    this.array = array;
                    this.end = end;
                    this.start = start;
                }

    * compute() 方法
        1. 获取当前线程，强制类型转换为 MyWorkerThread 后调用 addTask() 方法给线程本地任务计数器值加1
        2. 如果计算区间数量小于100，则遍历区间内每个元素求和；
           否则均分区间，拆成2个子任务后，调用 addResults() 方法将子任务的结果求和
        3. 返回结果

                @Override
                protected Integer compute() {
                    MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
                    thread.addTask();

                    if ((end - start) < 100) {
                        int ret = 0;
                        for (int i = start; i < end; i++) {
                            ret += array[i];
                        }
                        return ret;
                    } else {
                        int mid = (end + start) / 2;
                        MyRecursiveTask task1 = new MyRecursiveTask(array, start, mid);
                        MyRecursiveTask task2 = new MyRecursiveTask(array, mid, end);
                        invokeAll(task1, task2);
                        return addResults(task1, task2);
                    }
                }

    * addResults() 方法：调用 RecursiveTask 的 get() 方法活动任务的结果并求和后，睡上 10 毫秒

                private Integer addResults(MyRecursiveTask task1, MyRecursiveTask task2) {
                    int value;
                    try {
                        value = task1.get() + task2.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        value = 0;
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return value;
                }


* 控制类 : Main

    1. 创建一个 MyWorkerThreadFactory 对象 factory
    2. 创建一个 ForkJoinPool 对象 pool, 线程数为4，使用 MyWorkerThreadFactory 对象创建工作线程
    3. 创建待处理的数组，大小为 100000，每个元素的值为1
    4. 创建一个 MyRecursiveTask 任务处理整个数组并调用 execute() 方法提交到 ForkJoinPool 执行
    5. 等待任务执行完后，停止线程池，等待线程池中现有任务结束
    6. 输出任务的处理结果

                public static void main(String[] args) throws Exception {
                    MyWorkerThreadFactory factory = new MyWorkerThreadFactory();
                    ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);

                    int array[] = new int[100000];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = 1;
                    }

                    MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length);

                    pool.execute(task);

                    task.join();

                    pool.shutdown();
                    pool.awaitTermination(1, TimeUnit.DAYS);
                    System.out.printf("Main: Result: %d\n", task.get());
                    System.out.printf("Main: End of the program\n");
                }


### 讲解

Fork/Join 框架使用的线程被称为工作线程。
Java 包括 ForkJoinWorkerThread 类，该类扩展 Thread 类并实现了供 Fork/Join 框架使用的工作线程。

在本节中：

* MyWorkerThread类 <br/>
    实现了扩展 ForkJoinWorkerThread 类的 MyWorkerThread 类并覆盖了 ForkJoinWorkerThread 类的2个方法。
    * 目标是在每个工作线程内实现一个任务计数器，这样可以指定一个工作线程执行了多少个任务。
    * 计数器用一个 ThreadLocal 属性来实现。这样，每个线程将有其自己的计数器，对程序员是透明的。

    * 覆盖了 ForkJoinWorkerThread 类的 onStart() 方法来初始化任务计数器
        * 此方法在工作线程开始执行时被调用

    * 覆盖了 onTermination() 方法把任务计数器的值输出到终端
        * 此方法在工作线程结束执行时被调用

    * 在 MyWorkerThread 类中实现了一个方法 addTask()，此方法增长每个线程的任务计数器


* MyWorkerThreadFactory 类<br/>
ForkJoinPool 类如 Java Concurrency API 中的所有执行者，用一个工厂创建它的线程，
所以如果要在一个 ForkJoinPool 类中使用 MyWorkerThread 线程，必须实现自己的线程工厂。
对于 Fork/Join 框架，此工厂必须实现 ForkJoinPool.ForkJoinWorkerThreadFactory 类。
为此，实现了 MyWorkerThreadFactory 类。此类只有一个创建一个新 MyWorkerThread 对象的方法

* 最后只需要用已创建的工厂初始化一个 ForkJoinPool 类。
本例中在 Main 类中使用 ForkJoinPool 类的构造函数做了这件事。


### 了解更多

留意 ForkJoinWorkerThread 类提供的 onTermination() 方法在线程正常结束或抛出 Exception 异常时都会被调用。
此方法接受一个 Throwable 对象作为一个参数。

* 如果此参数值为 null，表示工作线程正常结束；
* 如果参数值不为 null，表示线程抛出一个异常。要有必要的代码来处理此情况。




