/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectcomparator;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomModelComparator extends ElementModelComparator {

    public boolean compareModels(Project project1, Project project2) {
        List model1 = project1.getModels();
        List model2 = project2.getModels();
        return compareModels(model1, model2);
    }
    public boolean compareModels(Package subModelRoot1, Package subModelRoot2) {
        return compareModels(Collections.singleton(subModelRoot1), Collections.singleton(subModelRoot2));
    }

    @Override
    public String getDiffInfo() {
        return getCustomDiffInfo();
    }

    public String getCustomDiffInfo(){
        StringBuilder sBuilder = new StringBuilder();
        Entry entry;
        if (!getAdded().isEmpty()) {
            sBuilder.append("New elements:\n");

            for (Entry value : getAdded()) {
                entry = value;
                sBuilder.append("\t");
                toFullName(entry.getElement(), sBuilder);
                sBuilder.append("\n\t\t");
                sBuilder.append(" property=");
                sBuilder.append(entry.getProperty());
                sBuilder.append("\n\t\t");
                sBuilder.append(" owner in source 1=");
                toFullName(entry.getOwner1(), sBuilder);
                sBuilder.append("\n\t\t");
                sBuilder.append(" owner in source 2=");
                toFullName(entry.getOwner2(), sBuilder);
                sBuilder.append("\n");
            }
        }

        if (!getRemoved().isEmpty()) {
            sBuilder.append("Removed elements:\n");

            for (Entry value : getRemoved()) {
                entry = value;
                sBuilder.append("\t");
                toFullName(entry.getElement(), sBuilder);
                sBuilder.append(" ||| property=");
                sBuilder.append(entry.getProperty());
                sBuilder.append(" owner in source 1=");
                toFullName(entry.getOwner1(), sBuilder);
                sBuilder.append(" owner in source 2=");
                toFullName(entry.getOwner2(), sBuilder);
                sBuilder.append("|||");
                sBuilder.append("\n");
            }
        }

        if (!getChanged().isEmpty()) {
            sBuilder.append("Changed elements:\n");

            for (Map.Entry<Element, Diff> elementDiffEntry : getChanged().entrySet()) {
                Map.Entry map = (Map.Entry) elementDiffEntry;
                sBuilder.append("\t");
                toFullName((Element) map.getKey(), sBuilder);
                sBuilder.append("\n");
                Diff diff = (Diff) map.getValue();

                for (String var6 : diff.getChanges()) {
                    sBuilder.append("\t\t").append(var6).append("\n");
                }
            }
        }

        return sBuilder.toString();
    }

    private static void toFullName(Element element, StringBuilder sBuilder) {
        sBuilder.append(element.getClassType().getSimpleName())
                .append(" ")
                .append(element instanceof NamedElement? ((NamedElement) element).getName(): "")
                .append("\n\t");
        toQualifiedName(element, sBuilder);
    }

    private static void toQualifiedName(Element element, StringBuilder stringBuilder) {
        for(int size = stringBuilder.length(); element != null; element = element.getOwner()) {
            if (stringBuilder.length() > size) {
                stringBuilder.insert(size, "::");
            }

            String res = "";
            res += element instanceof NamedElement && ((NamedElement)element).getName().length() > 0 ? ((NamedElement)element).getName().replace("\n", "E") : "$" + element.getClassType().getSimpleName();
            stringBuilder.insert(size, res);
        }

    }
}
