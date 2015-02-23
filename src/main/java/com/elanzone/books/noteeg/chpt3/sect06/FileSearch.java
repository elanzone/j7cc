package com.elanzone.books.noteeg.chpt3.sect06;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 在一个文件夹及其子文件夹中查找以一后缀结尾、最近修改时间在24小时内的文件
 */
public class FileSearch implements Runnable {

    private String initPath;// 在此文件夹及子文件夹中查找
    private String end;     // 文件结束字符串(后缀)
    private List<String> results;

    private Phaser phaser;  // 控制任务不同步骤的同步

    public FileSearch(String initPath, String end, Phaser phaser) {
        this.initPath = initPath;
        this.end = end;
        this.phaser = phaser;
        results = new ArrayList<String>();
    }

    private void directoryProcess(File file) {
        File list[] = file.listFiles();
        if (list != null) {
            for (File aList : list) {
                if (aList.isDirectory()) {
                    directoryProcess(aList);
                } else {
                    fileProcess(aList);
                }
            }
        }
    }

    private void fileProcess(File file) {
        // 文件名以end结尾的
        if (file.getName().endsWith(end)) {
            results.add(file.getAbsolutePath());
        }
    }

    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = new Date().getTime();
        for (String result : results) {
            File file = new File(result);
            long fileDate = file.lastModified();
            // 最后修改时间在一天内的
            if ((actualDate - fileDate) < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(result);
            }
        }
        results = newResults;
    }

    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n",Thread.currentThread().getName(),phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n",Thread.currentThread().getName(),phaser.getPhase());
            phaser.arriveAndDeregister();   // 到达此检录点并退出比赛
            return false;
        } else {
            System.out.printf("%s: Phase %d: %d results.\n",Thread.currentThread().getName(),phaser.getPhase(),results.size());
            phaser.arriveAndAwaitAdvance(); // 到达此检录点并等待其他参赛者以继续下一阶段比赛
            return true;
        }
    }

    private void showInfo() {
        for (String result : results) {
            File file = new File(result);
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        phaser.arriveAndAwaitAdvance();     // 到达此检录点并等待其他参赛者以继续下一阶段比赛
    }

    @Override
    public void run() {
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s: Starting.\n",Thread.currentThread().getName());

        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }

        if (!checkResults()){
            return;
        }

        filterResults();

        if (!checkResults()){
            return;
        }

        showInfo();
        phaser.arriveAndDeregister();   // 已到达终点，退出比赛
        System.out.printf("%s: Work completed.\n",Thread.currentThread().getName());
    }

}
