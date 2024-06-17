package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.testGeneration;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsBaseline;
import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;

import java.util.List;
import java.util.Map;

public class CreateProxyPort extends AbstractTestCase {
  @Override
  public void initVariables() {
    testCaseID = "_2021x_2_302b0611_1670957645992_118224_3249"; // http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957645992_118224_3249
    testPackageName = "1 create proxy port";
  }

  @Override
  public void initOptions() {
    Map<String, List<OptionsBaseline>> initBaseline = Map.of(
    );
    OptionsBaseline.initBaseline(initBaseline);
  }

  @Override
  public void testAction() {
    Element owner = findTestedElementByID("_2021x_2_302b0611_1670957597348_883331_3236"); // http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957597348_883331_3236
    Element diagramElement = findTestedElementByID("_2021x_2_302b0611_1670957709771_148945_3383"); // http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957709771_148945_3383
    if (diagramElement != null) {
      openDiagram("_2021x_2_302b0611_1670957709771_148945_3383");
    }

    Port p1 = OMFUtils.getProject().getElementsFactory().createPortInstance();

    Profile profile;
    Stereotype stereotype;
    profile = StereotypesHelper.getProfile(OMFUtils.getProject(), "SysML");
    stereotype = StereotypesHelper.getStereotype(OMFUtils.getProject(), "ProxyPort", profile);
    StereotypesHelper.addStereotype(p1, stereotype);

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
