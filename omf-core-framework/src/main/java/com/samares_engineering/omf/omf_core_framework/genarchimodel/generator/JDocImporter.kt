package com.samares_engineering.omf.omf_core_framework.genarchimodel.generator

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.nomagic.esi.emf.a.F
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Method

class JDocImporter(val elementManager: ModelElementManager) {
    val files = HashMap<String, File>()
    val parsedClass = HashMap<File, CompilationUnit>()

    fun getClassJavadoc(pluginClass: Class<*>, pathToFiles: String, domain: String): String? {
        val sourceFile = try {
            getSourceFilePath(pluginClass, pathToFiles, domain)
        } catch (e: FileNotFoundException) {
//            OMFLogger.errorToSystemConsole(OMFLog().err("Error while getting source file for class: ${pluginClass.name}").expandText(OMFLog().text(e.message)))
            return ""
        }

        val parser = JavaParser()
        val compilationUnit: CompilationUnit = parseClass(sourceFile, parser)?: return ""

        val className = pluginClass.simpleName

        val classDeclaration = compilationUnit
            .types
            .asSequence()
            .filterIsInstance<ClassOrInterfaceDeclaration>()
            .firstOrNull { it.nameAsString == className }

        return classDeclaration?.javadocComment?.orElse(null)?.content
    }

    fun getMethodJavadoc(classifier: Classifier, method: Method, pathToFiles: String, domain: String): String {
        val pluginClass = try {
            elementManager.getClassFromClassifier(classifier)
        } catch (e: Exception){
            OMFLogger.errorToSystemConsole(OMFLog().err("Error while getting class from classifier: ${classifier.name}").expandText(OMFLog().text(e.message)))
            return ""
        }

        val sourceFile = try {
            getSourceFilePath(pluginClass, pathToFiles, domain)
        } catch (e: FileNotFoundException) {
            OMFLogger.errorToSystemConsole(OMFLog().err("Error while getting source file for class: ${pluginClass.name}").expandText(OMFLog().text(e.message)))
            return ""
        }

        val parser = JavaParser()
        val compilationUnit: CompilationUnit = parseClass(sourceFile, parser)?: return ""

        val className = pluginClass.simpleName

        // Récupérer la déclaration de la classe
        val classDeclaration = compilationUnit
            .types
            .asSequence()
            .filterIsInstance<ClassOrInterfaceDeclaration>()
            .firstOrNull { it.nameAsString == className } ?: return ""

        // Récupérer la déclaration de la méthode spécifiée
        val methodDeclaration = classDeclaration
            .members
            .filterIsInstance<MethodDeclaration>()
            .firstOrNull { it.nameAsString == method.name } ?: return ""

        // Retourner la Javadoc de la méthode
        return methodDeclaration.javadocComment?.orElse(null)?.content ?: ""
    }

    @Throws(FileNotFoundException::class)
    fun getSourceFilePath(clazz: Class<*>, pathToFiles: String, domain: String): File {
        if (!clazz.name.contains(domain)) throw FileNotFoundException("File not part of the generated plugin: ${clazz.name}")
        if(clazz.name[0] == '[') throw FileNotFoundException("Array class not supported: ${clazz.name}")
        val basePackagePath = clazz.name.replace('.', File.separatorChar).replace(Regex("\\$.*$"), "")

        if (files.containsKey(basePackagePath)) {
            return files[basePackagePath]!!
        }

        // Recherche dans les sous-répertoires
        val possibleDirectories = File(pathToFiles).walkTopDown()
            .filter { it.isDirectory && it.name == "com" }

        // Recherche du fichier source
        possibleDirectories.forEach { srcDir ->
            val relativePath = basePackagePath.removePrefix("com${File.separatorChar}")

            var sourceFile = File(srcDir, "$relativePath.java")
            if (sourceFile.exists()) {
                files[basePackagePath] = sourceFile
                return sourceFile
            }

            sourceFile = File(srcDir, "$relativePath.kt")
            if (sourceFile.exists()) {
                files[basePackagePath] = sourceFile
                return sourceFile
            }
        }

        throw FileNotFoundException("Source file not found for class: ${clazz.name}")
    }


    private fun parseClass(sourceFile: File, parser: JavaParser): CompilationUnit? {
        if (parsedClass.containsKey(sourceFile)) return parsedClass[sourceFile]

        val compilationUnit = parser.parse(sourceFile).result.orElse(null) ?: return null
        parsedClass[sourceFile] = compilationUnit
        return compilationUnit
    }



}