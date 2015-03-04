package com.elanzone.books.noteeg.chpt7.sect06;


import java.util.Date;
import java.util.concurrent.*;

public class MyScheduledTask<V> extends FutureTask<V>
        implements RunnableScheduledFuture<V> {

    private RunnableScheduledFuture<V> task;
    private ScheduledThreadPoolExecutor executor;
    private long period;
    private long startDate;

    /**
     * 构造函数
     *
     * @param runnable    将被一个任务执行的 Runnable 对象
     * @param result      任务的返回值
     * @param executor    将执行任务的 ScheduledThreadPoolExecutor 对象
     * @param task        被用于创建 MyScheduledTask 对象的 RunnableScheduledFuture 任务
     */
    public MyScheduledTask(Runnable runnable, V result,
                           ScheduledThreadPoolExecutor executor, RunnableScheduledFuture<V> task) {
        super(runnable, result);
        this.executor = executor;
        this.task = task;
    }

    @Override
    public boolean isPeriodic() {
        return task.isPeriodic();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        if (!isPeriodic()) {
            return task.getDelay(unit);
        } else {
            if (startDate == 0) {
                return task.getDelay(unit);
            } else {
                Date now = new Date();
                long delay = startDate - now.getTime();
                return unit.convert(delay, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public int compareTo(Delayed o) {
        return task.compareTo(o);
    }

    @Override
    public void run() {
        if (isPeriodic() && (!executor.isShutdown())) {
            Date now = new Date();
            startDate = now.getTime() + period;
            executor.getQueue().add(this);
        }

        System.out.printf("Pre-MyScheduledTask: %s\n", new Date());
        System.out.printf("MyScheduledTask: Is Periodic: %s\n", isPeriodic());
        super.runAndReset();
        System.out.printf("Post-MyScheduledTask: %s\n", new Date());
    }

    public void setPeriod(long period) {
        this.period = period;
    }

}
