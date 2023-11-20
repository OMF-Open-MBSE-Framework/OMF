package com.samares_engineering.omf.omf_public_features.testGeneration.generatedCode;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.testGeneration.utils.OptionsBaseline;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class ReInitOptionsBaseline {
  public static Map<String, List<OptionsBaseline>> snapshot() {
    return Map.of(
    );
  }

  private static Element findElementByID(String id) {
    NamedElement foundElement = (NamedElement) OMFUtils.getProject().getElementByID(id);
    Assert.assertNotNull(" No element found with ID " + id, foundElement);
    return foundElement;
  }
}
