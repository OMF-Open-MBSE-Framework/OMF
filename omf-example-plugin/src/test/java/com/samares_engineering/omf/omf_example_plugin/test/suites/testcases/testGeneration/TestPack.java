package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.testGeneration;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.utils.OptionsBaseline;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestPack extends AbstractTestCase {
  @Override
  public void initVariables() {
    testCaseID = "_2021x_2_da1032a_1676023563249_420259_2862"; // http://localhost:9850/refmodel/?ID=_2021x_2_da1032a_1676023563249_420259_2862
    testPackageName = "TestPack";
  }

  @Override
  public void initOptions() {
    Map<String, List<OptionsBaseline>> initBaseline = Map.of(
    	 "env.options.omf.plugin", Arrays.asList(
    		 new OptionsBaseline(Boolean.class, "Activate OMF Automations", true),
    		 new OptionsBaseline(Boolean.class, "BooleanField", true),
    		 new OptionsBaseline(Boolean.class, "BooleanField 2", true),
    		 new OptionsBaseline(String.class, "Automation organizer configuration file path", "C:\\Users\\Calliope\\IdeaProjects\\samaresmbseframework\\omf-example-plugin\\build\\install/plugins/com.samares-engineering.omf.plugin/resources/organizer_config.csv"),
    		 new OptionsBaseline(Boolean.class, "Activate automatic element organization", true),
    		 new OptionsBaseline(String.class, "Automation type to instance configuration file path", "C:\\Users\\Calliope\\IdeaProjects\\samaresmbseframework\\omf-example-plugin\\build\\install/plugins/com.samares-engineering.omf.plugin/resources/instance_config.csv"),
    		 new OptionsBaseline(Boolean.class, "Activate automatic instance stereotype application", true),
    		 new OptionsBaseline(Element.class, "Test profile property", findElementByID("_11_5EAPbeta_be00301_1147424179914_458922_958")) // http://localhost:9850/refmodel/?ID=_11_5EAPbeta_be00301_1147424179914_458922_958
    	 )
    );
    OptionsBaseline.initBaseline(initBaseline);
  }

  @Override
  public void testAction() {
    Element owner = findTestedElementByID("_2021x_2_da1032a_1676023576223_970263_2863"); // http://localhost:9850/refmodel/?ID=_2021x_2_da1032a_1676023576223_970263_2863
    openDiagram("_2021x_2_da1032a_1680100753636_268670_2819"); // http://localhost:9850/refmodel/?ID=_2021x_2_da1032a_1680100753636_268670_2819

    Port p1 = OMFUtils.currentProject.getElementsFactory().createPortInstance();

    p1.setOwner(owner);
    p1.setName("p1");
  }

  @Override
  public void reInitEnvOptions() {
    OptionsBaseline.reInitBaseline();
  }

  @Override
  public void verifyResults() {
  }
}
