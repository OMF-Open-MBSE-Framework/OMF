/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectcomparator.model_comparators.filters;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

public class ElementFilter implements ModelComparatorFilter {
    @Override
    public boolean needToCompare(Element element) {
        //Each attributes of stereotype
        if (element instanceof Diagram || element.getOwner() instanceof Diagram)
            return false;
        if (Profile.getInstance().getMagicDraw().legend().is(element) || Profile.getInstance().getMagicDraw().legend().is(element.getOwner()))
            return false;
        return true;
    }

    @Override
    public boolean needToCompareAttribute(String attributeName, Element elementA, Element elementB) {
        switch (attributeName) {
            case "id":
            case "creationDate":
            case "modificationDate":
            case "owningPackage":
            case "URI":
            case "owner":
            case "query":
            case "useInSelectionDialogs":
            case "redefinitionContext":
            case "namespace":
            case "featuringClassifier":
            case "classifier":
            case "UMLClass":
                return false;
            default:
                return true;

        }
    }
}
