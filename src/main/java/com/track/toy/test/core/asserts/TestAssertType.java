package com.track.toy.test.core.asserts;

import java.util.Arrays;

public enum TestAssertType implements ITestAssertJudge {
    AND {
        @Override
        public boolean judge(Object source, Object target) {
            return true;
        }
    },
    OR {
        @Override
        public boolean judge(Object source, Object target) {
            return true;
        }
    },
    EQ {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) == 0;
        }
    },
    GTE {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) >= 0;
        }
    },
    GT {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) > 0;
        }
    },
    LTE {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) <= 0;
        }
    },
    LT {
        @Override
        public boolean judge(Object source, Object target) {
            return ((Comparable) source).compareTo((Comparable) target) < 0;
        }
    },
    NOT {
        public boolean judge(Object source, Object target) {
            if (source == null) {
                return target == null;
            }
            return !source.equals(target);
        }
    },
    CONTAINS {
        @Override
        public boolean judge(Object source, Object target) {
            String[] split = source.toString().split(",");
            if (split == null || split.length == 0) {
                return target == null || target.toString().trim().isEmpty();
            }
            return Arrays.asList(split).contains(target.toString());
        }
    },
    NOT_NULL {
        @Override
        public boolean judge(Object source, Object target) {
            return source != null;
        }
    },
    IS_NUMBER {
        @Override
        public boolean judge(Object source, Object target) {
            if (source == null) {
                return false;
            }
            try {
                Double.valueOf(source.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    },

    //=============================================================================
    ;

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
