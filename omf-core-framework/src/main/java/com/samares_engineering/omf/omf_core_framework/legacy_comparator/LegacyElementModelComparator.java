/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.legacy_comparator;



import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparator;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml2.Elements;
import com.nomagic.magicdraw.uml2.util.ModelEStoreEList;
import com.nomagic.uml2.ext.jmi.reflect.AbstractRefObject;
import com.nomagic.uml2.ext.jmi.reflect.ModelReflection;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Message;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.MessageOccurrenceSpecification;
import com.nomagic.uml2.impl.jmi.UML2ModelHelper;
import org.omg.mof.model.Class;
import org.omg.mof.model.MofAttribute;
import org.omg.mof.model.Reference;
import org.omg.mof.model.StructuralFeature;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.stream.Collectors;

public class LegacyElementModelComparator implements ModelComparator {
    //For Logging
    private final Set<Entry> added = new HashSet<>();
    private final Set<Entry> removed = new HashSet<>();
    private final Map<Element, Diff> changed = new HashMap<>();

    private final Collection<ModelComparatorFilter> filters = new ArrayList<>();
    private boolean loggingEnabled = true;

    @Override
    public boolean compareModels(Project project1, Project project2) {
        List<Package> model1 = project1.getModels();
        List<Package> model2 = project2.getModels();
        return comparePackages(model1, model2);
    }

    public boolean comparePackages(Package subModelRoot1, Package subModelRoot2) {
        return comparePackages(Collections.singleton(subModelRoot1), Collections.singleton(subModelRoot2));
    }

    public boolean comparePackages(Collection<Package> model1Packages, Collection<Package> model2Packages) {
        //TODO: areSizeEquals => size of comparable elements (filter(noNeedToCompare))
        if (model1Packages.size() != model2Packages.size()) {
            return false;
        }

        for (Package package1 : model1Packages) {
            boolean findAnySimilarPackage = model2Packages.stream().anyMatch(package2 -> compareElements(package1, package2));
            if (!findAnySimilarPackage)
                return false;
        }

        return noChangesFound();
    }

    public boolean compareElements(Element elem1, Element elem2) {
        //TODO: noNeedToCompare(elem2) shall return false, to continue iteration and comparing the next one.
        if (noNeedToCompare(elem1) || noNeedToCompare(elem2))
            return true;

        boolean attributesAreEqual = areAttributesEqual(elem1, elem2);

        AbstractRefObject abstractRefObject1 = (AbstractRefObject) elem1;
        AbstractRefObject abstractRefObject2 = (AbstractRefObject) elem2;
        List<Reference> references = ModelReflection.getReferences((Class) abstractRefObject1.refClass().refMetaObject());
        boolean refsAreEqual = areReferencesEqual(references, abstractRefObject1, abstractRefObject2);
        return attributesAreEqual && refsAreEqual;
    }

    private boolean noNeedToCompare(Element elem) {
        return !filters.stream().allMatch(filter -> filter.needToCompare(elem));
    }

    private boolean noNeedToCompareAttribute(String attributeName, Element elem1, Element elem2) {
        return !filters.stream().allMatch(filter -> filter.needToCompareAttribute(attributeName, elem1, elem2));
    }

    private void removeNotComparable(Collection<Element> var1) {
        var1.removeIf(this::noNeedToCompare);
    }

    //TODO REFACTOR IN ONE LOOP
    private boolean areAttributesEqual(Element elem1, Element elem2) {
        AbstractRefObject refE1 = (AbstractRefObject)elem1;
        AbstractRefObject refE2 = (AbstractRefObject)elem2;

        Class classE1 = (Class)refE1.refClass().refMetaObject();
        List<MofAttribute> attributesE1 = ModelReflection.getInstance(elem1).getAttributes(classE1);

        String attributeName;
        Object valueAttr1;
        Object valueAttr2;
        boolean areAttributesEqual = true;
        Iterator mofAttributeIterator = attributesE1.iterator();
        do {
            boolean isRefE1SetAttributeName;
            boolean isRefE2SetAttributeName;
            do {
                //Filter on comparable attributes
                do {
                    if (!mofAttributeIterator.hasNext())
                        return areAttributesEqual;

                    StructuralFeature e1Attribute = (StructuralFeature)mofAttributeIterator.next();
                    attributeName = e1Attribute.getName();
                } while(noNeedToCompareAttribute(attributeName, elem1, elem2));

                isRefE1SetAttributeName = refE1.isSet(attributeName);
                isRefE2SetAttributeName = refE2.isSet(attributeName);
            } while(!isRefE1SetAttributeName && !isRefE2SetAttributeName); //Hypothesis: the attribute has been modified, default value are not compared
            valueAttr1 = refE1.get(attributeName);
            valueAttr2 = refE2.get(attributeName);
            if(!areValuesEqual(attributeName, refE1, refE2, valueAttr1, valueAttr2)) {
                //DOC See Photo
                addChange(elem1, elem2, (Element) refE1, (Element) refE2, attributeName + ": " + value(valueAttr1) + " to " + value(valueAttr2));
                areAttributesEqual = false;
            }

        } while(mofAttributeIterator.hasNext());

        return areAttributesEqual;
    }

    protected boolean areValuesEqual(String var1, AbstractRefObject var2, AbstractRefObject var3, Object value1, Object value2) {
        return Objects.equals(value1, value2);
    }

    protected static String value(Object var0) {
        return var0 == null ? "null" : "\"" + var0.toString() + "\"";
    }

    /**
     *
     * Example: Compare Block A and Block B, it will compare all references values like ownedElements, ownedProperties, owner etc.
     * @param ref1Iterable (reference field to compare) initialized with ModelReflection.getReferences((Class) ref1.refClass().refMetaObject());
     * @param ref1 (Element)
     * @param ref2 (Element)
     * @return
     */
    private boolean areReferencesEqual(Iterable<Reference> ref1Iterable, AbstractRefObject ref1, AbstractRefObject ref2) {
        Collection<String> ref1DerivedFeaturesTitles = ref1.getDerivedFeatures();
        Iterator itRef1 = ref1Iterable.iterator();

        String refName;
        boolean isCompositeRef;
        Object ref2Values = null;
        Object ref1Values = null;

        boolean areReferenceEquals = true;

        String diffResult = null;

        boolean containsComparedFeature;
        boolean areBothValueEmpty;
        boolean firstOUTCondition;
        boolean bitwiseRefValues = false;

        boolean isRef1Empty = !itRef1.hasNext();
        boolean isRef2Empty = !ModelReflection.getReferences((Class) ref2.refClass().refMetaObject()).iterator().hasNext();
        boolean areBothEmpty = isRef1Empty && isRef2Empty;

        //If ref1 has no derivedFeatures
        //TODO LOG IF ONLY ONE IS EMPTY
        if (isRef1Empty || isRef2Empty)
            return areBothEmpty;


        do {
            Object ref1Value = null;
            Object ref2Value = null;

            Reference refToCompare = (Reference) itRef1.next();
            refName = refToCompare.getName();

            isCompositeRef = ModelReflection.isCompositeReference(refToCompare);

            //OUT CONDITION
            containsComparedFeature = ref1DerivedFeaturesTitles.contains(refName);
            areBothValueEmpty = (!ref1.isSet(refName) && !ref2.isSet(refName));
            //TODO check why we continue when containsCompareFeature ==true
            firstOUTCondition = containsComparedFeature || UML2ModelHelper.isPrivatePropertyName(refName) || areBothValueEmpty;

            if (firstOUTCondition)
                continue;

            ref1Value = ref1.get(refName);
            ref2Value = ref2.get(refName);

            if (ref1Value != null && ref2Value != null) {
                if (ref1Value instanceof Collection) {
                    ref1Values = ref1Value;
                    ref2Values = ref2Value;
                } else {
                    ref1Values = Collections.singletonList((Element) ref1Value);
                    ref2Values = Collections.singletonList((Element) ref2Value);
                }

                if(!checkIfReferenceEquals(refName, isCompositeRef, (Element) ref1, (Element) ref2, (Collection) ref1Values, (Collection) ref2Values, diffResult, areReferenceEquals))
                    areReferenceEquals = false;
                continue;
            }

            bitwiseRefValues= (ref1Value == null) == (ref2Value == null);

            if(!bitwiseRefValues) {
                diffResult = " reference " + refName + " to ";
                if (ref1Value == null) {
                    diffResult = "Removed" + diffResult + getFullName((Element) ref2Value);
                } else {
                    diffResult = "Added" + diffResult + getFullName((Element) ref1Value);
                }

                if (!areObjectReferencesEqual(refName, isCompositeRef, (Element) ref1, (Element) ref2, (Collection) ref1Values, (Collection) ref2Values)) {
                    addChange((Element) ref1, (Element) ref2, diffResult);
                    areReferenceEquals = false;
                }
            }



            // I WERE HERE
        } while (itRef1.hasNext());
        return areReferenceEquals;
    }

    public boolean checkIfReferenceEquals(String refName, boolean isCompositeRef, Element ref1, Element ref2, Collection ref1Values, Collection ref2Values, String diffResult, boolean areReferenceEquals){
        if(diffResult == null) //Initialize diffResult //TODO REFACTOR THIS
            diffResult = "- ";

        if(noNeedToCompareAttribute(refName,  ref1,  ref2))
            return true;

        if(!areObjectReferencesEqual(refName, isCompositeRef, ref1, ref2, ref1Values, ref2Values)) {
            Object ref1Value = ((AbstractRefObject) ref1).get(refName);
            Object ref2Value = ((AbstractRefObject) ref2).get(refName);
            Element e1 = null;
            Element e2 = null;
            if(ref1Value instanceof ModelEStoreEList && !((ModelEStoreEList) ref1Value).isEmpty())
                e1 = (Element) ((ModelEStoreEList) ref1Value).get(0);
            else if (ref1Value instanceof Element)
                e1 = (Element) ref1Value;

            if(ref2Value instanceof Iterable && ((Iterable) ref2Value).iterator().hasNext())
                e2 = (Element) ((Iterable) ref2Value).iterator().next();
            else if (ref1Value instanceof Element)
                e2 = (Element) ref2Value;

            if (ref1Value == null) {
                diffResult = "Removed: " + diffResult + getFullName(e1);
            } else {
                diffResult = "Added: " + diffResult + getFullName(e2);
            }

            addChange( ref1, ref2, diffResult);
        }
        return areReferenceEquals;
    }

    protected boolean areObjectReferencesEqual(String refName, boolean isCompositeRef, Element elemA, Element elemB, Collection<Element> ref1Values, Collection<Element> ref2Values) {
        boolean noChangesFound = true;

        ArrayList refElements1 = new ArrayList(ref1Values);
        ArrayList refElements2 = new ArrayList(ref2Values);
        ArrayList newArraylist = new ArrayList(refElements1.size() + refElements2.size());
        Iterator itRef1 = refElements1.iterator();

        Element bestMatch;
        while(itRef1.hasNext()) {
            Element valueAttr1 = (Element) itRef1.next();
            List equivalentElems = (List)refElements2.stream()
                    .filter(elem -> areSameObjects(valueAttr1, (Element) elem))
                    .collect(Collectors.toList());

            if (!equivalentElems.isEmpty()) {
                newArraylist.add(valueAttr1);
                itRef1.remove();
                bestMatch = findBestMatchingElement(valueAttr1, equivalentElems);
                newArraylist.add(bestMatch);
                refElements2.remove(bestMatch);
            }
        }

        removeNotComparable(refElements1);
        removeNotComparable(refElements2);
        Element elem1;
        if (isCompositeRef) {
            addNewElements(refElements2, refName, elemA, elemB);
            addRemovedElements(refElements1, refName, elemA, elemB);
        } else {
            Iterator itRefElem = refElements1.iterator();
            if(!refElements2.isEmpty() || !refElements1.isEmpty())
                noChangesFound = false;
            while(itRefElem.hasNext()) {
                elem1 = (Element)itRefElem.next();
                addChange(elemA, elemB, "Removed reference " + refName + " to " + getFullName(elem1));
            }

            itRefElem = refElements2.iterator();
            while(itRefElem.hasNext()) {
                elem1 = (Element)itRefElem.next();
                addChange(elemA, elemB, "Added reference " + refName + " to " + getFullName(elem1));
            }
        }

        if (isCompositeRef && noChangesFound) {
            for(int i = 0; i < newArraylist.size(); i += 2) {
                elem1 = (Element)newArraylist.get(i);
                bestMatch = (Element)newArraylist.get(i + 1);
                compareElements(elem1, bestMatch);
            }
        }

        return noChangesFound;
    }

    private Element findBestMatchingElement(Element element, List<Element> elemsToMatch) {
        List<Element> matchingElements = new ArrayList();
        if (elemsToMatch.size() > 1) {
            boolean isLoggingEnabled = isLoggingEnabled();
            setLoggingEnabled(false);
            matchingElements = elemsToMatch.stream().filter(var2x -> areAttributesEqual(element, var2x)).collect(Collectors.toList());
            if (((List)matchingElements).size() > 1) {
                ((List)matchingElements).removeIf((var1x) -> !equalsChildStructure((Element) var1x, element));
            }

            setLoggingEnabled(isLoggingEnabled);
        }

        return !(matchingElements).isEmpty() ? (matchingElements).get(0) : elemsToMatch.get(0);
    }

    private static boolean equalsChildStructure(Element var0, Element var1) {
        ArrayList var2 = new ArrayList();
        var2.add(var0);
        ArrayList var3 = new ArrayList();
        var3.add(var1);
        int var4 = 0;

        do {
            if (var4 >= var2.size()) {
                return true;
            }

            int var5 = var2.size();

            for(int var6 = var4; var6 < var5; ++var6) {
                var2.addAll(((Element)var2.get(var6)).getOwnedElement());
                var3.addAll(((Element)var3.get(var6)).getOwnedElement());
            }

            var4 = var5;
        } while(var2.size() == var3.size());

        return false;
    }

    private boolean noChangesFound() {
        return added.isEmpty() && removed.isEmpty() && changed.isEmpty();
    }

    protected boolean areSameObjects(@CheckForNull Element elem1, @CheckForNull Element elem2) {

        final boolean oneIsNull =elem1 == null || elem2 == null;
        if (oneIsNull)
            return elem1 == null && elem2 == null;  //if bothNull => same
        //IF ID
        final boolean hasSameID = filters.stream().allMatch(filter -> filter.needToCompareAttribute("id", elem1, elem2)) && elem1.getID().equals(elem2.getID());
        if (hasSameID)
            return false;


//        if (existsInSameProject(elem1, elem2))
//            return false;

        boolean sharedSameClass = elem1.getClassType().equals(elem2.getClassType());
        if (!sharedSameClass)
            return false;

        //NAMED ELEMENTS
        if (elem1 instanceof NamedElement) {
            final boolean hasSameName = ((NamedElement)elem1).getName().equals(((NamedElement)elem2).getName());
            if (!hasSameName)
                return false;

            if (elem1 instanceof Diagram) {
                DiagramPresentationElement var4 = Project.getProject(elem1).getDiagram((Diagram)elem1);
                DiagramPresentationElement var5 = Project.getProject(elem2).getDiagram((Diagram)elem2);

                final boolean sameDiagramType = var4.getDiagramType().isEqualType(var5.getDiagramType());
                if (!sameDiagramType)
                    return false;
            }
        }


        //FUCK IT, I'm not translating that
        if (Elements.isRelationship(elem1) && !(elem1 instanceof Connector) && (!areSameObjects(Elements.getClientElement(elem1), Elements.getClientElement(elem2)) || !areSameObjects(Elements.getSupplierElement(elem1), Elements.getSupplierElement(elem2))))
            return false;


        if (elem1 instanceof Slot) {
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.StructuralFeature attrE1 = ((Slot)elem1).getDefiningFeature();
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.StructuralFeature attrE2 = ((Slot)elem2).getDefiningFeature();
            if (!areSameObjects(attrE1, attrE2))
                return false;
        }

        Property propE1;
        if (elem1 instanceof TaggedValue) {
            propE1 = ((TaggedValue)elem1).getTagDefinition();
            Property propE2 = ((TaggedValue)elem2).getTagDefinition();
            if (!areSameObjects(propE1, propE2))
                return false;
        }

        if (elem1 instanceof Property) {
            propE1 = (Property)elem1;

            boolean isE1NameVoid = propE1.getName() == null || propE1.getName().length() == 0;
            if (isE1NameVoid) {
                Type type1 = propE1.getType();
                Property prop2 = (Property)elem2;
                Type type2 = prop2.getType();
                if (!areSameObjects(type1, type2))
                    return false;

                Collection e1Ends = propE1.getEnd();
                Collection e2Ends = prop2.getEnd();
                if (e1Ends.size() != e2Ends.size())
                    return false;

                if (!e1Ends.isEmpty()) {
                    ConnectorEnd ceE1 = (ConnectorEnd)e1Ends.iterator().next();
                    ConnectorEnd ceE2 = (ConnectorEnd)e2Ends.iterator().next();
                    if (!areSameObjects(ceE1, ceE2))
                        return false;

                }
            }
        }

        if (elem1 instanceof MessageOccurrenceSpecification) {
            Message var15 = ((MessageOccurrenceSpecification)elem1).getMessage();
            Message var20 = ((MessageOccurrenceSpecification)elem2).getMessage();
            if (!areSameObjects(var15, var20))
                return false;

        }

        if (elem1 instanceof Message && !Objects.equals(((Message)elem1).getMessageSort(), ((Message)elem2).getMessageSort()))
            return false;

        if (elem1 instanceof ElementValue) {
            ElementValue var19 = (ElementValue)elem1;
            ElementValue var22 = (ElementValue)elem2;
            return areSameObjects(var19.getElement(), var22.getElement());
        } else {
            //ADDED IF NO DIFF BUT SAME NAME THEN RETURN TRUE
            if (elem1 instanceof NamedElement)
                if(((NamedElement) elem1).getName().equals(((NamedElement) elem2).getName()))
                    return true;
            boolean needToCompareOwners = filters.stream().anyMatch(filter -> filter.needToCompareAttribute("owner", elem1, elem2));
            if(!needToCompareOwners)
                return true;

            Element ownerE1 = elem1.getOwner();
            Element ownerE2 = elem2.getOwner();
            return areSameObjects(ownerE1, ownerE2);
        }
    }

    protected boolean existsInSameProject(BaseElement var1, BaseElement var2) {
        return Project.getProject(var2).getElementByID(var1.getID()) != null;
    }

    @Override
    public String getDiffInfo() {
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
                sBuilder.append("\t");
                toFullName(elementDiffEntry.getKey(), sBuilder);
                sBuilder.append("\n");
                Diff diff = elementDiffEntry.getValue();

                for (String change : diff.getChanges()) {
                    sBuilder.append("\t\t").append(change).append("\n");
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
            res += element instanceof NamedElement && ((NamedElement)element).getName().length() > 0
                    ? ((NamedElement)element).getName().replace("\n", "E") : "$" + element.getClassType().getSimpleName();
            stringBuilder.insert(size, res);
        }

    }

    public void addFilter(ModelComparatorFilter var1) {
        filters.add(var1);
    }

    private String getFullName(Element elem) {
        StringBuilder var1 = new StringBuilder();
        toFullName(elem, var1);
        return var1.toString();
    }

    private void addChange(Element ref1, Element ref2, String diffResult) {
        addChange(ref1.getOwner(), ref2.getOwner(), ref1, ref2, diffResult);
    }

    protected void addChange(Element owner1, Element owner2, Element elem1, Element elem2, String changeDesc) {
        if (isLoggingEnabled()) {
            Diff var4 = changed.computeIfAbsent(elem1, elem1x -> new Diff(elem2));

            if(owner1 instanceof TaggedValue)
                var4.addChange("Owner A: " + getFullName(owner1.getOwner())
                        + " - " + "Owner B: " + getFullName(owner2.getOwner()));
            else
                var4.addChange( "Owner A: " + getFullName(owner1)
                        + " - " + "Owner B: " + getFullName(owner2));
            var4.addChange(changeDesc);
        }

    }

    private void addNewElements(Iterable<Element> elements, String changeDesc, Element elem1, Element elem2) {
        if (isLoggingEnabled()) {

            for (Element var6 : elements) {
                added.add(new Entry(var6, changeDesc, elem1, elem2));
            }
        }

    }

    private void addRemovedElements(Iterable<Element> elements, String changeDesc, Element elem1, Element elem2) {
        if (isLoggingEnabled()) {

            for (Element var6 : elements) {
                removed.add(new Entry(var6, changeDesc, elem1, elem2));
            }
        }

    }

    public static final class Entry {

        private final Element element;
        private final String property;
        private final Element owner1;
        private final Element owner2;
        private Entry(Element modifiedElement, String changeDesc, Element owner1, Element owner2) {
            this.element = modifiedElement;
            this.property = changeDesc;
            this.owner1 = owner1;
            this.owner2 = owner2;
        }

        public Element getElement() {
            return element;
        }

        public String getProperty() {
            return property;
        }

        public Element getOwner1() {
            return owner1;
        }

        public Element getOwner2() {
            return owner2;
        }

    }

    public static class Diff {

        private final Element changed;
        private final List<String> changes = new ArrayList<>();
        public Diff(Element var1) {
            changed = var1;
        }

        public void addChange(String var1) {
            changes.add(var1);
        }

        public List<String> getChanges() {
            return changes;
        }

        public Element getChangedElement() {
            return changed;
        }

    }

    /*
    Getters/Setters
     */

    private boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    protected void setLoggingEnabled(boolean var1) {
        loggingEnabled = var1;
    }

    public Set<Entry> getAdded() {
        return added;
    }

    public Set<Entry> getRemoved() {
        return removed;
    }

    public Map<Element, Diff> getChanged() {
        return changed;
    }
}

