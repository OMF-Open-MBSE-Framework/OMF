/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.utils;

public class StringUtils {
    private StringUtils() {}

    public static boolean compareStringsNoCaseNoSpace(String s1, String s2) {
        return org.apache.commons.lang.StringUtils.deleteWhitespace(s1).equalsIgnoreCase(org.apache.commons.lang.StringUtils.deleteWhitespace(s2));
    }
}
