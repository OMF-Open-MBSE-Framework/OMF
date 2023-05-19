/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.filters;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

public class FunctionFilter implements ModelComparatorFilter {
    @Override
    public boolean needToCompare(Element element) {
        if (!(element instanceof NamedElement))
            return false;
        NamedElement namedElement = (NamedElement) element;

        return false;
    }

    @Override
    public boolean needToCompareAttribute(String attributeName, Element element, Element element1) {
        if(attributeName.equals("id"))
            return false;
        return ModelComparatorFilter.super.needToCompareAttribute(attributeName, element, element1);
    }
}
