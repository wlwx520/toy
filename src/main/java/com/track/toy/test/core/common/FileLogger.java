package com.track.toy.test.core.common;

import com.track.toy.test.core.factory.LoggerFactory;
import lombok.AllArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
public class FileLogger {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS  |  ");

    private String path;

    public void debug(String message, Object... objects) {
        if (!LoggerFactory.isDebug()) {
            return;
        }
        message(message, "DEBUG", objects);
    }

    public void info(String message, Object... objects) {
        message(message, "INFO", objects);
    }

    private void message(String message, String type, Object... objects) {
        String newMessage = message;
        if (objects != null) {
            for (Object object : objects) {
                newMessage = message.replace("{}", object.toString());
            }
        }

        LoggerFactory.log(DATE_FORMAT.format(new Date()) + type + "  |  " + newMessage, path);
    }
}
