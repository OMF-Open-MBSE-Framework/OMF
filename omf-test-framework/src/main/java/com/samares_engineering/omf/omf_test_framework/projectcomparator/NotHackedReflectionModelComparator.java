/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectcomparator;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.nomagic.annotation.InternalApi;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparator;
import com.nomagic.magicdraw.tests.common.comparators.ModelComparatorFilter;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.RepresentationTextCreator;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml2.Elements;
import com.nomagic.uml2.ext.jmi.reflect.AbstractRefObject;
import com.nomagic.uml2.ext.jmi.reflect.ModelReflection;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.Message;
import com.nomagic.uml2.ext.magicdraw.interactions.mdbasicinteractions.MessageOccurrenceSpecification;
import com.nomagic.uml2.impl.jmi.UML2ModelHelper;
import org.omg.mof.model.Class;
import org.omg.mof.model.Reference;
import org.omg.mof.model.StructuralFeature;

import javax.annotation.CheckForNull;
import java.lang.Package;
import java.util.*;
import java.util.stream.Collectors;

/** @deprecated */
@InternalApi(
        reason = "No Magic internal API. This code can change without any notification."
)
@Deprecated
public class NotHackedReflectionModelComparator implements ModelComparator {
    private final Set<Entry> added = new HashSet();
    private final Set<Entry> removed = new HashSet();
    private final Map<Element, Diff> changed = new HashMap();
    private final Collection<ModelComparatorFilter> filters = new ArrayList();
    private boolean loggingEnabled = true;

    public NotHackedReflectionModelComparator() {
    }

    public boolean compareModels(Project var1, Project var2) {
        List var3 = var1.getModels();
        List var4 = var2.getModels();
        return this.compareModels((Collection)var3, (Collection)var4);
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public boolean compareModels(Collection<Package> var1, Collection<Package> var2) {
        boolean var3 = var1.size() == var2.size();
        if (var3) {
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
                Package var5 = (Package)var4.next();
                boolean var6 = var2.stream().anyMatch((var2x) -> this.areElementsEqual((Element) var5, (Element) var2x));
                if (!var6) {
                    var3 = false;
                    break;
                }
            }
        }

        return var3;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public boolean areElementsEqual(Element var1, Element var2) {
        if (!this.noNeedToCompare(var1) && !this.noNeedToCompare(var2)) {
            AbstractRefObject var3 = (AbstractRefObject)var1;
            AbstractRefObject var4 = (AbstractRefObject)var2;
            if (!this.areAttributesEqual(var1, var2)) {
                return false;
            } else {
                List var5 = ModelReflection.getReferences((Class) var3.refClass().refMetaObject());
                return this.areReferencesEqual(var5, var3, var4);
            }
        } else {
            return true;
        }
    }

    private boolean noNeedToCompare(Element var1) {
        return !this.filters.stream().allMatch((var1x) -> {
            return var1x.needToCompare(var1);
        });
    }

    private boolean needToCompareAttribute(String var1, Element var2, Element var3) {
        return this.filters.stream().allMatch((var3x) -> {
            return var3x.needToCompareAttribute(var1, var2, var3);
        });
    }

    private void removeNotComparable(Collection<Element> var1) {
        var1.removeIf(this::noNeedToCompare);
    }

    private boolean areAttributesEqual(Element var1, Element var2) {
        AbstractRefObject var3 = (AbstractRefObject)var1;
        AbstractRefObject var4 = (AbstractRefObject)var2;
        Class var5 = (Class) var3.refClass().refMetaObject();
        List var6 = ModelReflection.getInstance(var1).getAttributes(var5);
        Iterator var7 = var6.iterator();

        String var9;
        Object var12;
        Object var13;
        do {
            boolean var10;
            boolean var11;
            do {
                do {
                    if (!var7.hasNext()) {
                        return true;
                    }

                    StructuralFeature var8 = (StructuralFeature)var7.next();
                    var9 = var8.getName();
                } while(!this.needToCompareAttribute(var9, var1, var2));

                var10 = var3.isSet(var9);
                var11 = var4.isSet(var9);
            } while(!var10 && !var11);

            var12 = var3.get(var9);
            var13 = var4.get(var9);
        } while(this.areValuesEqual(var9, var3, var4, var12, var13));

        this.addChange((Element)var3, (Element)var4, var9 + ": " + value(var12) + " to " + value(var13));
        return false;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected boolean areValuesEqual(String var1, AbstractRefObject var2, AbstractRefObject var3, Object var4, Object var5) {
        return Objects.equals(var4, var5);
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected static String value(Object var0) {
        return var0 == null ? "null" : "\"" + var0.toString() + "\"";
    }

    private boolean areReferencesEqual(Iterable<Reference> var1, AbstractRefObject var2, AbstractRefObject var3) {
        Collection var4 = var2.getDerivedFeatures();
        Iterator var5 = var1.iterator();

        String var7;
        boolean var8;
        Object var12;
        Object var13;
        label63:
        do {
            Object var9;
            Object var10;
            do {
                do {
                    do {
                        do {
                            if (!var5.hasNext()) {
                                return true;
                            }

                            Reference var6 = (Reference)var5.next();
                            var7 = var6.getName();
                            var8 = ModelReflection.isCompositeReference(var6);
                        } while(var4.contains(var7));
                    } while(UML2ModelHelper.isPrivatePropertyName(var7));
                } while(!var2.isSet(var7) && !var3.isSet(var7));

                var9 = var2.get(var7);
                var10 = var3.get(var7);
                if (var9 != null && var10 != null) {
                    if (var9 instanceof Collection) {
                        var13 = (Collection)var9;
                        var12 = (Collection)var10;
                    } else {
                        var13 = Collections.singletonList((Element)var9);
                        var12 = Collections.singletonList((Element)var10);
                    }
                    continue label63;
                }
            } while(!(var9 == null ^ var10 == null));

            String var11 = " reference " + var7 + " to ";
            if (var9 == null) {
                var11 = "Removed" + var11 + getFullName((Element)var10);
            } else {
                var11 = "Added" + var11 + getFullName((Element)var9);
            }

            this.addChange((Element)var2, (Element)var3, var11);
            return false;
        } while(this.areObjectReferencesEqual(var7, var8, (Element)var2, (Element)var3, (Collection)var13, (Collection)var12));

        return false;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected boolean areObjectReferencesEqual(String var1, boolean var2, Element var3, Element var4, Collection<Element> var5, Collection<Element> var6) {
        boolean var7 = this.noChangesFound();
        ArrayList var8 = new ArrayList(var5);
        ArrayList var9 = new ArrayList(var6);
        ArrayList var10 = new ArrayList(var8.size() + var9.size());
        Iterator var11 = var8.iterator();

        Element var14;
        while(var11.hasNext()) {
            Element var12 = (Element)var11.next();
            List var13 = (List)var9.stream().filter((var2x) ->
                this.areSameObjects(var12, (Element) var2x)
            ).collect(Collectors.toList());
            if (!var13.isEmpty()) {
                var10.add(var12);
                var11.remove();
                var14 = this.findBestMatchingElement(var12, var13);
                var10.add(var14);
                var9.remove(var14);
            }
        }

        this.removeNotComparable(var8);
        this.removeNotComparable(var9);
        Element var17;
        if (var2) {
            this.addNewElements(var9, var1, var3, var4);
            this.addRemovedElements(var8, var1, var3, var4);
        } else {
            Iterator var15 = var8.iterator();

            while(var15.hasNext()) {
                var17 = (Element)var15.next();
                this.addChange(var3, var4, "Removed reference " + var1 + " to " + getFullName(var17));
            }

            var15 = var9.iterator();

            while(var15.hasNext()) {
                var17 = (Element)var15.next();
                this.addChange(var3, var4, "Added reference " + var1 + " to " + getFullName(var17));
            }
        }

        if (var2 && var7) {
            for(int var16 = 0; var16 < var10.size(); var16 += 2) {
                var17 = (Element)var10.get(var16);
                var14 = (Element)var10.get(var16 + 1);
                this.areElementsEqual(var17, var14);
            }
        }

        return this.noChangesFound();
    }

    private Element findBestMatchingElement(Element var1, List<Element> var2) {
        Object var3 = new ArrayList();
        if (var2.size() > 1) {
            boolean var4 = this.isLoggingEnabled();
            this.setLoggingEnabled(false);
            var3 = (List)var2.stream().filter((var2x) -> {
                return this.areAttributesEqual(var1, var2x);
            }).collect(Collectors.toList());
            if (((List)var3).size() > 1) {
                ((List)var3).removeIf((var1x) -> !equalsChildStructure((Element) var1x, var1));
            }

            this.setLoggingEnabled(var4);
        }

        return !((List)var3).isEmpty() ? (Element)((List)var3).get(0) : (Element)var2.get(0);
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
        return this.added.isEmpty() && this.removed.isEmpty() && this.changed.isEmpty();
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected boolean areSameObjects(@CheckForNull Element var1, @CheckForNull Element var2) {
        if (var1 != null && var2 != null) {
            if (var1.getID().equals(var2.getID())) {
                return true;
            } else if (this.existsInSameProject(var1, var2)) {
                return false;
            } else {
                boolean var3 = var1.getClassType().equals(var2.getClassType());
                if (var3) {
                    if (var1 instanceof NamedElement) {
                        if (!((NamedElement)var1).getName().equals(((NamedElement)var2).getName())) {
                            return false;
                        }

                        if (var1 instanceof Diagram) {
                            DiagramPresentationElement var4 = Project.getProject(var1).getDiagram((Diagram)var1);
                            DiagramPresentationElement var5 = Project.getProject(var2).getDiagram((Diagram)var2);
                            if (!var4.getDiagramType().isEqualType(var5.getDiagramType())) {
                                return false;
                            }
                        }
                    }

                    if (Elements.isRelationship(var1) && !(var1 instanceof Connector) && (!this.areSameObjects(Elements.getClientElement(var1), Elements.getClientElement(var2)) || !this.areSameObjects(Elements.getSupplierElement(var1), Elements.getSupplierElement(var2)))) {
                        return false;
                    } else {
                        if (var1 instanceof Slot) {
                            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.StructuralFeature var12 = ((Slot)var1).getDefiningFeature();
                            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.StructuralFeature var14 = ((Slot)var2).getDefiningFeature();
                            if (!this.areSameObjects(var12, var14)) {
                                return false;
                            }
                        }

                        Property var13;
                        if (var1 instanceof TaggedValue) {
                            var13 = ((TaggedValue)var1).getTagDefinition();
                            Property var16 = ((TaggedValue)var2).getTagDefinition();
                            if (!this.areSameObjects(var13, var16)) {
                                return false;
                            }
                        }

                        if (var1 instanceof Property) {
                            var13 = (Property)var1;
                            if (var13.getName() == null || var13.getName().length() == 0) {
                                Type var18 = var13.getType();
                                Property var6 = (Property)var2;
                                Type var7 = var6.getType();
                                if (!this.areSameObjects(var18, var7)) {
                                    return false;
                                }

                                Collection var8 = var13.getEnd();
                                Collection var9 = var6.getEnd();
                                if (var8.size() != var9.size()) {
                                    return false;
                                }

                                if (!var8.isEmpty()) {
                                    ConnectorEnd var10 = (ConnectorEnd)var8.iterator().next();
                                    ConnectorEnd var11 = (ConnectorEnd)var9.iterator().next();
                                    if (!this.areSameObjects(var10, var11)) {
                                        return false;
                                    }
                                }
                            }
                        }

                        if (var1 instanceof MessageOccurrenceSpecification) {
                            Message var15 = ((MessageOccurrenceSpecification)var1).getMessage();
                            Message var20 = ((MessageOccurrenceSpecification)var2).getMessage();
                            if (!this.areSameObjects(var15, var20)) {
                                return false;
                            }
                        }

                        if (var1 instanceof Message && !Objects.equals(((Message)var1).getMessageSort(), ((Message)var2).getMessageSort())) {
                            return false;
                        } else if (var1 instanceof ElementValue) {
                            ElementValue var19 = (ElementValue)var1;
                            ElementValue var22 = (ElementValue)var2;
                            return this.areSameObjects(var19.getElement(), var22.getElement());
                        } else {
                            Element var17 = var1.getOwner();
                            Element var21 = var2.getOwner();
                            return this.areSameObjects(var17, var21);
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            return var1 == null && var2 == null;
        }
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected boolean existsInSameProject(BaseElement var1, BaseElement var2) {
        return Project.getProject(var2).getElementByID(var1.getID()) != null;
    }

    public String getDiffInfo() {
        StringBuilder var1 = new StringBuilder();
        Iterator var2;
        Entry var3;
        if (!this.added.isEmpty()) {
            var1.append("New elements:\n");
            var2 = this.added.iterator();

            while(var2.hasNext()) {
                var3 = (Entry)var2.next();
                var1.append("\t");
                toFullName(var3.element, var1);
                var1.append("\n\t\t");
                var1.append(" property=");
                var1.append(var3.property);
                var1.append("\n\t\t");
                var1.append(" owner in source 1=");
                toFullName(var3.owner1, var1);
                var1.append("\n\t\t");
                var1.append(" owner in source 2=");
                toFullName(var3.owner2, var1);
                var1.append("\n");
            }
        }

        if (!this.removed.isEmpty()) {
            var1.append("Removed elements:\n");
            var2 = this.removed.iterator();

            while(var2.hasNext()) {
                var3 = (Entry)var2.next();
                var1.append("\t");
                toFullName(var3.element, var1);
                var1.append(" ||| property=");
                var1.append(var3.property);
                var1.append(" owner in source 1=");
                toFullName(var3.owner1, var1);
                var1.append(" owner in source 2=");
                toFullName(var3.owner2, var1);
                var1.append("|||");
                var1.append("\n");
            }
        }

        if (!this.changed.isEmpty()) {
            var1.append("Changed elements:\n");
            var2 = this.changed.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry var7 = (Map.Entry)var2.next();
                var1.append("\t");
                toFullName((Element)var7.getKey(), var1);
                var1.append("\n");
                Diff var4 = (Diff)var7.getValue();
                Iterator var5 = var4.getChanges().iterator();

                while(var5.hasNext()) {
                    String var6 = (String)var5.next();
                    var1.append("\t\t").append(var6).append("\n");
                }
            }
        }

        return var1.toString();
    }

    public void addFilter(ModelComparatorFilter var1) {
        this.filters.add(var1);
    }

    private static void toFullName(Element var0, StringBuilder var1) {
        var1.append(var0.getClassType().getSimpleName()).append(" ");
        toQualifiedName(var0, var1);
        var1.append("[id=").append(var0.getID());
        var1.append(']');
        String var2 = RepresentationTextCreator.getRepresentedText(var0);
        var2 = var2.replace("\n", "E");
        var1.append("[representation text=").append(var2);
        var1.append(']');
    }

    private static String getFullName(Element var0) {
        StringBuilder var1 = new StringBuilder();
        toFullName(var0, var1);
        return var1.toString();
    }

    private static void toQualifiedName(Element var0, StringBuilder var1) {
        for(int var2 = var1.length(); var0 != null; var0 = var0.getOwner()) {
            if (var1.length() > var2) {
                var1.insert(var2, "::");
            }

            var1.insert(var2, var0 instanceof NamedElement && ((NamedElement)var0).getName().length() > 0 ? ((NamedElement)var0).getName().replace("\n", "E") : "$" + var0.getClassType().getSimpleName());
        }

    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected void addChange(Element var1, Element var2, String var3) {
        if (this.isLoggingEnabled()) {
            Diff var4 = this.changed.computeIfAbsent(var1, (var1x) -> new Diff(var2));
            var4.addChange(var3);
        }

    }

    private void addNewElements(Iterable<Element> var1, String var2, Element var3, Element var4) {
        if (this.isLoggingEnabled()) {
            Iterator var5 = var1.iterator();

            while(var5.hasNext()) {
                Element var6 = (Element)var5.next();
                this.added.add(new Entry(var6, var2, var3, var4));
            }
        }

    }

    private void addRemovedElements(Iterable<Element> var1, String var2, Element var3, Element var4) {
        if (this.isLoggingEnabled()) {
            Iterator var5 = var1.iterator();

            while(var5.hasNext()) {
                Element var6 = (Element)var5.next();
                this.removed.add(new Entry(var6, var2, var3, var4));
            }
        }

    }

    private boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    protected void setLoggingEnabled(boolean var1) {
        this.loggingEnabled = var1;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public Set<Entry> getAdded() {
        return this.added;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public Set<Entry> getRemoved() {
        return this.removed;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public Map<Element, Diff> getChanged() {
        return this.changed;
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public static final class Entry {
        private final Element element;
        private final String property;
        private final Element owner1;
        private final Element owner2;

        private Entry(Element var1, String var2, Element var3, Element var4) {
            this.element = var1;
            this.property = var2;
            this.owner1 = var3;
            this.owner2 = var4;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public Element getElement() {
            return this.element;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public String getProperty() {
            return this.property;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public Element getOwner1() {
            return this.owner1;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public Element getOwner2() {
            return this.owner2;
        }
    }

    /** @deprecated */
    @InternalApi(
            reason = "No Magic internal API. This code can change without any notification."
    )
    @Deprecated
    public static class Diff {
        private final Element changed;
        private final List<String> changes = new ArrayList();

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        Diff(Element var1) {
            this.changed = var1;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        void addChange(String var1) {
            this.changes.add(var1);
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public List<String> getChanges() {
            return this.changes;
        }

        /** @deprecated */
        @InternalApi(
                reason = "No Magic internal API. This code can change without any notification."
        )
        @Deprecated
        public Element getChangedElement() {
            return this.changed;
        }
    }
}
