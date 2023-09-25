package com.samares_engineering.omf.omf_public_features.patterncreation.exception;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;

public class NoPatternFoundOnTemplateElementException extends OMFException {

    public NoPatternFoundOnTemplateElementException(Element templateElement) {
        super("No pattern found on template element " + templateElement.getHumanName() + " (" + templateElement.getHumanType() + ")", ECriticality.ALERT);
    }
}
