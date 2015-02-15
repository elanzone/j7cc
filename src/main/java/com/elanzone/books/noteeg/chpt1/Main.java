package com.elanzone.books.noteeg.chpt1;

public class Main {

    public static void main(String[] args) {

        Thread task = new PrimeGenerator();
        task.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断task
        task.interrupt();
    }

}
