package com.samares_engineering.omf.omf_gradle_plugin.tasks

import com.samares_engineering.omf.omf_gradle_plugin.OmfGradleUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar

import java.nio.file.Path

abstract class PackagePlugin extends DefaultTask {
    @Input
    abstract Property<String> getHumanVersion()

    @Input
    abstract Property<String> getMyPluginName()

    @Input
    abstract Property<String> getMyPackage()

    @Input
    abstract Property<String> getMyPluginId()

    @Input
    abstract Property<String> getMyPluginMainClass()

    @Input
    abstract Property<String> getPluginPackageFolderName()

    @Input
    abstract Property<String> getPluginDeliveryName()

    @Input
    abstract SetProperty<File> getResolvedArtifacts()

    @Input
    @Optional
    abstract Property<String> getPluginUnderTestId()

    @Input
    @Optional
    abstract Property<String> getPluginUnderTestName()

    @Internal
    boolean isTestPlugin() {
        pluginUnderTestId.isPresent() && pluginUnderTestName.isPresent()
    }

    @Internal
    String getInternalVersion() {
        OmfGradleUtils.generateInternalAndResourceVersion(humanVersion.get())[0]
    }

    @Internal
    String getResourceVersion() {
        OmfGradleUtils.generateInternalAndResourceVersion(humanVersion.get())[1]
    }

    @Internal
    Jar getOutputArtifact() {
        isTestPlugin() ? project.testJar : project.jar
    }

    @Internal
    String getDistFolder() {
        isTestPlugin() ? 'src/test/resources/dist' : 'src/main/resources/dist'
    }

    @Internal
    String getBuildDistFolder() {
        "$project.buildDir/${pluginPackageFolderName.get()}"
    }

    @Internal
    String getBuildDistPluginFolder() {
        "$buildDistFolder/plugins/${myPackage.get()}"
    }

    @TaskAction
    void executeTask() {
        //TODO Dist folder needs a redesign
        project.copy {
            from "$distFolder/template/plugin"
            into "$buildDistPluginFolder"
        }

        project.copy {
            from "$distFolder/template/install"
            into "$buildDistFolder"
        }

        project.copy {
            from "LICENSE"
            into buildDistFolder
            rename { filename -> filename.replace("LICENSE", "EULA_SAMARES.txt") }
        }

        project.copy {
            from outputArtifact
            into buildDistPluginFolder
        }

        project.copy {
            from resolvedArtifacts.get()
            into "$buildDistPluginFolder/lib"
        }

        project.fileTree(dir: "$buildDistFolder", include: "**/*${myPackage}*/**").each { file ->
            file.renameTo(project.file(file.getAbsolutePath().replace("${myPackage}", "${myPackage.get()}")))
        }

        generatePluginXml()
        generateDescriptorFile()
    }

    private void generatePluginXml() {
        String pluginLibrariesLines = getPluginLibrariesLines()

        project.copy {
            from "$distFolder/template/descriptors/plugin/plugin.xml"
            filter { it.replace('<!-- START AUTO-GENERATED -->', '<!-- START AUTO-GENERATED -->'
                        + System.lineSeparator() + System.lineSeparator() + pluginLibrariesLines)
            }

            filter { it.replace('${plugin.name}', myPluginName.get()) }
            filter { it.replace('${human.version}', humanVersion.get()) }
            filter { it.replace('${internal.version}', internalVersion) }
            filter { it.replace('${plugin.id}', myPluginId.get()) }
            filter { it.replace('${plugin.package}', myPackage.get()) }
            filter { it.replace('${plugin.main}', myPluginMainClass.get()) }
            if (isTestPlugin()) {
                filter { it.replace('${plugin.undertest.id}', pluginUnderTestId.get()) }
                filter { it.replace('${plugin.undertest.name}', pluginUnderTestName.get()) }
            }
            into buildDistPluginFolder
        }
    }

    private String getPluginLibrariesLines() {
        String pluginLibraries = ''
        project.fileTree(dir: buildDistPluginFolder, include: '*.jar').each { file ->
            pluginLibraries += '\t\t<library name=\"' + file.name + '\"/>' + System.lineSeparator()
        }
        project.fileTree(dir: buildDistPluginFolder, include: 'lib/*.jar').each { file ->
            pluginLibraries += '\t\t<library name=\"lib/' + file.name + '\"/>' + System.lineSeparator()
        }
        pluginLibraries
    }

    private void generateDescriptorFile() {
        def descriptorFileDestinationDir = "$buildDistFolder/data/resourcemanager"
        def templateDescriptor = findResourceDescriptor()
        def generatedInstallLines = generateDescriptorInstallLines(descriptorFileDestinationDir + templateDescriptor.name)
        project.copy {
            from templateDescriptor
            filter { it.replace(
                    '<!-- START AUTO-GENERATED -->',
                    '<!-- START AUTO-GENERATED -->' + System.lineSeparator()
                            + System.lineSeparator() + generatedInstallLines
            )}
            filter { it.replace('${human.version}', humanVersion.get()) }
            filter { it.replace('${internal.version}', internalVersion) }
            filter { it.replace('${resource.version}', resourceVersion) }
            filter { it.replace('${plugin.id}', myPluginId.get()) }
            filter { it.replace('${build.timestamp}', project.buildTimestamp) }
            filter { it.replace('${plugin.name}', myPluginName.get()) }
            filter { it.replace('${plugin.archiveFileName}', pluginDeliveryName.get()) }
            into descriptorFileDestinationDir
        }
    }

    private File findResourceDescriptor() {
        def resourceManagerFolder = "$distFolder/template/descriptors/resourcemanager"
        def descriptorCandidates = project.fileTree(resourceManagerFolder)
        if (descriptorCandidates.isEmpty()) {
            throw new GradleException("No resource descriptor file found in $resourceManagerFolder")
        }
        if (descriptorCandidates.size() > 1) {
            print("Multiple resource descriptor files found in $resourceManagerFolder. Using the first one.")
        }
        return descriptorCandidates.first()
    }

    private String generateDescriptorInstallLines(String generatedDescriptorFilePath) {
        def autoGeneratedLines = ''
        def rootPath = project.file("$buildDistFolder").toPath()
        // We need to add all the files in the distribution folder to the descriptor file
        project.fileTree(dir: "$project.buildDir/${pluginPackageFolderName.get()}").each { File file ->
            autoGeneratedLines += PackagePlugin.generateDescriptorInstallLineForFile(file, rootPath)
        }
        // We add the descriptor file itself separately as it is not already created in the distribution folder
        autoGeneratedLines += generateDescriptorInstallLineForFile(project.file(generatedDescriptorFilePath), rootPath)
        autoGeneratedLines
    }

    private static String generateDescriptorInstallLineForFile(File fileToCopy, Path rootPath) {
        String relativePath = relativizePath(fileToCopy, rootPath)
        '\t\t<file from=\"' + relativePath + '\" to=\"' + relativePath + '\"/>' + System.lineSeparator()
    }

    private static String relativizePath(File file, Path rootPath) {
        rootPath.relativize(file.toPath()).toString()
    }
}
