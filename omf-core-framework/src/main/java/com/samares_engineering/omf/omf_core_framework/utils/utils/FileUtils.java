/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    private FileUtils() {}
    public static File lastPath = null;


    public static void setCurrentDirectory(JFileChooser fileChooser) {
        if (lastPath == null) {
            return;
        }
        fileChooser.setCurrentDirectory(lastPath);
    }
    public static void setDefaultPath(@CheckForNull File selectedFile) {
        lastPath = selectedFile.getParentFile();
    }


    public static void unzipFile(String zipFilePath, String outputDirectoryPath) {
        File dir = new File(outputDirectoryPath);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)
        ) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputDirectoryPath + File.separator + fileName);
                //create directories for sub directories in zip
                int len;
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
        } catch (IOException e) {
            throw new OMFCriticalException("Error while unzipping file", e);
        }
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static File searchFile(File file, String search) {
        if (file.isDirectory()) {
            File[] arr = file.listFiles();
            for (File f : arr) {
                File found = searchFile(f, search);
                if (found != null)
                    return found;
            }
        } else {
            if (file.getName().equals(search)) {
                return file;
            }
        }
        return null;
    }

    public static void copyFilesInto(ArrayList<String> l_files, String destination) throws IOException {
        l_files.stream().forEach(file -> copyFileInto(file, destination));
    }

    public static void copyFileInto(String elementToCopy, String destination) {
        Path destionationPath = Paths.get(destination);
        Path originalPath = new File(elementToCopy).toPath();

        try (BufferedReader reader = Files.newBufferedReader(originalPath);
             BufferedWriter writer = Files.newBufferedWriter(destionationPath)) {

            Files.copy(originalPath, destionationPath, StandardCopyOption.REPLACE_EXISTING);

            try (Stream<Path> walk = Files.walk(originalPath)) {
                walk.forEach(sourcePath -> copyFile(sourcePath, originalPath, destionationPath));
            } catch (Exception e) {
                throw new OMFLogException("Error while coping a file");
            }
        } catch (Exception e) {
            throw new OMFLogException("Error while coping a file");
        }
    }

    private static void copyFile(Path sourcePath, Path originalPath, Path destionationPath) {
        try {
            Path targetPath = destionationPath.resolve(originalPath.relativize(sourcePath));
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            OMFLogger.infoToSystemConsole("Successfully copied " + sourcePath + " to " + targetPath);
        } catch (IOException ex) {
            throw new OMFLogException("Error while coping" + sourcePath);
        }
    }


    public static boolean containsIgnoreCase(String string, String pattern) {
        return string.toLowerCase().contains(pattern.toLowerCase());
    }

    public static String toUpperSnakeCase(String str) {
        str = str.toUpperCase();
        str = str.replaceAll("^([0-9]+)", "_$1");
        str = str.replaceAll("-", "_");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("=", "_");
        str = str.replaceAll("/", "_");
        str = str.replaceAll("\\\\", "_");
        str = str.replaceAll("\\(", "_");
        str = str.replaceAll("\\)", "_");
        str = str.replaceAll("\\.", "_");

        return str;
    }

    public static void createFolder(String tempFolderPath) throws IOException {
        Files.createDirectories(Paths.get(tempFolderPath));
    }

    public static void moveFileTo(String tempFolderPath, String destinationPath) throws IOException {
        Files.move(Paths.get(tempFolderPath), Paths.get(destinationPath));
    }

    public static boolean isEmpty(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return false;
        }

        try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
            return !directory.iterator().hasNext();
        }

    }

}
