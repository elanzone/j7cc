package com.elanzone.books.noteeg.chpt7.sect04;

public class Main {

    public static void main(String[] args) {
        MyThreadFactory factory=new MyThreadFactory("MyThreadFactory");
        MyTask task = new MyTask();

        Thread thread = factory.newThread(task);

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Thread information.\n");
        System.out.printf("%s\n",thread);
        System.out.printf("Main: End of the example.\n");
    }
}
