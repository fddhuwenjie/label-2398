package com.hotel.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工具类
 */
public class LogUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_FILE = "hotel.log";
    
    public static void info(String message) {
        log("INFO", message);
    }
    
    public static void warn(String message) {
        log("WARN", message);
    }
    
    public static void error(String message) {
        log("ERROR", message);
    }
    
    public static void error(String message, Throwable e) {
        log("ERROR", message + " - " + e.getMessage());
        writeToFile("ERROR", getStackTrace(e));
    }
    
    private static void log(String level, String message) {
        String logMessage = String.format("[%s] [%s] %s", sdf.format(new Date()), level, message);
        System.out.println(logMessage);
        writeToFile(level, message);
    }
    
    private static void writeToFile(String level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] [%s] %s%n", sdf.format(new Date()), level, message);
        } catch (Exception ignored) {
        }
    }
    
    private static String getStackTrace(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\t").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
