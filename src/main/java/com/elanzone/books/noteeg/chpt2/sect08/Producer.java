package com.elanzone.books.noteeg.chpt2.sect08;

/**
 * Producer
 * <p>
 *      将文件内容从 FileMock 中读入到缓冲区
 * </p>
 */
public class Producer implements Runnable {

    private FileMock mock;
    private Buffer buffer;

    public Producer(FileMock mock, Buffer buffer) {
        this.mock = mock;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setPendingLines(false);
    }
}
