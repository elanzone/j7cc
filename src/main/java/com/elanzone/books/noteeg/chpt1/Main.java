package com.elanzone.books.noteeg.chpt1;

public class Main {

    public static void main(String[] args) {

        for (int i = 1; i <= Calculator.ThreadNum; i++) {
            Calculator calculator = new Calculator(i);
            Thread thread = new Thread(calculator);
            thread.start();
        }
    }

}
