package com.elanzone.books.noteeg.chpt3.sect03;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueue {

    private Semaphore semaphore;

    public static int FreePrinterCount = 3;
    private boolean freePrinters[];
    private Lock lockPrinters;

    public PrintQueue() {
        semaphore = new Semaphore(FreePrinterCount);
        freePrinters = new boolean[FreePrinterCount];
        for (int i = 0; i < FreePrinterCount; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document) {

        try {
            semaphore.acquire();

            int assignedPrinter = getPrinter();

            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job in Printer %d during %d seconds\n",
                    Thread.currentThread().getName(), assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);

            freePrinters[assignedPrinter] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

    }

    /**
     * todo: 为什么设置为 true 的时候不用加锁？
     *
     * @return 可用打印机索引
     */
    private int getPrinter() {
        int ret = -1;

        try {
            lockPrinters.lock();

            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockPrinters.unlock();
        }

        return ret;
    }

}
