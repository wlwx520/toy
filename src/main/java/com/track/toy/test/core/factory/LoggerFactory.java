package com.track.toy.test.core.factory;

import com.track.toy.bean.MultiProcessor;
import com.track.toy.test.core.common.LogType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerFactory {
    @Getter
    private static String loggerRoot;
    @Getter
    private static LogType type;

    private static boolean isRunning = false;

    private static MultiProcessor<Log> multiProcessor = new MultiProcessor<>(10_000, 20, Log::toWrite);

    public static void startLog(String loggerRoot, LogType type) {
        if (isRunning) {
            log.info("file log is running");
            return;
        }
        isRunning = true;
        LoggerFactory.loggerRoot = loggerRoot;
        LoggerFactory.type = type;
    }

    public static void stopLog() {
        multiProcessor.stop();
        isRunning = false;
    }

    public static void log(String message, String path) {
        multiProcessor.add(new Log(message, path));
    }

    @AllArgsConstructor
    private static class Log {
        String message;
        String path;

        private void toWrite() {
            //TODO
        }
    }
}
