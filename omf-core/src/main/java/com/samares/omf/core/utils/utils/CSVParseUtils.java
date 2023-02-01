/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.utils;

import com.jidesoft.csv.CsvReader;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.errors.exceptions.OMFException;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.io.*;
import java.util.List;

public class CSVParseUtils {
    public static File lastPath = null;

    private CSVParseUtils() {}

    public static List<List<String>> getParsedLines(String csvFilePath, char delimiter) throws FileNotFoundException, OMFException {
        CsvReader reader = new CsvReader(new FileReader(csvFilePath), delimiter);
        List<List<String>> parsedLines;
        try {
            parsedLines = reader.parse();
        } catch (IOException e) {
            throw new OMFException("Can't read .csv config file " + csvFilePath + ", make sure the file is in .csv " +
                    "format and uses " + delimiter + "as a delimiter", e, GenericException.ECriticality.CRITICAL);
        }
        try {
            reader.getReader().close();
        } catch (IOException e) {
            throw new OMFException("Problem when closing .csv config file " + csvFilePath, e,
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
