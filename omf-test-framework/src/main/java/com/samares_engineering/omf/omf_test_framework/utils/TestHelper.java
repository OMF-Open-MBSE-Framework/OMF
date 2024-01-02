/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.PluginUtils;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.legacy_comparator.LegacyElementModelComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.filters.ElementFilter;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_test_framework.errors.OMFTestFrameworkException;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.assertNotNull;

public class TestHelper {
    public static boolean compareTestProjects(AbstractTestCase testCase) {
        Project initProject = testCase.getInitProject();
        Project oracleProject = testCase.getOracleProject();
        String testPackageName = testCase.getTestPackageName();
        TestLogger logger = testCase.getLoggerTest();

        Package testPackage = Finder.byNameRecursively().find(initProject, Package.class, testPackageName);
        Package resultPackage = Finder.byNameRecursively().find(oracleProject, Package.class, testPackageName);

        assertNotNull("Test package not found in InitProject: " + testPackageName, testPackage);
        assertNotNull("Test package not found in oracleProject: " + testPackageName, resultPackage);

        testCase.createNewProjectComparator("./logfile.txt"); // TODO: what is the purpose of this line?
        LegacyElementModelComparator comparator = new LegacyElementModelComparator();
        comparator.addFilter(new ElementFilter(testPackage, resultPackage));

        boolean result = false;

        try{
            result = comparator.comparePackages(testPackage, resultPackage);
        }catch (Exception e){
            logger.err("/!\\ ---- ERROR DURING TEST  ---- /!\\ \n");
            LegacyErrorHandler.handleException(e, false);
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

    public static OMFPlugin findTestedPluginInstance(Class<? extends OMFPlugin> pluginClass) throws OMFTestFrameworkException {
        try {
            return (OMFPlugin) PluginUtils.getPlugins().stream()
                    .filter(pluginClass::isInstance)
                    .findFirst().orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new OMFTestFrameworkException("Can't find tested plugin");
        }
    }
}

