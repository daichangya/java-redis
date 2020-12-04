package com.daicy.redis.persistence.utils;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.daicy.redis.persistence.utils
 * @date:12/4/20
 */
public class NumberUtils {
    /**
     * <p>Convert a {@code String} to an {@code int}, returning a
     * default value if the conversion fails.</p>
     * <p>
     * <p>If the string is {@code null}, the default value is returned.</p>
     * <p>
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param str          the string to convert, may be null
     * @param defaultValue the default value
     * @return the int represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static Integer toInt(final String str, final Integer defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

}
