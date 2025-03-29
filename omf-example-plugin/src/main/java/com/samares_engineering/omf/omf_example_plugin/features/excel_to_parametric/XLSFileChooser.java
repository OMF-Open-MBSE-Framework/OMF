package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.utils.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class XLSFileChooser {

    private File selectedFile;
    private final JFileChooser fileChooser;

    private XLSFileChooser() {
        this.fileChooser = new JFileChooser();
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.setDialogTitle("Choose an Excel (xls) file...");
        javax.swing.filechooser.FileFilter filter = new ExcelFilter();
        this.fileChooser.setFileFilter(filter);
    }

    public static XLSFileChooser getInstance() {
        return MFileChooserHolder.instance;
    }

    public void open() {
        try {
            FileUtils.setCurrentDirectory(fileChooser);
            int result = this.fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedFile = this.fileChooser.getSelectedFile();
                FileUtils.setDefaultPath(selectedFile);
            } else {
                if (result == JFileChooser.CANCEL_OPTION) {
                    this.selectedFile = null;
                }
            }
        } catch (HeadlessException ex) {
            OMFLogger.logToSystemConsole("Keyboard and Mouse Required", OMFLogLevel.ERROR);
            OMFLogger.err(ex);
        }
    }

    public File getSelectedFile() {
        return this.selectedFile;
    }

    private static class MFileChooserHolder {
        private static final XLSFileChooser instance = new XLSFileChooser();
    }

    static class ExcelFilter extends javax.swing.filechooser.FileFilter {
        String description = ".xls/xlsx file";

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String name = f.getName().toLowerCase();
            return name.endsWith(".xls") || name.endsWith(".xlsx");
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

}
