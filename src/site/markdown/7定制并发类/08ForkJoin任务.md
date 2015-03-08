定制在 Fork/Join 框架中运行的任务
====

请回顾 [第五章介绍](../5forkjoin框架/01介绍.html) 及 [上一节介绍](07ForkJoin线程.html)

默认情况下，被一个 ForkJoinPool 类执行的任务是 ForkJoinTask 类对象。
您也可以提交 Runnable 和 Callable 对象到一个 ForkJoinPool 类，但这样享受不到任何 Fork/Join 框架的好处。
一般提交到 ForkJoinPool 的是 ForkJoinTask 类的2个子类：

* RecursiveAction: 如果任务不返回结果
* RecursiveTask: 如果任务有返回结果

在本节中，将学习如何通过实现一个扩展 ForkJoinTask 类的任务为 Fork/Join 框架实现您自己的任务。
将实现的任务测算并输出它的执行时间到终端，这样您能控制它的进展。
您也能实现您自己的 Fork/Join 任务来写日志、在任务中获取资源或对结果进行后处理。

### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt7.sect08 package中*


* 定制工作任务类: MyWorkerTask : extends ForkJoinTask\<Void\>
    <br/>
    无返回，故泛型参数为 Void

    * 属性 name : String 对象 : 任务名称
    * 构造函数初始化属性值为参数值

                private String name;

                public MyWorkerTask(String name) {
                    this.name = name;
                }

    * 覆盖 getRawResult() 方法：因为返回类型是Void，返回 null 即可
    * 覆盖 setRawResult() 方法：因为不返回结果，故留空即可

                @Override
                public Void getRawResult() {
                    return null;
                }

                @Override
                protected void setRawResult(Void value) {
                }

    * 覆盖 exec() 方法 ：这是任务的主方法。本例中，将任务逻辑转到 compute() 方法。计算方法的执行时间并输出到终端。

                @Override
                protected boolean exec() {
                    Date startDate = new Date();

                    compute();

                    Date finishDate = new Date();
                    long diff = finishDate.getTime() - startDate.getTime();
                    System.out.printf("MyWorkerTask: %s : %d Milliseconds to complete.\n", name, diff);

                    return true;
                }

    * getName() 方法: name 属性的 getter 方法

                public String getName() {
                    return name;
                }

    * 声明抽象方法 compute()：此方法将实现任务的逻辑，且必须由 MyWorkerTask 类的子类实现

                protected abstract void compute();

* 任务类 : Task : extends MyWorkerTask

    * 属性：int数组（模拟将要处理的数据数组）array 及起始值 start、结束值 end
    * 构造函数将各属性初始化为参数值

                private int array[];
                private int start, end;

                public Task(int[] array, int start, int end) {
                    this.array = array;
                    this.end = end;
                    this.start = start;
                }

    * compute() 方法 : 增加由 start 和 end 属性决定的区间内的元素值
        1. 如果计算区间内的元素数量大于 100，则:
            1. 均分为两个区间并创建两个 Task 对象各处理一个区间
            2. 调用 invokeAll() 方法提交到Fork/Join线程池
        2. 如果计算区间数量小于100，则遍历区间内每个元素，给每个元素值加1；
        3. 睡上 50 毫秒

                @Override
                protected void compute() {
                    if ((end - start) > 100) {
                        int mid = (end + start) / 2;
                        Task task1 = new Task(this.getName() + "1", array, start, mid);
                        Task task2 = new Task(this.getName() + "2", array, mid, end);
                        invokeAll(task1, task2);
                    } else {
                        for (int i = start; i < end; i++) {
                            array[i]++;
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


* 控制类 : Main
    1. 创建一个有 10000 个元素的整型数组
    2. 创建一个 ForkJoinThreadPool 对象 pool
    3. 创建一个 Task 对象处理之前创建的整个数组
    4. 使用 ForkJoinThreadPool 对象的 invoke() 方法，提交任务到池
    5. 调用 shutdown() 方法后停止线程池

                public static void main(String[] args) throws Exception {
                    int array[] = new int[10000];
                    ForkJoinPool pool = new ForkJoinPool();
                    Task task = new Task("Task", array, 0, array.length);
                    pool.invoke(task);
                    pool.shutdown();
                    System.out.printf("Main: End of the program.\n");
                }


### 讲解

在本节中，实现了扩展 ForkJoinTask 类的 MyWorkerTask 类。
它是实现能被放在 ForkJoinPool 执行者中执行、并能有 ForkJoinPool 执行者的各样好处（如工作窃取算法）的任务的基类。
此类相当于 RecursiveAction 和 RecursiveTask 类。

扩展 ForkJoinTask 类必须实现以下3个方法：

* setRawResult(): 用于设置任务的结果。（本例中因为任务不返回结果，所以此方法为空）
* getRawResult(): 用于返回任务结果。（本例中因为任务不返回结果，所有此方法返回 null）
* exec(): 此方法实现任务的逻辑。<br/>
    在本例中，委托给抽象方法 compute() （如 RecursiveAction 和 RecursiveTask 类一样）
    并在 exec() 方法中度量方法的执行时间输出到终端

最后，在例子的主类中，创建了一个有 10000 个元素的数组、一个 ForkJoinPool 执行者 和 一个处理整个数组的 Task 对象。
运行此程序，您将看到被执行的不同的任务是如何输出运行时间到终端的。


