/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class VersionUtils {
    private VersionUtils() {}

    /**
     * Utility method to parse csv with plugin versions.
     *
     * @return String the version of the plugin
     */
    public static String versionCsvReader() {
        ClassLoader classLoader = OMFUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("plugin_versions.csv");
        HashMap<String, String> versionPluginList = new HashMap<>();
        try (
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] fields = line.split(";");
                String name = fields[0];
                String version = fields[1];
                versionPluginList.put(name, version);
            }
        } catch (IOException e) {
            LegacyErrorHandler.handleException(e, false);
        }
        if (versionPluginList.containsKey("plugin_version")) {
            return versionPluginList.get("plugin_version");
        }
        return null;
    }

    public static class Version implements Comparable<Version> {

        private String version;

        public Version(String version) {
            if (version == null)
                throw new IllegalArgumentException("Version can not be null");
            if (!version.matches("[0-9]+(\\.[0-9]+)*"))
                throw new IllegalArgumentException("Invalid version format");
            this.version = version;
        }

        public final String get() {
            return this.version;
        }

        @Override
        public int compareTo(Version that) {
            if (that == null)
                return 1;
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart)
                    return -1;
                if (thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

    }
}
