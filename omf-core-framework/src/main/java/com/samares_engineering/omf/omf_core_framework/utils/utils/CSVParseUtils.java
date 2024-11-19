/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils;

import com.jidesoft.csv.CsvReader;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVParseUtils {
    public static File lastPath = null;

    private CSVParseUtils() {}

    public static List<List<String>> getParsedLines(String csvFilePath, char delimiter) throws FileNotFoundException, LegacyOMFException {
        CsvReader reader = new CsvReader(new FileReader(csvFilePath), delimiter);
        List<List<String>> parsedLines;
        try {
            parsedLines = reader.parse();
        } catch (IOException e) {
            throw new LegacyOMFException("Can't read .csv config file " + csvFilePath + ", make sure the file is in .csv " +
                    "format and uses " + delimiter + "as a delimiter", e, GenericException.ECriticality.CRITICAL);
        }
        try {
            reader.getReader().close();
        } catch (IOException e) {
            throw new LegacyOMFException("Problem when closing .csv config file " + csvFilePath, e,
                            GenericException.ECriticality.ALERT);
        }
        return parsedLines;
    }

    public static void setCurrentDirectory(JFileChooser fileChooser) {
        if (lastPath != null)
            fileChooser.setCurrentDirectory(lastPath);
    }

    public static void setDefaultPath(@CheckForNull File selectedFile) {
        lastPath = selectedFile.getParentFile();
    }

}
