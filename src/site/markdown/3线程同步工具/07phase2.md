在并发阶段性任务中控制阶段变化
====

Phaser 类提供了一个方法在每次 phaser 改变阶段时执行。这就是 onAdvance() 方法。

* 它有2个参数：
    * 当前阶段数
    * 注册的参与者数
* 它返回一个Boolean值
    * false : phaser 继续执行
    * true : phaser 已结束进入结束状态
    * 缺省行为
        * 如果注册的参与者数为0，则缺省返回 true;
        * 否则返回 false

您可以通过扩展 Phaser 类并覆盖 onAdvance 方法来改变缺省行为。
通常当您在从一个阶段推进到下一个阶段时必须执行某些动作的情况下，您会有兴趣改变其缺省行为。



### 任务

模拟实现一个测试，在其中学生们必须做3道练习，所有学生必须结束一个练习才能继续下一个。
在每个阶段变化时必须执行某些动作。


### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt3.sect07 package中*

* 扩展 Phaser 类 : MyPhaser

    * 覆盖 onAdvance 方法 : 根据 phase 属性的值调用不同的辅助方法.
        * 各辅助方法输出当前阶段结束和下一阶段的开始信息.
        * 各辅助方法返回 false 表示尚未结束, 继续执行下一阶段

            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                switch (phase) {
                    case 0:
                        return studentsArrived();
                    case 1:
                        return finishFirstExercise();
                    case 2:
                        return finishSecondExercise();
                    case 3:
                        return finishExam();
                    default:
                        return true;
                }
            }

    * studentsArrived 方法

            private boolean studentsArrived() {
                System.out.printf("Phaser: The exam are going to start. The students are ready.\n");
                System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
                return false;
            }

    * 其他方法详见代码

* 学生线程类 : Student

    * 属性 phaser : Phaser 对象
    * 构造函数将 phaser 初始化为参数值

            private Phaser phaser;

            public Student(Phaser phaser) {
                this.phaser = phaser;
            }

    * run方法 : 模拟测试过程
        1. 输出信息到终端表示此学生已入场；并调用 phaser 的 arriveAndWaitAdvance() 方法等待其他线程
        2. 开始练习．练习3次，每次的代码基本如下:
            1. 输出信息到终端
            2. 调用辅助方法 doExerciseN (N指第N个练习)
            3. 输出结束信息到终端
            4. 调用 phaser 的 arriveAdnWaitAdvance() 方法等待其余的学生结束此练习

                System.out.printf("%s: Has arrived to do the exam. %s\n", Thread.currentThread().getName(), new Date());
                phaser.arriveAndAwaitAdvance();

                System.out.printf("%s: Is going to do the first exercise. %s\n", Thread.currentThread().getName(), new Date());
                doExercise1();
                System.out.printf("%s: Has done the first exercise. %s\n", Thread.currentThread().getName(), new Date());
                phaser.arriveAndAwaitAdvance();

                System.out.printf("%s: Is going to do the second exercise.%s\n", Thread.currentThread().getName(), new Date());
                doExercise2();
                System.out.printf("%s: Has done the second exercise. %s\n", Thread.currentThread().getName(), new Date());
                phaser.arriveAndAwaitAdvance();

                System.out.printf("%s: Is going to do the third exercise. %s\n", Thread.currentThread().getName(), new Date());
                doExercise3();
                System.out.printf("%s: Has finished the exam. %s\n", Thread.currentThread().getName(), new Date());
                phaser.arriveAndAwaitAdvance();

    * doExerciseN 方法 : 随机睡上几秒, 例如 doExercise1 方法:

            private void doExercise1() {
                try {
                    long duration = (long) (Math.random() * 10);
                    TimeUnit.SECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

* 控制类 : Main

    * 创建一个 MyPhaser 对象

            MyPhaser phaser = new MyPhaser();

    * 创建 5 个学生对象, 并用 register() 方法注册到 phaser

            Student students[] = new Student[5];
            for (int i = 0; i < students.length; i++) {
                students[i] = new Student(phaser);
                phaser.register();
            }

    * 创建 5 个线程来运行 students 并启动

            Thread threads[] = new Thread[students.length];
            for (int i = 0; i < students.length; i++) {
                threads[i] = new Thread(students[i], "Student " + i);
                threads[i].start();
            }

    * 等待 5 个线程结束后调用 phaser 的 isTerminated 方法输出 phaser 的状态信息

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("Main: The phaser has finished: %s.\n", phaser.isTerminated());



### 工作原理

本例模拟了一个有3个练习的测验。所有学生必须都完成一个练习才能启动下一个。
为了实现此同步需求，使用了 Phaser 类，只是扩展原类覆盖了 onAdvance() 方法实现了自己的 Phaser。

onAdvance() 方法在阶段变化和唤醒所有在 arriveAndAwaitAdvance() 方法内睡眠的线程前被 phaser 调用。
此方法接受实际阶段数作为参数，阶段数和注册参与人数从 0 开始。
最有用的参数时实际阶段。如果您依赖实际的阶段执行一个不同的操作，您必须使用选择语句（if/else或switch）来选择想要执行的操作。
本例使用一个 switch 语句来为每次阶段的变化选择不同的方法。

onAdvance() 方法返回一个 Boolean 值表示 phaser 是否已终结。

* 如果返回 false，表示尚未终结，线程将继续执行其他的阶段
* 如果返回 true, phaser 将唤醒在等待的线程，但是将 phaser 转入已终结状态，
    这样未来所有对此phaser的任一方法的调用都将立即返回，isTerminated() 方法返回 true

在 Main 类中，当创建 MyPhaser 对象时未指定 phaser 内的参与者数目。
您为每个创建的学生对象调用一次 register() 方法来注册一个参与者到 phaser。
此调用不在 Student 对象或对应执行的线程　与　phaser 之间建立联系。
在 phaser 中的参与者数目只是一个数字，在 phaser 和 参与者之间没有关系。



