/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectcomparator;

import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.samares.omf.core.utils.profile.Profile;

public class ElementFilter implements ModelComparatorFilter {
    @Override
    public boolean needToCompare(Element element) {
        //Each attributes of stereotype
        if(element instanceof Diagram || element.getOwner() instanceof Diagram)
            return false;
        if(Profile.getInstance().getMagicDraw().legend().is(element) || Profile.getInstance().getMagicDraw().legend().is(element.getOwner()))
            return false;
        if (element instanceof TaggedValue) {
            TaggedValue taggedValue = (TaggedValue) element;
            String tagName = taggedValue.getTagDefinition().getName();
            switch (tagName){
                case "Author":
                case "id":
                case "Creation Date":
                case "Modification date":
                case "Last modified by":
                case "query":
                case "useInSelectionDialogs":
                    return false;
            }
        }
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
                return false;
            default:
                if (elementA instanceof Diagram)
                    return this.needToCompareDiagramAttribute(attributeName, elementA, elementB);
                if (elementA.getOwner() instanceof Diagram)
                    return this.needToCompareDiagramAttribute(attributeName, elementA, elementB);

        }
        return ModelComparatorFilter.super.needToCompareAttribute(attributeName, elementA, elementB);
    }

    private boolean needToCompareDiagramAttribute(String attributeName, Element elementA, Element elementB) {
        return true;
    }
}
