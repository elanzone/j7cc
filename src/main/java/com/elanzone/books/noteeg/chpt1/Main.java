package com.elanzone.books.noteeg.chpt1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {

        Thread threads[] = new Thread[Calculator.ThreadNum];
        Thread.State status[] = new Thread.State[Calculator.ThreadNum];

        // 设置线程的优先级
        for (int i = 0; i < Calculator.ThreadNum; i++) {
            threads[i] = new Thread(new Calculator(i));
            if ((i%2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread " + i);
        }

        // 使用log.txt文件记录线程的初始状态 （此时状态应该为 new）
        FileWriter file;
        try {
            file = new FileWriter(".\\data\\log.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        PrintWriter pw = new PrintWriter(file);

        // 记录线程的状态
        for (int i = 0; i < Calculator.ThreadNum; i++) {
            pw.println("Main : Status of Thread "+i+" : " + threads[i].getState());
            status[i]=threads[i].getState();
        }

        // 启动线程
        for (int i = 0; i < Calculator.ThreadNum; i++) {
            threads[i].start();
        }

        // 当线程状态变化时，输出线程状态变化信息
        boolean finish = false;
        while (!finish) {
            for (int i = 0; i < Calculator.ThreadNum; i++) {
                if (threads[i].getState() != status[i]) {
                    writeThreadInfo(pw, threads[i], status[i]);
                    status[i] = threads[i].getState();
                }
            }

            // 当所有线程的状态都为 TERMINATED 时结束
            finish = true;
            for (int i = 0; i < Calculator.ThreadNum; i++) {
                finish = finish && (threads[i].getState() == Thread.State.TERMINATED);
            }
        }

        pw.flush();
        pw.close();
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeThreadInfo(PrintWriter pw, Thread thread, Thread.State state) {
        pw.printf("Main : Id %d - %s\n",thread.getId(),thread.getName());
        pw.printf("Main : Priority: %d\n",thread.getPriority());
        pw.printf("Main : Old State: %s\n",state);
        pw.printf("Main : New State: %s\n",thread.getState());
        pw.printf("Main : ************************************\n");
    }

}
