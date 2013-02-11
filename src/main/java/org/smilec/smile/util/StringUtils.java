package org.smile.smilec.util;

public class StringUtils {

    private StringUtils() {}

    public static final boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }
}