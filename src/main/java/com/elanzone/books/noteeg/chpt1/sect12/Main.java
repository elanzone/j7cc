package com.elanzone.books.noteeg.chpt1.sect12;


public class Main {

    public static void main(String[] args) {
        MyThreadGroup threadGroup = new MyThreadGroup("MyThreadGroup");
        Task task = new Task();

        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(threadGroup, task);
            thread.start();
        }
    }

}
