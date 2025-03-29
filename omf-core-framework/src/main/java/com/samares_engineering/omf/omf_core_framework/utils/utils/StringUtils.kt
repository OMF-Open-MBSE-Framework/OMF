/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.utils.utils

import org.apache.commons.lang3.StringUtils
import java.util.*

object StringUtils {
    @JvmStatic
    fun compareStringsNoCaseNoSpace(s1: String?, s2: String?): Boolean {
        return StringUtils.deleteWhitespace(s1).equals(StringUtils.deleteWhitespace(s2), ignoreCase = true)
    }

    /**
     * Transform a string to a camelCase formatted string
     * @param str : string to transform
     * @return the camelCase formatted string
     *
     * Example: from "this is an example" to "thisIsAnExample"
     */
    @JvmStatic
    fun toCamelCase(str: String): String {
        val words = str.lowercase(Locale.getDefault()).split("[^a-zA-Z]+".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray() // TODO : may want to keep number at some point, just not in first position
        val sb = StringBuilder(words[0])
        for (i in 1 until words.size) {
            sb.append(words[i].substring(0, 1).uppercase(Locale.getDefault()))
            sb.append(words[i].substring(1))
        }
        return sb.toString()
    }

    @JvmStatic
    fun toPascalSnakeCase(input: String): String {
        return input.split(Regex("\\s+|_")) // Split by spaces or underscores
            .filter { it.isNotBlank() }
            .joinToString("_") { it.lowercase().replaceFirstChar { it.uppercase() } }
    }


    @JvmStatic
    fun toSnakeCase(input: String): String {
        return input.split(Regex("\\s+|_")) // Split by spaces or underscores
            .filter { it.isNotBlank() }
            .joinToString("_") { it.lowercase() }
    }

}
