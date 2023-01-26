package com.samares.omf.core.utils;

import com.jidesoft.csv.CsvReader;
import com.samares.omf.core.utils.errorManagement.exceptions.GenericException;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVParseUtils {
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
}
