package com.elanzone.books.noteeg.chpt1;

import java.util.Date;

public class Main {

    public static void main(String[] args) {

        DataSourcesLoader dsLoader = new DataSourcesLoader();
        Thread dsThread = new Thread(dsLoader, "DataSourceLoader");

        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread ncThread = new Thread(ncLoader, "NetworkConnectionsLoader");

        dsThread.start();
        ncThread.start();

        try {
            dsThread.join();
            ncThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Configuration has been loaded: %s\n",new Date());
    }

}
