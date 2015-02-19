package com.elanzone.books.noteeg.chpt2.sect06;

public class Main {

    public static void main(String[] args) {

        PricesInfo pricesInfo = new PricesInfo();

        int readerCnt = 5;
        Reader readers[] = new Reader[readerCnt];
        Thread threadReaders[] = new Thread[readerCnt];
        for (int i = 0; i < readerCnt; i++) {
            readers[i] = new Reader(pricesInfo);
            threadReaders[i] = new Thread(readers[i], "ReaderThread" + i);
        }

        Writer writer = new Writer(pricesInfo);
        Thread threadWriter = new Thread(writer, "WriterThread");

        for (int i = 0; i < readerCnt; i++) {
            threadReaders[i].start();
        }
        threadWriter.start();
    }
}
