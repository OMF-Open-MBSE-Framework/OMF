package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.codegeneration
import java.io.File

fun main() {
    // Chemins des fichiers
    val templatePath = "C:/Workspace/Plugins/OMF_Private/omf-core-framework/src/main/resources/template/OMFLog.kt"
    val resultFilePath = "C:/Workspace/Plugins/OMF_Private/omf-core-framework/src/main/resources/template/OMFLog2.kt"

    // Charger le fichier source
    val templateFile = File(templatePath)
    if (!templateFile.exists()) {
        println("Le fichier template n'existe pas : $templatePath")
        return
    }
    val sourceCode = templateFile.readText()

    // Créer ou ouvrir le fichier cible
    val resultFile = File(resultFilePath)
    if (!resultFile.exists()) {
        println("Le fichier cible n'existe pas, création de $resultFilePath")
        resultFile.createNewFile()
    }

    // Identifier les méthodes annotées avec @OMFLogEquivalent
    val omfLogEquivalentPattern = Regex("@OMFLogEquivalent\\s+fun\\s+(\\w+)\\(([^)]*)\\):\\s*OMFLog")
    val matches = omfLogEquivalentPattern.findAll(sourceCode)

    // Créer une liste des méthodes équivalentes
    val generatedMethods = StringBuilder()
    matches.forEach { match ->
        val methodName = match.groupValues[1]
        val params = match.groupValues[2]

        // Identifier les paramètres supplémentaires (autres que le String ou OMFLog)
        val paramList = params.split(",").map { it.trim() }
        val additionalParams = paramList.filterNot { it.startsWith("string:") || it.startsWith("log:") }

        // Générer les paramètres pour la méthode
        val paramString = additionalParams.joinToString(", ") { it }
        val paramNames = additionalParams.joinToString(", ") { it.split(":")[0].trim() }

        // Générer la méthode équivalente
        val newMethod = if (additionalParams.isEmpty()) {
            """
            fun $methodName(log: OMFLog): OMFLog {
                return $methodName(log.toString())
            }
            """.trimIndent()
        } else {
            """
            fun $methodName(log: OMFLog, $paramString): OMFLog {
                return $methodName(log.toString(), $paramNames)
            }
            """.trimIndent()
        }
        generatedMethods.appendLine(newMethod)
    }

    // Insérer les méthodes générées après le commentaire //<--GENERATED METHOD -->
    val insertionPoint = sourceCode.indexOf("//<--GENERATED METHOD -->")
    if (insertionPoint != -1) {
        val beforeGeneratedBlock = sourceCode.substring(0, insertionPoint + "//<--GENERATED METHOD -->".length)
        val afterGeneratedBlock = sourceCode.substring(insertionPoint + "//<--GENERATED METHOD -->".length)
        val updatedSourceCode = beforeGeneratedBlock +
                "\n\n    // Méthodes générées automatiquement\n${generatedMethods.toString()}\n" +
                afterGeneratedBlock

        // Écrire le fichier complet dans le fichier cible
        resultFile.writeText(updatedSourceCode)

        println("Fichier généré : $resultFilePath")
    } else {
        println("Le commentaire //<--GENERATED METHOD --> n'a pas été trouvé dans le fichier source.")
    }
}
