package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;

public class CodeGenerationUtils {

    /**
     * Write the class provided by a class builder to a file
     * @param classBuilder : class to write
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
            LegacyErrorHandler.handleException(new CodeGenerationException("Unable to save the generated file.", GenericException.ECriticality.ALERT), false);
        }
    }


}

