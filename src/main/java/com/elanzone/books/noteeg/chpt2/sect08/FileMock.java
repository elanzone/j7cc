package com.elanzone.books.noteeg.chpt2.sect08;

/**
 * 模拟文本文件
 */
public class FileMock {

    private String content[]; // 文件内容, 行数组
    private int index;        // 行数组的当前行号

    /**
     * 填充 size 行长度为 length 的随机字符串
     *
     * @param size      文件的总行数
     * @param length    每行的长度
     */
    public FileMock(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int indice = (int) (Math.random() * 255);
                buffer.append((char) indice);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    public boolean hasMoreLines() {
        return index < content.length;
    }

    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }

}
