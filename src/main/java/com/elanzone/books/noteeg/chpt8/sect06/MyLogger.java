package com.elanzone.books.noteeg.chpt8.sect06;


import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

    private static Handler handler;

    public static Logger getLogger(String name) {
        // 5. 获得与 name 关联的 Logger
        Logger logger = Logger.getLogger(name);

        // 6. 设置日志级别以输出所有级别的日志
        logger.setLevel(Level.ALL);

        try {
            // 在用到时才创建 handler
            if (handler == null) {
                handler = new FileHandler("recipe8.log");
                Formatter format = new MyFormatter();
                handler.setFormatter(format);
            }
            if (logger.getHandlers().length == 0) {
                logger.addHandler(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }
}
