package com.track.toy.helper;

public class ClassParseHelper {
    public static int toInt(Object origin, int defaultValue) {
        try {
            return Integer.valueOf(origin.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
