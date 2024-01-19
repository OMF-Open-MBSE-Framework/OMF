package com.samares_engineering.omf.omf_core_framework.model_comparators.logger;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

public class LoggerUtils {

    public static String getElementName(Element element) {
        if (element instanceof NamedElement) {
            String type = element.getHumanType();
            String name = ((NamedElement) element).getName();

            return "(" + type + ") " + name;
        } else {
            return "";
        }
    }
}
