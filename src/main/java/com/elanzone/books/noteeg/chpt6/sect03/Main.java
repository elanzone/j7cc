package com.elanzone.books.noteeg.chpt6.sect03;


import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        LinkedBlockingDeque<String> list = new LinkedBlockingDeque<>(3);

        Client client = new Client(list);
        Thread thread = new Thread(client);
        thread.start();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                String request = null;
                try {
                    request = list.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                System.out.printf("Main: Request: %s at %s. Size: %d\n", request, new Date(), list.size());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: End of the program.\n");
    }
}
