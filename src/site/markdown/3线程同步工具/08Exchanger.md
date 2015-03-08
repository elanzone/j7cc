在并发任务间交换数据
====

Java concurrency API 提供了一个同步工具可在2个并发任务间交换数据。Exchanger 类允许在 2 个线程间定义一个同步点。
当2个线程到达此点，他们互换一个数据结构这样第一个线程的数据结构转到第二个，第二个的转到第一个。

在类似生产者-消费者问题的情况下，此类可能非常有用。
因为 Exchanger 类只能同步2个线程，如果您的生产者-消费者问题有一个生产者和一个消费者，您可以用它。


### 任务

使用 Exchanger 解决有一个生产者和一个消费者的生产者-消费者问题。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt3.sect08 package中*


* 生产者线程 : Producer

    * 属性 buffer : List\<String\> 对象, 将被交换的数据结构
    * 属性 exchanger : final Exchanger\<List\<String\>\> 对象, 用于同步生产者和消费者的交换对象
    * 在构造函数中初始化上述2个属性

            private List<String> buffer;
            private final Exchanger<List<String>> exchanger;

            public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
                this.buffer = buffer;
                this.exchanger = exchanger;
            }

    * run 方法 : 实现 10 次交换循环, 在每次循环中:
        1. 加 10 个字符串到 buffer
        2. 调用 exchanger 的 exchange 方法与消费者交换数据 (需要捕捉 InterruptedException 异常)

                int cycle = 1;
                for (int i = 0; i < 10; i++) {
                    System.out.printf("Producer: Cycle %d\n", cycle);
                    for (int j = 0; j < 10; j++) {
                        String message = "Event " + ((i * 10) + j);
                        System.out.printf("Producer: %s\n", message);
                        buffer.add(message);
                    }
                    try {
                        buffer = exchanger.exchange(buffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Producer: " + buffer.size());
                    cycle++;
                }

* 消费者线程 : Consumer

    * 属性与构造函数同 Producer

            private List<String> buffer;
            private final Exchanger<List<String>> exchanger;

            public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
                this.buffer = buffer;
                this.exchanger = exchanger;
            }

    * run方法 : 实现 10 次交换循环, 在每次循环中:
        1. 调用 exchanger 的 exchange 方法与 producer 同步数据 (需要捕捉 InterruptedException 异常)
        2. 将 producer 送过来的 10 个字符串输出到终端, 并从 buffer 中清空

                int cycle = 1;
                for (int i = 0; i < 10; i++) {
                    System.out.printf("Consumer: Cycle %d\n", cycle);
                    try {
                        buffer = exchanger.exchange(buffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Consumer: " + buffer.size());
                    for (int j = 0; j < 10; j++) {
                        String message = buffer.get(0);
                        System.out.println("Consumer: " + message);
                        buffer.remove(0);
                    }
                    cycle++;
                }

* 控制类 : Main

    * 创建2个 buffer 由生产者、消费者使用

            List<String> buffer1=new ArrayList<>();
            List<String> buffer2=new ArrayList<>();

    * 创建 Exchanger 对象以同步生产者和消费者

            Exchanger<List<String>> exchanger=new Exchanger<>();

    * 创建 Producer 和 Consumer 对象及对应的线程，并启动线程

            Producer producer=new Producer(buffer1, exchanger);
            Consumer consumer=new Consumer(buffer2, exchanger);

            Thread threadProducer=new Thread(producer);
            Thread threadConsumer=new Thread(consumer);

            threadProducer.start();
            threadConsumer.start();


### 工作原理

消费者以空 buffer 开始，调用 Exchanger 来和生产者同步。它需要消耗数据。
生产者开始执行时 buffer 为空；它创建 10 个字符串，存入 buffer，然后用 exchanger 来和消费者同步。

此时，两个线程（生产者和消费者）在 Exchanger 内，Exchanger 改变了数据结构，这样：

* 当消费者从 exchange() 方法返回，将有带10个字符串的 buffer
* 当生产者从 exchange() 方法返回，将有一个空 buffer 供再次填充

此操作将重复 10 次

当您执行此例时，您将看到生产者和消费者是如何并发完成他们的工作，2个对象是如何在每步交换它们的 buffer 的。
第一个调用 exchange() 方法的线程将进入睡眠直到另一个线程来到。


### 了解更多

Exchanger 类有另一个版本的 exchange 方法:

* exchange(V data, long time, TimeUnit unit)
    * V : 在 Exchanger 声明内用作一个参数的类型（本例是 List\<String\>）
    * 线程将睡眠，直到:
        * 被中断
        * 另一个线程到达
        * 过了指定的时间 ( time 是数值, unit 是单位. 如 3 秒 (time = 3, unit = TimeUnit.SECONDS))


