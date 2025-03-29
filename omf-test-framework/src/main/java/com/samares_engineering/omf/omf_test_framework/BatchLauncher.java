/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework;

import com.nomagic.magicdraw.commandline.CommandLineAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_test_framework.formatter.JUnitResultFormatterAsRunListener;
import com.samares_engineering.omf.omf_test_framework.formatter.XMLJUnitResultFormatter;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatch;
import org.junit.internal.TextListener;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BatchLauncher implements CommandLineAction {

    public static final SaveTestModel confSaveTestModel = SaveTestModel.ANYWAY;
    public static ATestBatch currentBatch;
    private static final String REPORT_DIR = OMFUtils.getUserDir() + "\\..\\test-reports";
    private static final String XML_REPORT_DIR = REPORT_DIR + "\\TEST-XML";

    List<Class<? extends ATestBatch>> testBatches;

    public BatchLauncher(List<Class<? extends ATestBatch>> testBatches) {
        this.testBatches = testBatches;
    }

	public static boolean shallSaveModel(boolean result) {
        return shallSaveModel(confSaveTestModel, result);
    }


	public static boolean shallSaveModel(SaveTestModel savingConf, boolean result) {
		if(SaveTestModel.ANYWAY == savingConf)
			return true;
		if(SaveTestModel.NEVER == savingConf)
			return false;
        return SaveTestModel.IF_FAILED == savingConf && !result;
    }

    @Override
    public byte execute(String[] args) {
        try {
            Files.createDirectories(Paths.get(REPORT_DIR));
            Files.createDirectories(Paths.get(XML_REPORT_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JUnitCore core = new JUnitCore();
        try {
            core.addListener(
                    new TextListener(
                            new PrintStream(
                                    new File(REPORT_DIR, "TEST-OUTPUT.txt")
                            )
                    )
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        core.addListener(new TextListener(System.out));
        core.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
            @Override
            public void testStarted(Description description) throws Exception {
                formatter.setOutput(
                        new FileOutputStream(
                                new File(XML_REPORT_DIR, "TEST-" + description.getDisplayName() + ".xml")
                        )
                );
                super.testStarted(description);
            }
        });

        for (Class<? extends ATestBatch> testBatch : testBatches) {
            runTestBatch(core, testBatch);
        }

        return 0;
    }

    private static void runTestBatch(JUnitCore core, Class<? extends ATestBatch> testBatch) {
       try {
            currentBatch = testBatch.getDeclaredConstructor().newInstance();

            currentBatch.startBatch(); //if Project batch => will load the project (local or twc)
            Result result = core.run(new Computer(), testBatch);
            currentBatch.endBatch(shallSaveModel(result.wasSuccessful())); //if ProjectBatch => will close/commit the project

       } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
           throw new RuntimeException(e);
       }
    }
}
