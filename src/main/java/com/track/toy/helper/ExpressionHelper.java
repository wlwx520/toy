package com.track.toy.helper;

import java.util.HashMap;
import java.util.Map;

public class ExpressionHelper {
    private Map<String, ExpressionFilter> filters = new HashMap<>();

    private static final char BEFORE = '(';
    private static final String BEFORE_FORMAT = "/\\(";
    private static final char AFTER = ')';
    private static final String AFTER_FORMAT = "/\\)";

    public void addFilter(String key, ExpressionFilter filter) throws ExpressionException {
        if (key.startsWith("$")) {
            if (filters.containsKey(key)) {
                throw new ExpressionException("this key of expression is exsits.");
            }
            filters.put(key, filter);
        } else {
            throw new ExpressionException("key must start with $.");
        }
    }

    public String expressionFilter(String origin, Object... objs) throws ExpressionException {
        if (!check(origin)) {
            return origin;
        }

        StringBuilder result = new StringBuilder();
        StringBuilder key = new StringBuilder();
        StringBuilder content = new StringBuilder();
        boolean skip = false;
        boolean isExpression = false;
        boolean isKey = false;
        boolean simple = true;
        int expressionCurrentCount = -1;

        for (int index = 0; index < origin.length(); ) {
            char charAt = origin.charAt(index);

            if (!isExpression && skip) {
                skip = false;
                index++;
                result.append(charAt);
                continue;
            }

            if (!isExpression && !skip && charAt == '/') {
                skip = true;
                index++;
                continue;
            }

            if (!isExpression && !skip && charAt == '$') {
                index++;
                expressionCurrentCount = 0;
                isExpression = true;
                isKey = true;
                continue;
            }

            if (!isExpression && !skip && !(charAt == '$')) {
                index++;
                result.append(charAt);
                continue;
            }

            assert (isExpression && isKey && expressionCurrentCount == 0 && !skip);

            if (isExpression && isKey && (charAt == '$')) {
                throw new ExpressionException("expression error,the key is not allow $");
            }

            if (isExpression && isKey && (charAt == BEFORE)) {
                index++;
                isKey = false;
                continue;
            }

            if (isExpression && isKey && !(charAt == AFTER)) {
                index++;
                key.append(charAt);
                continue;
            }

            if (isExpression && !isKey && skip && charAt == '/') {
                skip = false;
                index++;
                content.append(charAt);
                if (content.toString().indexOf("$") != -1) {
                    content.append(charAt);
                }
                continue;
            }

            if (isExpression && !isKey && skip && charAt != '/') {
                skip = false;
                index++;
                content.append(charAt);
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == '/' && expressionCurrentCount != 0) {
                skip = true;
                index++;
                content.append(charAt);
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == '/' && expressionCurrentCount == 0) {
                skip = true;
                index++;
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == '$') {
                index++;
                content.append(charAt);
                expressionCurrentCount++;
                simple = false;
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == AFTER && expressionCurrentCount != 0) {
                index++;
                content.append(charAt);
                expressionCurrentCount--;
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == AFTER && expressionCurrentCount == 0 && !simple) {
                index++;
                ExpressionFilter expressionFilter = filters.get("$" + key.toString());
                if (expressionFilter == null) {
                    throw new ExpressionException("this key of filter is not exits, key = " + key.toString());
                }
                result.append(expressionFilter.expressionFilter(expressionFilter(content.toString(), objs), objs));
                isExpression = false;
                isKey = false;
                simple = true;
                expressionCurrentCount = -1;
                key = new StringBuilder();
                content = new StringBuilder();
                continue;
            }

            if (isExpression && !isKey && !skip && charAt == AFTER && expressionCurrentCount == 0 && simple) {
                index++;
                ExpressionFilter expressionFilter = filters.get("$" + key.toString());
                if (expressionFilter == null) {
                    throw new ExpressionException("this key of filter is not exits, key = " + key.toString());
                }
                result.append(expressionFilter.expressionFilter(content.toString(), objs));
                isExpression = false;
                isKey = false;
                expressionCurrentCount = -1;
                key = new StringBuilder();
                content = new StringBuilder();
                continue;
            }

            if (isExpression && !isKey && !skip) {
                index++;
                content.append(charAt);
                continue;
            }

        }

        return result.toString();
    }

    private boolean check(String origin) {
        origin = origin.replaceAll("/\\$", "");
        origin = origin.replaceAll(BEFORE_FORMAT, "");
        origin = origin.replaceAll(AFTER_FORMAT, "");

        int indexOf1 = origin.indexOf("$");
        int indexOf2 = origin.indexOf(BEFORE);
        int indexOf3 = origin.lastIndexOf(AFTER);

        if (indexOf1 * indexOf2 * indexOf3 < 0) {
            return false;
        }
        return indexOf1 < indexOf2 && indexOf2 < indexOf3;
    }

    @FunctionalInterface
    public static interface ExpressionFilter {
        public String expressionFilter(String origin, Object... objs);

    }

    public static class ExpressionException extends RuntimeException {
        private static final long serialVersionUID = -8686135403359302221L;

        public ExpressionException(String msg) {
            super(msg);
        }
    }

}
