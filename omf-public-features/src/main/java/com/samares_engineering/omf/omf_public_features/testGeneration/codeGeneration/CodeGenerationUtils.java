package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;

public class CodeGenerationUtils {

    /**
     * Write the class provided by a class builder to a file
     * @param classBuilder
     * @param generationPathRoot : Root path where to generate the file (then the package folder will be added)
     * @param classPackage : package of the generated class
     *
     * Example : generationPathRoot : /home/myProject/foo
     *           classPackage       : myPackage
     *           class generated : /home/myProject/foo/myPackage/className.java
     */
    public static void writeToFile(TypeSpec classBuilder, String generationPathRoot, String classPackage) {
        JavaFile javaFile = JavaFile.builder(classPackage, classBuilder)
                .build();

        try {
            javaFile.writeTo(new File(generationPathRoot));
        } catch (IOException e) {
            OMFErrorHandler.handleException(new CodeGenerationException("Unable to save the generated file.", GenericException.ECriticality.ALERT), false);
        }
    }

    /**
     * Transform a string to a camelCase formatted string
     * @param str
     * @return
     *
     * Example: from "this is an example" to "thisIsAnExample"
     */
    public static String toCamelCase(String str) {
        String[] words = str.toLowerCase().split("[^a-zA-Z]+"); // TODO : may want to keep number at some point, just not in first position
        StringBuilder sb = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++) {
            sb.append(words[i].substring(0, 1).toUpperCase());
            sb.append(words[i].substring(1));
        }
        return sb.toString();
    }
}

