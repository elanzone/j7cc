package com.elanzone.books.noteeg.chpt1;

public class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;
        while (true) {
            // 判断是否为质数
            if (isPrime(number)) {
                System.out.printf("Number %d is Prime\n", number);
            }
            // 判断是否被中断
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0) {
                return false;
            }
        }
        return true;
    }
}
