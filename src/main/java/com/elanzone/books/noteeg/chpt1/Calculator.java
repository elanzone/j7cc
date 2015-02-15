package com.elanzone.books.noteeg.chpt1;

public class Calculator implements Runnable {

    public static int ThreadNum = 10;

    private int number;

    public Calculator(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        // 输出乘法结果
        for (int i = 1; i <= ThreadNum; i++) {
            System.out.printf("%s: %d * %d = %d\n",
                    Thread.currentThread().getName(), number, i, i * number);
        }
    }

}
