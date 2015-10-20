package com.wostrowski.airscanner;

import org.apache.log4j.*;

/**
 * Created by wojtek on 20.10.15.
 */
public class Log {
    private static Logger logger;

    static {
        logger = Logger.getLogger("default");
        logger.setAdditivity(false);
        logger.setLevel(Level.ALL);
        logger.removeAllAppenders();
        logger.addAppender(new ConsoleAppender(new PatternLayout("[%d] [%p] %m%n")));
    }

    public static void d(String msg) {
        logger.debug(msg);
    }

    public static void e(String msg) {
        logger.error(msg);
    }

    public static void e(String msg, Throwable t) {
        logger.error(msg, t);
    }
}
