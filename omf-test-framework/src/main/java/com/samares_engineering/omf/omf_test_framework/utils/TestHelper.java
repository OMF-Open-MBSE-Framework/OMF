/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.PluginUtils;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_test_framework.errors.OMFTestFrameworkException;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.filters.ElementFilter;
import com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.ElementModelComparator;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;
import org.apache.commons.lang.StringUtils;

import static org.junit.Assert.assertNotNull;

public class TestHelper {
    public static boolean compareTestProjects(Element initProject, Element oracleProject, String testPackageName) {
        Package testPackage = Finder.byNameRecursively().find(initProject, Package.class, testPackageName);
        Package resultPackage = Finder.byNameRecursively().find(oracleProject, Package.class, testPackageName);

        assertNotNull("Not Found Test package in InitProject: " + testPackageName, testPackage);
        assertNotNull("Not Found Test package in oracleProject: " + testPackageName, resultPackage);

        ElementModelComparator comparator = new ElementModelComparator();
        comparator.addFilter(new ElementFilter());

        boolean result = false;

        try{
            result = comparator.comparePackages(testPackage, resultPackage);
        }catch (Exception e){
            ColorPrinter.err("/!\\ ---- ERROR DURING TEST  ---- /!\\ \n");
            OMFErrorHandler.handleException(e, false);
        }

        if(result)
            ColorPrinter.success("**** PROJECT COMPARE: PASSED ***" + "\n " +
                    comparator.getDiffInfo());
        else
            ColorPrinter.err("**** PROJECT COMPARE: FAILED ***" + "\n " +
                    comparator.getDiffInfo());

        OMFLogger.getInstance().log("PROJECT COMPARE: " + result + "\n " +
                comparator.getDiffInfo(), null, OMFLogLevel.INFO);
        return result;
    }


    public static boolean compareTestProjects(AbstractTestCase testCase) {
        Project initProject = testCase.getInitProject();
        Project oracleProject = testCase.getOracleProject();
        String testPackageName = testCase.getTestPackageName();
        TestLogger logger = testCase.getLoggerTest();

        Package testPackage = Finder.byNameRecursively().find(initProject, Package.class, testPackageName);
        Package resultPackage = Finder.byNameRecursively().find(oracleProject, Package.class, testPackageName);

        assertNotNull("Test package not found in InitProject: " + testPackageName, testPackage);
        assertNotNull("Test package not found in oracleProject: " + testPackageName, resultPackage);

        testCase.createNewProjectComparator("./logfile.txt");
        ElementModelComparator comparator = new ElementModelComparator();
        comparator.addFilter(new ElementFilter());

        boolean result = false;

        try{
            result = comparator.comparePackages(testPackage, resultPackage);
        }catch (Exception e){
            logger.err("/!\\ ---- ERROR DURING TEST  ---- /!\\ \n");
            OMFErrorHandler.handleException(e, false);
        }

        if(result)
            logger.success("**** PROJECT COMPARE: PASSED ***" + "\n " +
                    comparator.getDiffInfo());
        else
            logger.err("**** PROJECT COMPARE: FAILED ***" + "\n " +
                    comparator.getDiffInfo());

        logger.log("PROJECT COMPARE: " + result + "\n " +
                comparator.getDiffInfo());

        return result;
    }

    public static boolean compareStringsNoCaseNoSpace(String s1, String s2) {
        return StringUtils.deleteWhitespace(s1).equalsIgnoreCase( StringUtils.deleteWhitespace(s2));
    }

    public static APlugin findTestedPluginInstance(Class<? extends APlugin> pluginClass) throws OMFTestFrameworkException {
        try {
            return (APlugin) PluginUtils.getPlugins().stream()
                    .filter(pluginClass::isInstance)
                    .findFirst().orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new OMFTestFrameworkException("Can't find tested plugin", GenericException.ECriticality.CRITICAL);
        }
    }
}

