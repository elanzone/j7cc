package com.elanzone.books.noteeg.chpt2.sect08;


import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Buffer
 * <br/>
 * 由生产者和消费者共享的文件缓存
 */
public class Buffer {

    private LinkedList<String> buffer; // 存储共享数据
    private int maxSize;               // 缓冲区最大长度
    private boolean pendingLines;      // 标识是否还有内容可读入缓冲区

    private ReentrantLock lock;        // 控制对修改缓冲区的代码块的访问权限
    private Condition lines;           // 判断是否有数据
    private Condition space;           // 判断是否有空间

    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition(); // note：Condition 和 Lock 有关
        space = lock.newCondition();
        pendingLines = true;
    }

    /**
     * 将 line 存入 buffer
     *
     * @param line 一行
     */
    public void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread().getName(), buffer.size());
            lines.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public String get() {
        String line = null;
        lock.lock();
        try {
            while ((buffer.size() == 0) && (hasPendingLines())) {
                lines.await();
            }
            if (hasPendingLines()) {
                line = buffer.poll();
                System.out.printf("%s: Line Readed: %d\n", Thread.currentThread().getName(), buffer.size());
                space.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return line;
    }

    public void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    public boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}
