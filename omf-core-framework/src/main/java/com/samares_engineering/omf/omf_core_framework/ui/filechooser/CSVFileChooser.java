/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.filechooser;


import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.utils.utils.CSVParseUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CSVFileChooser {

    private File selectedFile;
    private JFileChooser fileChooser;

    private CSVFileChooser() {
        this.fileChooser = new JFileChooser();
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.setDialogTitle("Choose the configuration file (csv) file...");
        javax.swing.filechooser.FileFilter filter = new CSVFilter();
        this.fileChooser.setFileFilter(filter);
    }

    public static CSVFileChooser getInstance() {
        return MFileChooserHolder.instance;
    }

    public void open() {
        try {
            CSVParseUtils.setCurrentDirectory(fileChooser);
            int result = this.fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedFile = this.fileChooser.getSelectedFile();
                CSVParseUtils.setDefaultPath(selectedFile);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                this.selectedFile = null;
            }
        } catch (HeadlessException e) {
            throw new OMFCriticalException("Keyboard and Mouse Required", e);
        }
    }

    public File getSelectedFile() {
        return this.selectedFile;
    }

    private static class MFileChooserHolder {
        private static final CSVFileChooser instance = new CSVFileChooser();
    }

    static class CSVFilter extends javax.swing.filechooser.FileFilter {
        String description = ".csv file";

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String name = f.getName().toLowerCase();
            if (name.endsWith(".csv")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

}
