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
    @Getter
    private static String loggerRoot;

    @Getter
    private static boolean isDebug = false;

    private static boolean isRunning = false;

    private static MultiProcessor<Log> multiProcessor;

    private static final FileLogger SYSTEM_FILE_LOGGER = new FileLogger(FileHelper.getAppRoot() + "/log/system.text");

    public static void systemLog(String message, Object... objects) {
        SYSTEM_FILE_LOGGER.info(message, objects);
    }

    public static FileLogger initFileLogger(String fileName) {
        return new FileLogger(loggerRoot + "/" + fileName);
    }

    public static void startLog(String loggerRoot) {
        if (LoggerFactory.isRunning) {
            log.info("file log is running");
            return;
        }
        LoggerFactory.isRunning = true;
        LoggerFactory.loggerRoot = loggerRoot;
        LoggerFactory.multiProcessor = new MultiProcessor<>(10_000, 20, Log::toWrite);
    }

    public static void stopLog() {
        multiProcessor.stop();
        isRunning = false;
        multiProcessor = null;
    }

    public static void log(String message, String path) {
        multiProcessor.add(new Log(message, path));
    }

    public static void setDebug(boolean isDebug) {
        LoggerFactory.isDebug = isDebug;
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
