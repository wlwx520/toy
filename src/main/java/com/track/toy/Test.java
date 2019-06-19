package com.track.toy;

import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.common.FileLogger;
import com.track.toy.test.core.factory.LoggerFactory;

public class Test {
    public static void main(String[] args) {
        LoggerFactory.startLog();
        LoggerFactory.systemLog("xxxxx");
        FileLogger fileLogger1 = LoggerFactory.initFileLogger("data1/"+"test1.txt");
        FileLogger fileLogger2 = LoggerFactory.initFileLogger("data1/"+"test2.txt");
        FileLogger fileLogger3 = LoggerFactory.initFileLogger("data1/"+"test3.txt");

        fileLogger1.info("test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1");
        fileLogger2.info("test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2test2");
        fileLogger3.info("test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3test3");

        LoggerFactory.stopLog();
        System.out.println("test end");
    }
}
