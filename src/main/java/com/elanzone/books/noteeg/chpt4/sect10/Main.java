package com.elanzone.books.noteeg.chpt4.sect10;


import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        ResultTask resultTasks[] = new ResultTask[5];

        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task" + i);
            resultTasks[i] = new ResultTask(executableTask);
            executor.submit(resultTasks[i]);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 5; i++) {
            resultTasks[i].cancel(true);
        }

        for (ResultTask resultTask : resultTasks) {
            try {
                if (!resultTask.isCancelled()) {
                    System.out.printf("%s\n", resultTask.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

}
