package com.elanzone.books.noteeg.chpt7.sect06;


import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public MyScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * 创建并返回一个 MyScheduledTask<V>对象
     *
     * @param runnable  将被执行的 Runnable 对象
     * @param task      将执行 Runnable 对象的任务
     * @param <V>       泛型参数
     * @return          返回 MyScheduledTask<V> 对象
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
                                                          RunnableScheduledFuture<V> task) {
        return new MyScheduledTask<>(runnable, null, this, task);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture<?> task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
        MyScheduledTask<?> myTask = (MyScheduledTask<?>) task;
        myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
        return task;
    }
}
