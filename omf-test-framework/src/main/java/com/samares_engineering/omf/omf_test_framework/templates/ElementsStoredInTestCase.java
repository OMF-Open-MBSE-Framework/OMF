package com.samares_engineering.omf.omf_test_framework.templates;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.HashMap;
import java.util.Map;

public class ElementsStoredInTestCase {

    private Map<String, String> elements; //key = name, value = id

    public ElementsStoredInTestCase() {
        this.elements = new HashMap<>();
    }

    public boolean containsElement(String name) {
        return this.elements.containsKey(name);
    }

    public void storeElement(Element element, String name) {
        if (elements.containsKey(name)) {
            // TODO : how deal with that ? allow ? warning ? error ?
        }
        this.elements.put(name, element.getID());
    }

    public Element getStoredElement(String name) {
        return (Element) OMFUtils.getProject().getElementByID(this.elements.get(name));
    }
}
