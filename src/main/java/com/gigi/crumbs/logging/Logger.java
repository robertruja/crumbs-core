package com.gigi.crumbs.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger<T> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final String PATTERN = "%T [%L] %C %M";

    private static Level level;
    private Class<T> clazz;
    private Date date = new Date();

    private Logger(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <U> Logger<U> getLogger(Class<U> clazz) {
        return getLogger(clazz, Level.INFO);
    }

    public static <U> Logger<U> getLogger(Class<U> clazz, Level level) {
        return new Logger<>(clazz);
    }

    public static void setLevel(Level level) {
        Logger.level = level;
    }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void debug(String message, Object... args) {
        log(Level.DEBUG, message, args);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void warn(String message, Object... args) {
        log(Level.WARN, message, args);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void error(String message, Object... args) {
        log(Level.ERROR, message, args);
    }

    public void error(String message, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        message = message + ". Underlying cause: " + sw.toString();
        log(Level.ERROR, message);
    }

    private void log(Level level, String message, Object... args) {
        if(level.getOrdinal() <  Logger.level.getOrdinal()) {
            return;
        }
        date.setTime(System.currentTimeMillis());
        String formatted = PATTERN.replace("%T", DATE_FORMAT.format(date));
        formatted = formatted.replace("%L", level.toString());
        formatted = formatted.replace("%C", clazz.getSimpleName());
        message = formatMessage(message, args);
        formatted = formatted.replace("%M", message);
        System.out.println(formatted);
    }

    private String formatMessage(String message, Object... args) {
        String formatted = message;
        for(Object arg: args) {
            formatted = formatted.replace("{}", arg.toString());
        }
        return formatted;
    }
}
