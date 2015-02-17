package com.elanzone.books.noteeg.chpt1.sect08;

import java.util.ArrayDeque;
import java.util.Deque;

public class Main {

    public static void main(String[] args) {

        Deque<Event> deque = new ArrayDeque<>();

        WriterTask writer = new WriterTask(deque);
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(writer);
            thread.start();
        }

        CleanerTask cleaner = new CleanerTask(deque);
        cleaner.start();

    }

}
