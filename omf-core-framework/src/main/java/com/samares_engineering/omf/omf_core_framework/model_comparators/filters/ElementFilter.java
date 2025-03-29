/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.model_comparators.filters;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

public class ElementFilter implements ModelComparatorFilter {
    private Element scopeA;
    private Element scopeB;

    public ElementFilter(Element scopeA, Element scopeB) {
        this.scopeA = scopeA;
        this.scopeB = scopeB;
    }

    public ElementFilter() {
        this.scopeA = null;
        this.scopeB = null;
    }

    @Override
    public boolean needToCompare(Element element) {
        //Each attributes of stereotype
        if (element instanceof Diagram || element.getOwner() instanceof Diagram)
            return false;
        if (Profile.getInstance().getMagicDraw().legend().is(element) || Profile.getInstance().getMagicDraw().legend().is(element.getOwner()))
            return false;
        if (scopeA != null && !isInScope(scopeA, element) && scopeB != null && !isInScope(scopeB, element)) {
            return false;
        }
        return true;
    }

    private boolean isInScope(Element scope, Element element) {
        Element ancestor = element;
        do {
            if (ancestor == scope) {
                return true;
            }
            ancestor = ancestor.getOwner();
        } while (ancestor.getOwner() != null);
        return false;
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
            case "member":
            case "ownedMember":
            case "packagedElement":
            case "attribute":
            case "ownedAttribute":
            case "feature":
            case "role":
            case "nestedClassifier":
            case "visibility":
                return false;
            default:
                return true;
        }
    }
}
