package com.track.toy.test.core.common;

import com.track.toy.test.core.factory.LoggerFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileLogger {
    private String path;

    public void info(String message, Object... objects) {
        if (objects == null || objects.length == 0) {
            LoggerFactory.log(message, path);
        }

        String newMessage = message;
        for (Object object : objects) {
            newMessage = message.replace("{}", object.toString());
        }
        LoggerFactory.log(newMessage, path);
    }

    public void debug(String message, Object... objects) {
        if (!LoggerFactory.isDebug()) {
            return;
        }
        info(message, objects);
    }
}
