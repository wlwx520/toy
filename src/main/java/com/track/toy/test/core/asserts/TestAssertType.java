package com.track.toy.test.core.asserts;

import java.util.Arrays;

public enum TestAssertType {
    AND {
    },
    OR {
    },
    GTE {
        public boolean judge(String source, String target) {
            return source.compareTo(target) >= 0;
        }
    },
    GT {
        public boolean judge(String source, String target) {
            return source.compareTo(target) > 0;
        }
    },
    LTE {
        public boolean judge(String source, String target) {
            return source.compareTo(target) <= 0;
        }
    },
    LT {
        public boolean judge(String source, String target) {
            return source.compareTo(target) < 0;
        }
    },
    NOT {
        public boolean judge(String source, String target) {
            return source.compareTo(target) != 0;
        }
    },
    CONTAINS {
        public boolean judge(String source, String target) {
            if (source.trim().isEmpty()) {
                return target.trim().isEmpty();
            }
            return Arrays.asList(source.split(",")).contains(target);
        }
    }

    //=============================================================================
    ;

    public boolean judge(String source, String target) {
        return true;
    }

    public static TestAssertType getFromType(String type) {
        if (type == null) {
            throw new RuntimeException("TestAssertType is null");
        }
        TestAssertType[] types = TestAssertType.values();
        for (TestAssertType assertType : types) {
            if (assertType.name().toLowerCase().equals(type.toLowerCase())) {
                return assertType;
            }
        }
        throw new RuntimeException("TestAssertType not found , type = " + type);
    }
}
