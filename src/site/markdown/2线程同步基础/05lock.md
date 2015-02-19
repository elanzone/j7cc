用锁同步代码块
====

Java 提供了另一个同步代码块的机制。比 synchronized 更强大、灵活。它是基于 Lock 接口及其实现类（如 ReentrantLock）。
此机制有以下优点：

* 以更灵活的方式组织同步块的结构
    <br/>
    使用 synchronized 关键字，您必须以结构严谨的方式获得、释放同步块的控制。
    使用Lock接口可以更复杂的结构来实现临界区。
* Lock接口提供更多的功能
    <br/>
    如:
    * tryLock() 方法 : 尝试获取lock的控制，如果不能（因为被其他线程占用）则返回lock。
        * 使用 synchronized 关键字时：当线程(A)尝试执行一段同步代码块，如果有另一个线程(B)在执行，则线程(A)被挂起直到线程(B)结束同步块的执行。
        * 使用 lock 时: 可使用 tryLock() 方法，此方法将返回一个 Boolean 值表示是否有另一个线程在运行由此锁保护的的代码
* Lock接口可以分离读、写操作，这样可以有多个读者和唯一一个写者
* Lock接口的性能比 synchronized 关键字性能更好


### 任务

使用 Lock 接口和 ReentrantLock 类创建一个临界区，模拟一个打印队列。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt2.sect05 package中*


* 打印队列 (PrintQueue)

    * 声明一个 Lock 对象并初始化为 ReentrantLock 类的对象

            private final Lock queueLock = new ReentrantLock();

    * printJob() 方法
        1. 调用 Lock 对象的 lock() 方法获得对锁的控制
        2. 随机睡一段时间以模拟打印文档
        3. 调用 Lock 对象的 unlock() 方法释放对锁的控制

            public void printJob(Object document) {
                queueLock.lock();

                try {
                    Long duration = (long) (Math.random() * 10000);
                    System.out.println(Thread.currentThread().getName() + ": PrintQueue: Printing a Job during " + (duration / 1000) + " seconds");
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    queueLock.unlock();
                }
            }

* 线程类 (Job)

    * 声明一个 PrintQueue 类的对象并在构造函数中初始化它

            private PrintQueue printQueue;

            public Job(PrintQueue printQueue) {
                this.printQueue = printQueue;
            }

    * run方法 : 使用 PrintQueue 对象并打印些什么东西

                System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
                printQueue.printJob(new Object());
                System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());

* 控制类 (Main)

    * 创建一个共享的 PrintQueue 对象

            PrintQueue printQueue = new PrintQueue();

    * 创建多个 Job 对象及对应的线程

            Thread thread[] = new Thread[10];
            for (int i=0; i<10; i++) {
                thread[i] = new Thread(new Job(printQueue), "Thread" + i);
            }

    * 启动这10个线程

            for (int i=0; i<10; i++) {
                thread[i].start();
            }


### 工作原理

本例的关键在于 PrintQueue 类的 printJob() 方法。
当要用锁实现一个临界区并保证只有一个执行线程运行一个代码块，必须创建一个 ReentrantLock 对象。

1. 在临界区开始，要用 lock() 方法获得对锁的控制。
    <br/>
    当线程(A)调用此方法:
    * 如果没有其他线程拥有对此锁的控制，此方法赋予线程(A)对此锁的控制并立即返回以允许线程(A)的临界区的执行
    * 如果有另一个线程(B)在执行此锁控制的临界区，lock()方法让线程(A)睡眠直到线程(b)结束在临界区的执行

2. 在临界区结束，必须使用 unlock() 方法释放对锁的控制并允许其他线程运行此临界区。
    * 如果不这样，其他在等待的线程将永久等待，导致死锁。
    * 如果在临界区内使用 try-catch ，别忘了将包含 unlock 的方法放在 finally 段内。


### 了解更多

Lock 接口（和 ReentrantLock 类）包含其他的方法来获取对锁的控制。那就是 tryLock() 方法。
tryLock 和 lock 的最大的区别在于：
    * 如果使用 tryLock 的线程不能获得对锁的控制，将立即返回并不会让线程睡眠。

tryLock 方法返回一个 boolean 值:
    * true : 线程获得对锁的控制
    * false : 不能获得

程序员有责任判断 tryLock 方法的返回结果并作出相应处理。如果它返回 false，程序不应执行临界区代码；否则将获得错误的结果。

ReentrantLock 类也允许用于递归调用。当一个线程拥有对锁的控制并递归调用，它继续拥有对锁的控制。这样对 lock() 方法的调用将立即返回，程序也将继续递归执行。
也能调用其他方法。


### 再多说一点

使用 Lock 时必须非常小心以避免死锁。此场景发生在2个或更多线程被阻塞等待永远不会被释放的锁。
例如：线程(A)锁定了一个锁(X)，线程(B)锁定了锁(Y)。如果此时线程(A)尝试锁定锁(Y)并且线程(B)同时尝试锁定锁(X)，两个线程都会被永久阻塞。因为它们在等待将永不释放的锁。
注意这个问题发生在2个线程尝试以相反的顺序获得锁。附录给出了一些适当地设计并发应用并避免这些死锁问题的好提示。


