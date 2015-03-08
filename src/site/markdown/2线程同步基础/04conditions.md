在同步代码中使用条件
====

并发编程中的一个经典问题是 生产者-消费者 问题。
有一个数据缓冲区，一个或多个数据生产者将数据保存到缓冲区，一个或多个数据消费者将数据从缓冲区取走。

因为缓冲区是个共享的数据结构，必须使用一种例如 synchronized 关键字的同步机制对它进行控制，只是我们有更多限制：

* 生产者不能在缓冲区满时保存数据
* 消费者也不能在缓冲区空时获得数据

为此场景，Java 在 Object 类中提供了 wait()、notify() 和 notifyAll() 方法。

* 线程可在同步代码块内调用 wait() 方法。
    <br/>
    如果在同步代码块外调用，JVM 会抛出 IllegalMonitorStateException 异常。
* 当线程调用 wait() 方法，JVM 让线程睡眠并释放控制对象（控制着线程正运行的同步代码块的对象），并允许其他线程执行由该控制对象保护的其他同步代码块。
* 必须在由同一个控制对象保护的代码块中调用 notify() 或 notifyAll() 方法来唤醒睡眠线程


### 任务

使用 synchronized 关键字和 wait()、notify()、notifyAll()方法演示生产者-消费者问题。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt2.sect04 package中*


* 数据类 (EventStorage)

    * 属性 maxSize : int 类型
    * 属性 storage : LinkedList\<Date\>

        private int maxSize;
        private List<Date> storage;

    * 在构造函数中设定 storage 的最大大小为 10

        public EventStorage() {
            maxSize = 10;
            storage = new LinkedList<Date>();
        }

    * 并发方法 set() : 存储事件到 storage
        <br/>
        1. 检查 storage 是否满了
            * 如果满了, 则调用 wait() 方法直到 storage 中有空间
        2. 存储事件到 storage
        3. 调用 notifyAll() 方法唤醒所有在等待中的线程

            public synchronized void set() {
                while (storage.size() == maxSize) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ((LinkedList<Date>)storage).offer(new Date());
                System.out.printf("Set: %d\n", storage.size());
                notifyAll();
            }

    * 并发方法 get() : 从 storage 中获取事件
        <br/>
        1. 检查 storage 中是否有事件
            * 如果没有事件，则调用 wait() 方法直到 storage 中有数据
        2. 从 storage 中取数据
        3. 调用 notifyAll() 方法唤醒所有在等待中的线程

            public synchronized void get() {
                while (storage.size() == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.printf("Get: %d: %s\n", storage.size(), ((LinkedList<?>) storage).poll());
                notifyAll();
            }

* 生产者线程 : Producer

    * EventStorage 类型属性 storage 及构造函数

            private EventStorage storage;

            public Producer(EventStorage storage) {
                this.storage = storage;
            }

    * run 方法 : 调用 100 次 EventStorage 对象的 set 方法

                for (int i = 0; i < 100; i++) {
                    storage.set();
                }


* 消费者线程 : Consumer

    * EventStorage 类型属性 storage 及构造函数

            private EventStorage storage;

            public Consumer(EventStorage storage) {
                this.storage = storage;
            }

    * run 方法 : 调用 100 次 EventStorage 对象的 get 方法

                for (int i = 0; i < 100; i++) {
                    storage.get();
                }


* 控制类 : Main

    * 创建一个 EventStorage 对象

            EventStorage storage = new EventStorage();

    * 分别创建一个 Producer 和 Consumer 对象及对应的线程

            Producer producer = new Producer(storage);
            Thread thread1 = new Thread(producer);

            Consumer consumer = new Consumer(storage);
            Thread thread2 = new Thread(consumer);

    * 启动这 2 个线程

            thread2.start();
            thread1.start();


### 工作原理

本例的要点是 EventStorage 类的 set() 和 get() 方法。

set() 方法检查storage属性中是否有空间。如果已满，则调用 wait() 方法等待空间。
当其他线程调用 notifyAll() 方法，此线程醒来再次检查相关条件。notifyAll() 方法不保证一定会唤醒某线程。
此过程一直重复直到 storage 里有空间，能生成新的事件到 storage。

get()方法的行为也类似。首先它检查storage里是否有事件。如果为空，它将调用 wait() 方法等待事件。
当其他线程调用 notifyAll() 方法，此线程则醒来再次检查相关条件，直到 storage 里有事件为止。

*您必须在一个while循环中持续检查相关条件并调用wait()方法。条件不满足时不能继续执行后续操作。*

如果您运行本例，您将看到无论生产者和消费者怎么设置、获取事件，storage里不会超过10个事件。


### 了解更多

synchronize 关键字还有其他重要的用途，且待后续分解。
