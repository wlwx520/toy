package com.track.toy.test.core.factory;

import com.track.toy.bean.MultiProcessor;
import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.common.FileLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class LoggerFactory {

    private static String loggerRoot = FileHelper.getAppRoot() + "/log/log" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    @Getter
    private static boolean isDebug = true;

    private static boolean isRunning = false;

    private static MultiProcessor<Log> multiProcessor;

    private static final FileLogger SYSTEM_FILE_LOGGER = new FileLogger("system.text");

    public static void systemLog(String message, Object... objects) {
        SYSTEM_FILE_LOGGER.info(message, objects);
    }

    public static FileLogger initFileLogger(String fileName) {
        return new FileLogger(loggerRoot + "/" + fileName);
    }

    public static void startLog() {
        String logLevel = ConfigureFactory.get("log-level");
        LoggerFactory.isDebug = logLevel != null && logLevel.toLowerCase().equals("debug");

        if (LoggerFactory.isRunning) {
            log.info("file log is running");
            return;
        }
        LoggerFactory.isRunning = true;
        LoggerFactory.multiProcessor = new MultiProcessor<>(10_000, 20, Log::toWrite);
    }

    public static void stopLog() {
        multiProcessor.stop();
        isRunning = false;
    }

    public static void log(String message, String path) {
        multiProcessor.add(new Log(message, loggerRoot + "/" + path + ".txt"));
    }

    @AllArgsConstructor
    private static class Log {
        String message;
        String path;

        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS  |  ");

        private void toWrite() {
            File file = new File(path);
            FileHelper.createDirAndFileIfNotExists(file);

            String toWriteMessage = DATE_FORMAT.format(new Date()) + message;
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));) {
                writer.write(toWriteMessage);
                writer.newLine();
                writer.newLine();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
