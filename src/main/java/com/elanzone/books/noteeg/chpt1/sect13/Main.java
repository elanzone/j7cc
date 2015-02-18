package com.elanzone.books.noteeg.chpt1.sect13;


public class Main {

    public static void main(String[] args) {
        // 创建 MyThreadFactory 和 Task 对象
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        Task task = new Task();

        // 使用 MyThreadFactory 对象创建多个线程并启动它们
        Thread thread;
        System.out.printf("Starting the Threads\n");
        for (int i = 0; i < 10; i++) {
            thread = factory.newThread(task);
            thread.start();
        }

        // 输出线程工厂的统计信息
        System.out.printf("Factory stats:\n");
        System.out.printf("%s\n", factory.getStatistics());
    }

}
