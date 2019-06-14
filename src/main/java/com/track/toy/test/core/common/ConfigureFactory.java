package com.track.toy.test.core.common;

import java.util.HashMap;
import java.util.Map;

public class ConfigureFactory {
    private static final Map<String, String> PROPERTIES = new HashMap<>();

    public static void load(String key, String value) {
        PROPERTIES.put(key, value);
    }

    public static String get(String key) {
        return PROPERTIES.get(key);
    }
}
