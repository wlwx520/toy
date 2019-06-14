package com.track.toy.test.core.common;

import com.track.toy.test.core.factory.LoggerFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileLogger {
    private String path;

    public void info(String message) {
        LoggerFactory.log(message, path);
    }
}
