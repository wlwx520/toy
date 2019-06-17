package com.track.toy.test.core.common;

import com.track.toy.test.core.factory.LoggerFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileLogger {
    private String path;

    public void info(String message) {
        LoggerFactory.log(message, path);
    }

    public void info(String message, Object... objects) {
        if (objects == null || objects.length == 0) {
            info(message);
        }

        String newMessage = message;
        for (Object object : objects) {
            newMessage = message.replace("{}", object.toString());
        }
        info(newMessage);
    }

    public void debug(String message) {
        if (!LoggerFactory.isDebug()) {
            return;
        }

        LoggerFactory.log(message, path);
    }

    public void debug(String message, Object... objects) {
        if (!LoggerFactory.isDebug()) {
            return;
        }

        if (objects == null || objects.length == 0) {
            debug(message);
        }

        String newMessage = message;
        for (Object object : objects) {
            newMessage = message.replace("{}", object.toString());
        }
        debug(newMessage);
    }
}
