运行并发阶段性任务
====

使用 Phaser 类来执行并发阶段性任务的能力是Java concurrency API提供的最复杂、强大的功能之一。
此机制在当我们有一些并发任务被分成很多步时有用。Phaser 类使我们可以在每一步骤结束点同步各线程，
所有线程都完成第一步时，才会开始执行第二步。

和其他的同步工具一样，必须用参与同步操作的任务数来初始化 Phaser 类，但是能通过增减来动态修改此数字。


### 任务

在 3 个不同的文件夹及其子文件夹中查找后缀为 .log 、在最近 24 小时内有修改的文件的 3 个任务，被分成以下 3 步:

1. 在指定的文件夹及其子文件夹中获得后缀为 .log 的文件列表
2. 将在 24 小时前修改的文件从上一步获得的列表中过滤掉
3. 在终端中输出结果

在第1步和第2步结束时都检查一下看列表是否为空。如果为空，则结束线程并将此线程从 Phaser 类中去除。


### 实现

* 文件搜索线程类 : FileSearch

    * 属性 initPath : String 对象；在此文件夹及子文件夹中查找
    * 属性 end : String 对象; 待查找文件以 end 结尾
    * 属性 results : List\<String\> 对象; 文件查找的结果字符串列表, 结果为文件的全路径
    * 属性 phaser : Phaser 对象; 控制任务不同步骤的同步
    * 构造函数 : 初始化各属性, 参数有 initPath, end, phaser

            private String initPath;// 在此文件夹及子文件夹中查找
            private String end;     // 文件结束字符串(后缀)
            private List<String> results;

            private Phaser phaser;  // 控制任务不同步骤的同步

            public FileSearch(String initPath, String end, Phaser phaser) {
                this.initPath = initPath;
                this.end = end;
                this.phaser = phaser;
                results = new ArrayList<String>();
            }

    * directoryProcess() 方法 : 遍历目录下的所有文件:
        * 如果是目录类型, 则递归调用 directoryProcess;
        * 如果是文件类型, 则调用 fileProcess

            private void directoryProcess(File file) {
                File list[] = file.listFiles();
                if (list != null) {
                    for (File aList : list) {
                        if (aList.isDirectory()) {
                            directoryProcess(aList);
                        } else {
                            fileProcess(aList);
                        }
                    }
                }
            }

    * fileProcess(File) 方法 : 比较文件:
        * 如果是以 end 结尾, 则加入结果集;

            private void fileProcess(File file) {
                // 文件名以end结尾的
                if (file.getName().endsWith(end)) {
                    results.add(file.getAbsolutePath());
                }
            }

    * filterResults() 方法 : 遍历结果集, 将最后修改日期在一天前的文件从结果集中去除

            private void filterResults() {
                List<String> newResults = new ArrayList<>();
                long actualDate = new Date().getTime();
                for (String result : results) {
                    File file = new File(result);
                    long fileDate = file.lastModified();
                    // 最后修改时间在一天内的
                    if ((actualDate - fileDate) < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                        newResults.add(result);
                    }
                }
                results = newResults;
            }

    * checkResults() 方法 : 检查结果是否为空
        * 如果为空，则调用 Phaser 对象的 arriveAndDeregister 方法通知它已经完成当前步骤, 退出分步操作
        * 否则调用 Phaser 对象的 arriveAndAwaitAdvance 方法通知它已完成当前步骤，并希望被阻塞直到所有参与此分步操作的线程完成当前步骤

            private boolean checkResults() {
                if (results.isEmpty()) {
                    System.out.printf("%s: Phase %d: 0 results.\n",Thread.currentThread().getName(),phaser.getPhase());
                    System.out.printf("%s: Phase %d: End.\n",Thread.currentThread().getName(),phaser.getPhase());
                    phaser.arriveAndDeregister();   // 到达此检录点并退出比赛
                    return false;
                } else {
                    System.out.printf("%s: Phase %d: %d results.\n",Thread.currentThread().getName(),phaser.getPhase(),results.size());
                    phaser.arriveAndAwaitAdvance(); // 到达此检录点并等待其他参赛者以继续下一阶段比赛
                    return true;
                }
            }

    * showInfo() 方法 : 显示结果信息

            private void showInfo() {
                for (String result : results) {
                    File file = new File(result);
                    System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
                }
                phaser.arriveAndAwaitAdvance();     // 到达此检录点并等待其他参赛者以继续下一阶段比赛
            }

    * run 方法
        1. 调用 Phaser 对象的 arriveAndAwaitAdvance 方法。这样搜索操作在所有线程都被创建并运行了才会开始。
        2. 如果要检查的 initPath 是目录而不是文件，则调用 directoryProcess 方法开始第一次查找
        3. 调用 checkResults 方法检查结果 (这里会对所有线程做一次同步)
        4. 调用 filterResults 方法对第一次查找的结果进行过滤
        5. 调用 checkResults 方法检查结果 (这里会对所有线程做一次同步)
        6. 调用 showInfo 方法显示最终结果 (这里会对所有线程做一次同步)
        7. 调用 Phaser 对象的 arriveAndDeregister 方法告知已完成当步操作并退出分步操作


                phaser.arriveAndAwaitAdvance();
                System.out.printf("%s: Starting.\n",Thread.currentThread().getName());

                File file = new File(initPath);
                if (file.isDirectory()) {
                    directoryProcess(file);
                }

                if (!checkResults()){
                    return;
                }

                filterResults();

                if (!checkResults()){
                    return;
                }

                showInfo();
                phaser.arriveAndDeregister();   // 已到达终点，退出比赛
                System.out.printf("%s: Work completed.\n",Thread.currentThread().getName());

* 控制类 : Main

    * 创建一个有 3 个参与者的 Phaser 对象
    * 创建 3 个 FileSearch 对象，每个有不同的初始目录，查找以 .log 结尾的文件
    * 创建对应的线程对象并启动各线程
    * 等待 3 个线程结束

                Phaser phaser = new Phaser(3);
                FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
                FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
                FileSearch documents = new FileSearch("C:\\Documents And Settings", "log", phaser);

                Thread systemThread = new Thread(system, "System");
                systemThread.start();

                Thread appsThread = new Thread(apps, "Apps");
                appsThread.start();

                Thread documentsThread = new Thread(documents, "Documents");
                documentsThread.start();

                try {
                    systemThread.join();
                    appsThread.join();
                    documentsThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Terminated: " + phaser.isTerminated());


### 工作原理

程序开始创建一个将控制线程在每个阶段结束时的同步的 Phaser 对象。Phaser 的构造函数的参数有参与者数目（本例为3）。
此数字告知 Phaser 在改变阶段和唤醒睡眠中线程前必须执行一次 arriveAndAwaitAdvance() 方法的线程数目。

Phaser一被创建，我们就启动了3个执行3个不同的 FileSearch 对象的线程。

*本例使用了Windows操作系统的路径。如果您工作在其他操作系统，请根据您的环境修改路径*

FileSearch 对象的run方法中的第一条语句是一个对 Phaser 对象的 arriveAndAwaitAdvance() 方法的调用。
Phaser 知道我们想同步的线程数目。当一个线程调用此方法，Phaser减少必须结束当前阶段的线程数目并将此线程催眠直到所有剩余线程结束当前阶段。
在 run 方法的开始调用此方法，使得所有 FileSearch 线程都被创建了才开始工作。

在阶段1和阶段2, 检查此阶段是否生成结果和结果列表中是否有内容.

* 如果有内容, checkResults() 方法调用 arriveAndAwaitAdvance() 方法
* 如果无内容, 线程继续执行已没有意义, 所以返回. 但是必须告知 phaser 将少一个参与者. <br/>
    所以使用了 arriveAndDeregister. 它告知 phaser 此线程已经完成了此阶段的任务, 但是它不再参与后续阶段, 这样 phaser 不必等它以继续.

在 showInfo() 方法中实现的第3阶段的结尾, 有一个对 phaser 的 arriveAndAwaitAdvance() 方法的调用.
    通过此调用, 我们保证所有的线程在同一时间结束.
当此方法结束执行, 有一个对 phaser 的 arriveAndDeregister() 方法的调用.
    通过此调用, 我们注销了 phaser 的线程, 这样当所有线程结束, phaser 将没有参与者.

最后, main() 方法等待3个线程的完成, 并调用 phaser 的 isTerminated() 方法.
当 phaser 没有参与者, 它进入结束状态, isTerminated() 方法返回 true.
当我们注销了 phaser 的所有线程, 它将进入结束状态, 此调用将打印 true 到终端.

一个 Phaser 对象有 2 种状态:

* 活跃(Active) : 当 Phaser 接受新参与者要求在每阶段结束的同步的注册, Phaser 进入了此状态.
* 结束(Termination) : 当所有 Phaser 内的参与者都注销了, Phaser 没有参与者时, 进入此状态.
    此时 onAdvance() 方法返回 true. 如果您覆盖 onAdvance 方法，您可以改变默认行为．
    当 Phaser 处于此状态，同步方法 arriveAndAwaitAdvance() 立即返回不做任何同步操作．

Phaser 类的一个显著的特点是您不必控制 phaser 相关方法的异常．
不像其他同步工具，在一个 phaser 中睡眠的线程不会响应中断时间，不抛出 InterruptedException 异常．
只有一个异常，将在后面解释．


### 了解更多

Phaser 类提供了其他关于阶段变化的其他方法，如下:

* arrive(): 此方法告知 phaser 一个参与者已经完成了现阶段，但它继续执行不等待其余参与者。
            小心使用此方法，因为它不和其他线程同步。
* awaitAdvance(int phase): 在指定阶段等待
    * 如果参数传递的数字和phaser实际的阶段一致，则此方法将当前线程催眠直到所有参与者完成了当前阶段。
    * 如果不一致，此方法立即返回。
* awaitAdvanceInterruptibly(int phaser): 此方法和awaitAdvance(int phase)一样，但是如果在此方法中睡眠的线程被中断，它会抛出一个 InterruptedException 异常。


#### 注册参与者到 Phaser

当您创建一个 Phaser 对象，您指出该 phaser 有多少个参与者。但是 Phaser 类有2个方法来增加一个phaser的参与者数目。
如下:

* register() : 增加一个新参与者到 Phaser。此新参与者被视为尚未执行当前阶段任务。
* bulkRegister(int Parties): 此方法增加特定数目的参与者到 phaser。此新参与者将被视为尚未执行当前阶段任务。

Phaser 类提供的唯一用来减少参与者数目的方法是 arriveAndDeregister() 方法，其告知 phaser 线程已经结束当前阶段，且不希望继续执行后续阶段。


#### 强制结束一个阶段

当一个 phaser 没有参与者，它进入结束状态。Phaser 类提供了 forceTermination() 方法来改变 phaser 的状态并让它进入结束状态，
不管 phaser 中注册了多少参与者。此机制在其中一个参与者出错了要强制结束 phaser 时有用。

当一个 phaser 处于结束状态，awaitAdvance() 和 arriveAndAwaitAdvance() 方法立即返回一个负数而不是正常返回时的正数。
如果您知道您的 phaser 可能终结，您应校验这些方法的返回值以获知 phaser 是否已被终结。













