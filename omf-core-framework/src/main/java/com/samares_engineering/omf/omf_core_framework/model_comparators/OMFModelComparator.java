package com.samares_engineering.omf.omf_core_framework.model_comparators;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparator;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.samares_engineering.omf.omf_core_framework.model_comparators.comparison.ElementComparator;
import com.samares_engineering.omf.omf_core_framework.model_comparators.diffdata.dataclasses.ElementDiff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OMFModelComparator implements ModelComparator {
    private final List<ModelComparatorFilter> filters = new ArrayList<>();

    //
    // OMFModelComparator API/public methods
    //

    @Override
    public String getDiffInfo() {
        return ""; //TODO
    }

    @Override
    public void addFilter(ModelComparatorFilter filter) {
        filters.add(filter);
    }

    /***
     * UNTESTED !
     * Compares two projects.
     * @param project1 project to compare (the project to compare with the second project)
     * @param project2 other project to compare
     * @return true if the projects are identical, false otherwise
     */
    @Override
    public boolean compareModels(Project project1, Project project2) {
        List<Package> model1 = project1.getModels();
        List<Package> model2 = project2.getModels();
        return comparePackages(model1, model2);
    }

    /***
     * Compares two collections of packages.
     * WARNING: this method has not been tested yet.
     * @param model1Packages packages to compare
     * @param model2Packages other packages to compare
     * @return true if the packages are identical, false otherwise
     */
    public boolean comparePackages(Collection<Package> model1Packages, Collection<Package> model2Packages) {
        ElementComparator elementComparator = new ElementComparator(filters);
        //TODO: areSizeEquals => size of comparable elements (filter(noNeedToCompare))
        if (model1Packages.size() != model2Packages.size()) {
            return false;
        }

        for (Package package1 : model1Packages) {
            boolean findAnyIdenticalPackage = model2Packages.stream()
                    .anyMatch(package2 -> elementComparator.compareElements(package1, package2).isDiffIdentical());
            if (!findAnyIdenticalPackage)
                return false;
        }

        return true;
    }

    /*
     * UNTESTED !
     */
    public boolean comparePackages(Package subModelRoot1, Package subModelRoot2) {
        return comparePackages(Collections.singleton(subModelRoot1), Collections.singleton(subModelRoot2));
    }

    public ElementDiff compareElements(Element initPackage, Element oraclePackage) {
        ElementComparator elementComparator = new ElementComparator(filters);
        return elementComparator.compareElements(initPackage, oraclePackage);
    }
}
