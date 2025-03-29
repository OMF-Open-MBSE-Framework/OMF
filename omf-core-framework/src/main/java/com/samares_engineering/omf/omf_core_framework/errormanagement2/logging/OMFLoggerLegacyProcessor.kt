package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.TypeElement
import javax.lang.model.SourceVersion

@SupportedAnnotationTypes("com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.GenerateLegacyMethods")
@AutoService(Processor::class)
class OMFLoggerLegacyProcessor : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Rechercher l'annotation @GenerateLegacyMethods
        val elements = roundEnv.getElementsAnnotatedWith(GenerateLegacyMethods::class.java)

        for (element in elements) {
            // Créer la classe des méthodes legacy
            val className = "OMFLogger2LegacyMethodsGenerated"
            val fileName = "$className.kt"

            val legacyClassBuilder = TypeSpec.objectBuilder(className)
                .addModifiers(KModifier.PUBLIC)
                .addKdoc("This class is auto-generated to provide legacy methods for OMFLogger2.")

            // Ajouter les méthodes héritées pour chaque cas
            val legacyMethods = listOf(
                Triple("warnToUIConsole", "warning", "toUI"),
                Triple("errorToNotification", "error", "toNotification"),
                Triple("successToSystemConsole", "success", "toSystem"),
                Triple("logToAll", "log", "toAll"),
                Triple("logWarningToAll", "warning", "toAll"),
                Triple("logErrorToAll", "error", "toAll")
            )

            for ((methodName, logMethod, targetMethod) in legacyMethods) {
                val functionSpec = FunSpec.builder(methodName)
                    .addModifiers(KModifier.PUBLIC)
                    .addParameter("message", String::class)
                    .addAnnotation(JvmStatic::class)
                    .addCode("OMFLogger2.$targetMethod().$logMethod(message)\n")
                    .build()

                legacyClassBuilder.addFunction(functionSpec)
            }

            // Créer le fichier Kotlin
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"] ?: return false
            val fileSpec = FileSpec.builder("com.samares_engineering.omf.omf_core_framework.errormanagement2.logging", fileName)
                .addType(legacyClassBuilder.build())
                .build()

            fileSpec.writeTo(File(kaptKotlinGeneratedDir))
        }

        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}
