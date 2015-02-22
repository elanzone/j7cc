在一个共同点中同步任务 (CyclicBarrier)
====

可使用 CyclicBarrier 类在一个已确定的点内同步2或更多线程。它比较像 CountDownLatch 类，但某些区别使它更强大。

CyclicBarrier 类用一个整数初始化，此整数是在一个确定的点中将被同步的线程数目。
当其中一个线程到达此点时，它调用 await() 方法等待其他线程。CyclicBarrier 类将阻塞催眠此线程直到其他线程到达。
当最后一个线程调用 await() 方法，将唤醒所有在等待中的线程继续执行。

CyclicBarrier 类的一个有趣的优点是您可多传递一个 Runnable 对象作为初始化的参数，
当所有线程到达共同点，CyclicBarrier类作为一个线程来执行此对象。
此特点使得此类能胜任使用分治编程技术的任务的并行。


### 任务

在一个数字矩阵中查找一个数字。此矩阵将被分成子集（用分治计数），每个线程将各自在一个子集中搜索。
当所有线程完成任务，最后有一个将汇总它们的结果。


### 实现

* 待查找矩阵 : MatrixMock

    * 声明属性 name : int 矩阵
    * 在构造函数中以随机数初始化矩阵并输出预期的查找结果。<br/>
        参数为矩阵的行数、列数和待查找的数字

            private int data[][];

            public MatrixMock(int size, int length, int number) {
                int counter = 0;
                data = new int[size][length];
                Random random = new Random();

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < length; j++) {
                        data[i][j] = random.nextInt(10);
                        if (data[i][j] == number) {
                            counter++;
                        }
                    }
                }

                System.out.printf("Mock: There are %d ocurrences of number %d in generated data.\n", counter, number);
            }

    * getRow() 方法 : 如果存在，则返回指定的行数据；不存在则返回 null

            public int[] getRow(int row) {
                if ((row >= 0) && (row < data.length)) {
                    return data[row];
                }
                return null;
            }

* 查找结果类 : Results

    * 属性 data : int 数组，保存在矩阵每行中被搜索的数组出现的次数
    * 在构造函数中将 data 初始化为指定的行数

            private int data[];

            public Results(int size) {
                data = new int[size];
            }

    * setData() 方法 : 设置数组指定位置的值

            public void setData(int position, int value) {
                data[position] = value;
            }

    * getData() 方法 : 返回 data

            public int[] getData() {
                return data;
            }

* 搜索线程类 : Searcher (在随机数矩阵的某些行中搜索某数字)

    * 属性 firstRow, lastRow : int 类型, 在随机数矩阵的 firstRow 开始, lastRow 结束的行中搜索
    * 属性 mock : MatrixMock 对象
    * 属性 results : Results 对象，搜索结果
    * 属性 number : int 类型，待搜索的数字
    * 属性 barrier : final CyclicBarrier 对象
    * 在构造函数中初始化上述属性

            private int firstRow;
            private int lastRow;

            private MatrixMock mock;
            private Results results;

            private int number;

            private final CyclicBarrier barrier;

            public Searcher(int firstRow, int lastRow,
                            MatrixMock mock, Results results,
                            int number, CyclicBarrier barrier) {
                this.firstRow = firstRow;
                this.lastRow = lastRow;
                this.mock = mock;
                this.results = results;
                this.number = number;
                this.barrier = barrier;
            }

    * run 方法
        * 处理分配给线程的各行。对于每一行，把数字出现的次数存储到 results 的对应位置
        * 处理完后调用 CyclicBarrier 对象的 await() 方法，需处理 InterruptedException 和 BrokenBarrierException 异常

                int counter;
                System.out.printf("%s: Processing lines from %d to %d.\n",
                        Thread.currentThread().getName(), firstRow, lastRow);

                for (int i = firstRow; i < lastRow; i++) {
                    int row[] = mock.getRow(i);
                    counter = 0;
                    for (int aRow : row) {
                        if (aRow == number) {
                            counter++;
                        }
                    }
                    results.setData(i, counter);
                }
                System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());

                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

* 结果汇总线程 : Grouper

    * 属性 results : Results 对象，在该对象中保存了矩阵各行中待搜索数字的出现次数
    * 构造函数设置 results 的值为指定值

            private Results results;

            public Grouper(Results results) {
                this.results = results;
            }

    * run方法 : 遍历 results 数组求和

                int finalResult = 0;
                System.out.printf("Grouper: Processing results...\n");

                int data[] = results.getData();
                for (int number : data) {
                    finalResult += number;
                }

                System.out.printf("Grouper: Total result: %d.\n", finalResult);

* 控制类 : Main

    * 创建一个 10000 行 1000 列、待搜索数字为 5 的 MatrixMock 矩阵 mock，及对应 10000 行数据的结果数组 results
    * 创建一个 Grouper 对象用于汇总统计数据
    * 计划将整个计算分配给5个线程处理，故创建一个 CyclicBarrier 对象 barrier，在构造函数中设定等待 5 个线程、等齐后启动汇总线程
    * 创建 5 个 Searcher 对象(每个对象处理 2000 行数据)及对应执行的线程，并启动线程

                final int ROWS = 10000;
                final int NUMBERS = 1000;
                final int SEARCH = 5;
                final int PARTICIPANTS = 5;
                final int LINES_PARTICIPANT = 2000;

                MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);
                Results results = new Results(ROWS);
                Grouper grouper = new Grouper(results);

                CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);
                Searcher searchers[] = new Searcher[PARTICIPANTS];
                for (int i = 0; i < PARTICIPANTS; i++) {
                    int firstRow = i * LINES_PARTICIPANT;
                    int lastRow = firstRow + LINES_PARTICIPANT;
                    searchers[i] = new Searcher(firstRow, lastRow, mock, results, 5, barrier);
                    Thread thread = new Thread(searchers[i]);
                    thread.start();
                }
                System.out.printf("Main: The main thread has finished.\n");


### 工作原理

在本例中，希望知道一个随机数大矩阵中某数字出现的总次数。
为了有更好的性能，使用了分治技术，将矩阵分为5个子集，一个线程在一个子集中查找。这些线程是 Searcher 对象。

本例中使用了一个 CyclicBarrier 对象来同步 5 个线程的完成并执行 Grouper 任务来汇总各部分的结果得到最终结果。

CyclicBarrier 类有一个内部计数器来控制有多少个线程必须到达此同步点。
每当一个线程到达同步点，它调用 await() 方法告知 CyclicBarrier 对象；CyclicBarrier 将此线程催眠直到所有线程都到达同步点。

当所有线程都到达同步点，CyclicBarrier 对象唤醒所有在 await() 方法中等待的所有线程；
如果在构造函数传递了 Runnable 对象，则创建一个新线程执行以执行额外的任务。


### 了解更多

CyclicBarrier 有另一个版本的 await() 方法

* await(long time, TimeUnit unit) : 线程将睡眠直到:
    * 被中断
    * CyclicBarrier 的内部计数器到0
    * 过了指定的时间


#### 重置 CyclicBarrier 对象

CyclicBarrier 类和 CountDownLatch 类有一些共同点，也有一些区别。
其中最重要一点区别是 CyclicBarrier 对象能被重置为初始状态，设置内部计数器的值为初始值。

可用 CyclicBarrier 类的 reset() 方法重置。
当重置时，在 await() 方法中等待的所有线程都收到一个 BrokenBarrierException 异常。
在本例中处理此异常的方式是打印 stack trace，但是在一个更复杂的应用中，可执行某些其他的操作，例如重新开始执行或恢复在被中断时的操作。


#### 被打断的(broken) CyclicBarrier 对象

一个 CyclicBarrier 对象能处在一个表示被打断的特殊状态。
当有多个线程在 await() 方法中等待且其中一个被中断:

* 被中断的线程收到一个 InterruptedException 异常
* 其他等待中的线程收到一个 BrokenBarrierException 异常
* CyclicBarrier 处于 被打断(broken) 状态。

CyclicBarrier 类提供了 isBroken() 方法，如果此对象处于被打断状态，则返回 true；否则返回 false

