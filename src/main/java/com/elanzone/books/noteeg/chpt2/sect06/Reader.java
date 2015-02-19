package com.elanzone.books.noteeg.chpt2.sect06;


public class Reader implements Runnable {

    private PricesInfo pricesInfo;

    public Reader(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s: Price 1: %f, Price 2: %f\n",
                    Thread.currentThread().getName(), pricesInfo.getPrice1(), pricesInfo.getPrice2());
        }
    }
}
