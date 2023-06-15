package com.samares_engineering.omf.omf_gradle_plugin

import org.gradle.api.Project

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OmfGradleUtils {
    static generateInternalAndResourceVersion(String humanVersion) {
        int majorInt, minorInt, patchInt
        try {
            def (_,major,minor,patch) = (humanVersion =~ /^(\d+)\.(\d{1,2})\.(\d{1,2}).*$/)[0]
            majorInt = major.isInteger() ? major as Integer : 0
            minorInt = minor.isInteger() ? minor as Integer : 0
            patchInt = patch.isInteger() ? patch as Integer : 0
        } catch (any) {
            throw new Exception("Bad version nomenclature: $humanVersion", any);
        }

        int internalInt = majorInt*10000 + minorInt*100 + patchInt
        int resourceInt = majorInt*100000 + minorInt*1000 + patchInt*10

        return [internalInt.toString(), resourceInt.toString()]
    }
}
