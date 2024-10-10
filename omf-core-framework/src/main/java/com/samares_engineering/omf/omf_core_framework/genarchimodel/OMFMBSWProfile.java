package com.samares_engineering.omf.omf_core_framework.genarchimodel;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml2.Profiles;
import com.nomagic.profiles.ProfileCache;
import com.nomagic.profiles.ProfileImplementation;
import com.nomagic.profiles.ProfilesBridge;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.project.ElementProject;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("WeakerAccess, unused")
public class OMFMBSWProfile extends ProfileImplementation
{
    public static final String PROFILE_URI =  "";

    public static final String PROFILE_NAME =  "OMF MBSW Profile";

    private final AnnotationStereotype annotationStereotype;
    private final CodeClassStereotype codeClassStereotype;
    private final CodeDocStereotype codeDocStereotype;
    private final CodeFileStereotype codeFileStereotype;
    private final EnumClassStereotype enumClassStereotype;
    private final FeatureStereotype featureStereotype;
    private final FeatureItemStereotype featureItemStereotype;
    private final HookStereotype hookStereotype;
    private final InterfaceStereotype _interfaceStereotype;
    private final LiveActionStereotype liveActionStereotype;
    private final MethodStereotype methodStereotype;
    private final NamespacePackageStereotype namespacePackageStereotype;
    private final OptionStereotype optionStereotype;
    private final PluginStereotype pluginStereotype;
    private final UiActionStereotype uiActionStereotype;
    private final WithNameSpaceStereotype withNameSpaceStereotype;

    private final ClassTypeEnumeration classTypeEnumeration;
    private final ModifiersEnumeration modifiersEnumeration;
    public static OMFMBSWProfile getInstance(){
        return getInstance(OMFUtils.getProject());
    }
    public static OMFMBSWProfile getInstance(BaseElement baseElement)
    {
        OMFMBSWProfile profile = ProfilesBridge.getProfile(OMFMBSWProfile.class, baseElement);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(OMFMBSWProfile.class, baseElement, OMFMBSWProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public static OMFMBSWProfile getInstanceByProject(ElementProject project)
    {
        OMFMBSWProfile profile = ProfilesBridge.getProfile(OMFMBSWProfile.class, project);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(OMFMBSWProfile.class, project, OMFMBSWProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public  OMFMBSWProfile(ProfileCache cache)
    {
        super(cache);
        annotationStereotype = new AnnotationStereotype(this);
        codeClassStereotype = new CodeClassStereotype(this);
        codeDocStereotype = new CodeDocStereotype(this);
        codeFileStereotype = new CodeFileStereotype(this);
        enumClassStereotype = new EnumClassStereotype(this);
        featureStereotype = new FeatureStereotype(this);
        featureItemStereotype = new FeatureItemStereotype(this);
        hookStereotype = new HookStereotype(this);
        _interfaceStereotype = new InterfaceStereotype(this);
        liveActionStereotype = new LiveActionStereotype(this);
        methodStereotype = new MethodStereotype(this);
        namespacePackageStereotype = new NamespacePackageStereotype(this);
        optionStereotype = new OptionStereotype(this);
        pluginStereotype = new PluginStereotype(this);
        uiActionStereotype = new UiActionStereotype(this);
        withNameSpaceStereotype = new WithNameSpaceStereotype(this);
        classTypeEnumeration = new ClassTypeEnumeration(this);
        modifiersEnumeration = new ModifiersEnumeration(this);

    }
    public AnnotationStereotype annotation()
    {
        return annotationStereotype;
    }
    public CodeClassStereotype codeClass()
    {
        return codeClassStereotype;
    }
    public CodeDocStereotype codeDoc()
    {
        return codeDocStereotype;
    }
    public CodeFileStereotype codeFile()
    {
        return codeFileStereotype;
    }
    public EnumClassStereotype enumClass()
    {
        return enumClassStereotype;
    }
    public FeatureStereotype feature()
    {
        return featureStereotype;
    }
    public FeatureItemStereotype featureItem()
    {
        return featureItemStereotype;
    }
    public HookStereotype hook()
    {
        return hookStereotype;
    }
    public InterfaceStereotype _interface()
    {
        return _interfaceStereotype;
    }
    public LiveActionStereotype liveAction()
    {
        return liveActionStereotype;
    }
    public MethodStereotype method()
    {
        return methodStereotype;
    }
    public NamespacePackageStereotype namespacePackage()
    {
        return namespacePackageStereotype;
    }
    public OptionStereotype option()
    {
        return optionStereotype;
    }
    public PluginStereotype plugin()
    {
        return pluginStereotype;
    }
    public UiActionStereotype uiAction()
    {
        return uiActionStereotype;
    }
    public WithNameSpaceStereotype withNameSpace()
    {
        return withNameSpaceStereotype;
    }



    public static final String CLASSTYPE_DATATYPE = "ClassType";

    public static final String MODIFIERS_DATATYPE = "Modifiers";

    @SuppressWarnings("ConstantConditions")
    public Enumeration getClassType()
    {
        return classTypeEnumeration.getEnumeration();
    }

    @SuppressWarnings("ConstantConditions")
    public Enumeration getModifiers()
    {
        return modifiersEnumeration.getEnumeration();
    }



    //enumeration ClassType literals
    public enum ClassTypeEnum implements TextProvider
    {
        ;
        private final String text;

        ClassTypeEnum(String text)
        {
            this.text = text;
        }

        @Override
        public String getText()
        {
            return this.text;
        }
        @CheckForNull
        public static ClassTypeEnum from(@CheckForNull Object o)
        {
            return valueFromString(ClassTypeEnum.class, o);
        }
        @CheckForNull
        public static ClassTypeEnum toEnum(EnumerationLiteral literal)
        {
            return from(literal);
        }
        @CheckForNull
        public static EnumerationLiteral toEnumerationLiteral(OMFMBSWProfile profile, ClassTypeEnum anEnum)
        {
            return null;
        }
    }
    private static class ClassTypeEnumeration extends EnumerationWrapper
    {
        private  ClassTypeEnumeration(OMFMBSWProfile profile)
        {
            super(profile);
        }
        @CheckForNull
        public Enumeration getEnumeration()
        {
            return getElementByName(CLASSTYPE_DATATYPE);
        }
        @Override
        protected void clear()
        {
            super.clear();

        }
    }


    //enumeration Modifiers literals
    public enum ModifiersEnum implements TextProvider
    {
        PUBLIC(ModifiersEnumeration.PUBLIC),
        PRIVATE(ModifiersEnumeration.PRIVATE);
        private final String text;

        ModifiersEnum(String text)
        {
            this.text = text;
        }

        @Override
        public String getText()
        {
            return this.text;
        }
        @CheckForNull
        public static ModifiersEnum from(@CheckForNull Object o)
        {
            return valueFromString(ModifiersEnum.class, o);
        }
        @CheckForNull
        public static ModifiersEnum toEnum(EnumerationLiteral literal)
        {
            return from(literal);
        }
        @CheckForNull
        public static EnumerationLiteral toEnumerationLiteral(OMFMBSWProfile profile, ModifiersEnum anEnum)
        {
            if (anEnum == PUBLIC)
            {
                return profile.modifiersEnumeration.getPublicEnumerationLiteral();
            }
            if (anEnum == PRIVATE)
            {
                return profile.modifiersEnumeration.getPrivateEnumerationLiteral();
            }
            return null;
        }
    }
    private static class ModifiersEnumeration extends EnumerationWrapper
    {
        public static final String PUBLIC =  "public";
        public static final String PRIVATE =  "private";
        @CheckForNull
        private EnumerationLiteral _public;
        @CheckForNull
        private EnumerationLiteral _private;
        private  ModifiersEnumeration(OMFMBSWProfile profile)
        {
            super(profile);
        }
        @CheckForNull
        public Enumeration getEnumeration()
        {
            return getElementByName(MODIFIERS_DATATYPE);
        }
        @CheckForNull
        public EnumerationLiteral getPublicEnumerationLiteral()
        {
            if (_public == null)
            {
                _public = getEnumerationLiteralByName(getEnumeration(), PUBLIC);
            }
            return _public;
        }
        @CheckForNull
        public EnumerationLiteral getPrivateEnumerationLiteral()
        {
            if (_private == null)
            {
                _private = getEnumerationLiteralByName(getEnumeration(), PRIVATE);
            }
            return _private;
        }
        @Override
        protected void clear()
        {
            super.clear();
            _public = null;
            _private = null;
        }
    }

    public static class AnnotationStereotype extends StereotypeWrapper
    {


        //stereotype Annotation and its tags
        public static final String STEREOTYPE_NAME =  "Annotation";
        public static final String ISSTATIC =  "isStatic";
        public static final String TYPE =  "type";
        public static final String CODE_DOCUMENTATION =  "code documentation";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        protected  AnnotationStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsStaticProperty()
        {
            return _p.codeFile().getIsStaticProperty();
        }

        @CheckForNull
        public Property getTypeProperty()
        {
            return _p.codeFile().getTypeProperty();
        }

        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        public void setIsStatic(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsStaticProperty(), value);
        }
        public void clearIsStatic(Element element)
        {
            _p.codeFile().clearIsStatic(element);
        }
        @CheckForNull
        public Boolean isIsStatic(Element element)
        {
            return _p.codeFile().isIsStatic(element);
        }
        public void setType(Element element, @CheckForNull ClassTypeEnum value)
        {
            Profiles.setValue(element, getStereotype(), getTypeProperty(), value != null ? value.getText() : null);
        }
        public void clearType(Element element)
        {
            _p.codeFile().clearType(element);
        }
        @CheckForNull
        public ClassTypeEnum getType(Element element)
        {
            return _p.codeFile().getType(element);
        }
        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.annotation().getStereotype());
            }
            return false;
        }

    }
    public static class CodeClassStereotype extends StereotypeWrapper
    {


        //stereotype CodeClass and its tags
        public static final String STEREOTYPE_NAME =  "CodeClass";
        public static final String ISSTATIC =  "isStatic";
        public static final String TYPE =  "type";
        public static final String CODE_DOCUMENTATION =  "code documentation";
        public static final String NAMESPACE =  "namespace";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property isEncapsulated;
        protected  CodeClassStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsStaticProperty()
        {
            return _p.codeFile().getIsStaticProperty();
        }

        @CheckForNull
        public Property getTypeProperty()
        {
            return _p.codeFile().getTypeProperty();
        }

        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setIsStatic(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsStaticProperty(), value);
        }
        public void clearIsStatic(Element element)
        {
            _p.codeFile().clearIsStatic(element);
        }
        @CheckForNull
        public Boolean isIsStatic(Element element)
        {
            return _p.codeFile().isIsStatic(element);
        }
        public void setType(Element element, @CheckForNull ClassTypeEnum value)
        {
            Profiles.setValue(element, getStereotype(), getTypeProperty(), value != null ? value.getText() : null);
        }
        public void clearType(Element element)
        {
            _p.codeFile().clearType(element);
        }
        @CheckForNull
        public ClassTypeEnum getType(Element element)
        {
            return _p.codeFile().getType(element);
        }
        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.codeClass().getStereotype());
            }
            return false;
        }

    }
    public static class CodeDocStereotype extends StereotypeWrapper
    {


        //stereotype CodeDoc and its tags
        public static final String STEREOTYPE_NAME =  "CodeDoc";
        public static final String CODE_DOCUMENTATION =  "code documentation";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property codedocumentation;
        protected  CodeDocStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            if (codedocumentation == null)
            {
                codedocumentation = getTagByName(getStereotype(), CODE_DOCUMENTATION);
            }
            return codedocumentation;
        }

        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            Profiles.clearValue(element, getCodedocumentationProperty());
        }

        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return toString(Profiles.getFirstValue(element, getCodedocumentationProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            codedocumentation = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element != null &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element != null)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.codeDoc().getStereotype());
            }
            return false;
        }

    }
    public static class CodeFileStereotype extends StereotypeWrapper
    {


        //stereotype CodeFile and its tags
        public static final String STEREOTYPE_NAME =  "CodeFile";
        public static final String ISSTATIC =  "isStatic";
        public static final String TYPE =  "type";
        public static final String CODE_DOCUMENTATION =  "code documentation";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property isStatic;
        @CheckForNull
        private Property type;
        protected  CodeFileStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsStaticProperty()
        {
            if (isStatic == null)
            {
                isStatic = getTagByName(getStereotype(), ISSTATIC);
            }
            return isStatic;
        }

        @CheckForNull
        public Property getTypeProperty()
        {
            if (type == null)
            {
                type = getTagByName(getStereotype(), TYPE);
            }
            return type;
        }

        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        public void setIsStatic(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsStaticProperty(), value);
        }
        public void clearIsStatic(Element element)
        {
            Profiles.clearValue(element, getIsStaticProperty());
        }

        @CheckForNull
        public Boolean isIsStatic(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsStaticProperty()));
        }

        public void setType(Element element, @CheckForNull ClassTypeEnum value)
        {
            Profiles.setValue(element, getStereotype(), getTypeProperty(), value != null ? value.getText() : null);
        }
        public void clearType(Element element)
        {
            Profiles.clearValue(element, getTypeProperty());
        }

        @CheckForNull
        public ClassTypeEnum getType(Element element)
        {
            return ClassTypeEnum.from(Profiles.getFirstValue(element, getTypeProperty()));
        }

        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        @Override
        protected void clear()
        {
            super.clear();
            isStatic = null;
            type = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.codeFile().getStereotype());
            }
            return false;
        }

    }
    public static class EnumClassStereotype extends StereotypeWrapper
    {


        //stereotype EnumClass and its tags
        public static final String STEREOTYPE_NAME =  "EnumClass";
        public static final String ISSTATIC =  "isStatic";
        public static final String TYPE =  "type";
        public static final String CODE_DOCUMENTATION =  "code documentation";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        protected  EnumClassStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsStaticProperty()
        {
            return _p.codeFile().getIsStaticProperty();
        }

        @CheckForNull
        public Property getTypeProperty()
        {
            return _p.codeFile().getTypeProperty();
        }

        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        public void setIsStatic(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsStaticProperty(), value);
        }
        public void clearIsStatic(Element element)
        {
            _p.codeFile().clearIsStatic(element);
        }
        @CheckForNull
        public Boolean isIsStatic(Element element)
        {
            return _p.codeFile().isIsStatic(element);
        }
        public void setType(Element element, @CheckForNull ClassTypeEnum value)
        {
            Profiles.setValue(element, getStereotype(), getTypeProperty(), value != null ? value.getText() : null);
        }
        public void clearType(Element element)
        {
            _p.codeFile().clearType(element);
        }
        @CheckForNull
        public ClassTypeEnum getType(Element element)
        {
            return _p.codeFile().getType(element);
        }
        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.enumClass().getStereotype());
            }
            return false;
        }

    }
    public static class FeatureStereotype extends StereotypeWrapper
    {


        //stereotype Feature and its tags
        public static final String STEREOTYPE_NAME =  "Feature";
        public static final String HOOKS =  "hooks";
        public static final String LIVEACTIONS =  "liveActions";
        public static final String OPTIONS =  "options";
        public static final String UIACTIONS =  "uiActions";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property hooks;
        @CheckForNull
        private Property liveActions;
        @CheckForNull
        private Property options;
        @CheckForNull
        private Property uiActions;
        @CheckForNull
        private Property isEncapsulated;
        protected  FeatureStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getHooksProperty()
        {
            if (hooks == null)
            {
                hooks = getTagByName(getStereotype(), HOOKS);
            }
            return hooks;
        }

        @CheckForNull
        public Property getLiveActionsProperty()
        {
            if (liveActions == null)
            {
                liveActions = getTagByName(getStereotype(), LIVEACTIONS);
            }
            return liveActions;
        }

        @CheckForNull
        public Property getOptionsProperty()
        {
            if (options == null)
            {
                options = getTagByName(getStereotype(), OPTIONS);
            }
            return options;
        }

        @CheckForNull
        public Property getUiActionsProperty()
        {
            if (uiActions == null)
            {
                uiActions = getTagByName(getStereotype(), UIACTIONS);
            }
            return uiActions;
        }

        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setHooks(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getHooksProperty(), value);
        }
        public void clearHooks(Element element)
        {
            Profiles.clearValue(element, getHooksProperty());
        }
        public void addHooks(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getHooksProperty(), value);
        }
        public void removeHooks(Element element, Element value)
        {
            java.util.List<Element> values = getHooks(element);
            if (values.remove(value)) {
                setHooks(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getHooks(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getHooksProperty());
        }

        public void setLiveActions(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getLiveActionsProperty(), value);
        }
        public void clearLiveActions(Element element)
        {
            Profiles.clearValue(element, getLiveActionsProperty());
        }
        public void addLiveActions(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getLiveActionsProperty(), value);
        }
        public void removeLiveActions(Element element, Element value)
        {
            java.util.List<Element> values = getLiveActions(element);
            if (values.remove(value)) {
                setLiveActions(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getLiveActions(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getLiveActionsProperty());
        }

        public void setOptions(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getOptionsProperty(), value);
        }
        public void clearOptions(Element element)
        {
            Profiles.clearValue(element, getOptionsProperty());
        }
        public void addOptions(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getOptionsProperty(), value);
        }
        public void removeOptions(Element element, Element value)
        {
            java.util.List<Element> values = getOptions(element);
            if (values.remove(value)) {
                setOptions(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getOptions(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getOptionsProperty());
        }

        public void setUiActions(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getUiActionsProperty(), value);
        }
        public void clearUiActions(Element element)
        {
            Profiles.clearValue(element, getUiActionsProperty());
        }
        public void addUiActions(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getUiActionsProperty(), value);
        }
        public void removeUiActions(Element element, Element value)
        {
            java.util.List<Element> values = getUiActions(element);
            if (values.remove(value)) {
                setUiActions(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getUiActions(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getUiActionsProperty());
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            hooks = null;
            liveActions = null;
            options = null;
            uiActions = null;
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.feature().getStereotype());
            }
            return false;
        }

    }
    public static class FeatureItemStereotype extends StereotypeWrapper
    {


        //stereotype Feature Item and its tags
        public static final String STEREOTYPE_NAME =  "Feature Item";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property isEncapsulated;
        protected  FeatureItemStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.featureItem().getStereotype());
            }
            return false;
        }

    }
    public static class HookStereotype extends StereotypeWrapper
    {


        //stereotype Hook and its tags
        public static final String STEREOTYPE_NAME =  "Hook";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property isEncapsulated;
        protected  HookStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.hook().getStereotype());
            }
            return false;
        }

    }
    public static class InterfaceStereotype extends StereotypeWrapper
    {


        //stereotype Interface and its tags
        public static final String STEREOTYPE_NAME =  "Interface";
        public static final String ISSTATIC =  "isStatic";
        public static final String TYPE =  "type";
        public static final String CODE_DOCUMENTATION =  "code documentation";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        protected  InterfaceStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsStaticProperty()
        {
            return _p.codeFile().getIsStaticProperty();
        }

        @CheckForNull
        public Property getTypeProperty()
        {
            return _p.codeFile().getTypeProperty();
        }

        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        public void setIsStatic(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsStaticProperty(), value);
        }
        public void clearIsStatic(Element element)
        {
            _p.codeFile().clearIsStatic(element);
        }
        @CheckForNull
        public Boolean isIsStatic(Element element)
        {
            return _p.codeFile().isIsStatic(element);
        }
        public void setType(Element element, @CheckForNull ClassTypeEnum value)
        {
            Profiles.setValue(element, getStereotype(), getTypeProperty(), value != null ? value.getText() : null);
        }
        public void clearType(Element element)
        {
            _p.codeFile().clearType(element);
        }
        @CheckForNull
        public ClassTypeEnum getType(Element element)
        {
            return _p.codeFile().getType(element);
        }
        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance._interface().getStereotype());
            }
            return false;
        }

    }
    public static class LiveActionStereotype extends StereotypeWrapper
    {


        //stereotype LiveAction and its tags
        public static final String STEREOTYPE_NAME =  "LiveAction";
        public static final String EVENTMATCHES =  "eventMatches";
        public static final String PROCESS =  "process";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property eventMatches;
        @CheckForNull
        private Property process;
        @CheckForNull
        private Property isEncapsulated;
        protected  LiveActionStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getEventMatchesProperty()
        {
            if (eventMatches == null)
            {
                eventMatches = getTagByName(getStereotype(), EVENTMATCHES);
            }
            return eventMatches;
        }

        @CheckForNull
        public Property getProcessProperty()
        {
            if (process == null)
            {
                process = getTagByName(getStereotype(), PROCESS);
            }
            return process;
        }

        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setEventMatches(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getEventMatchesProperty(), value);
        }
        public void clearEventMatches(Element element)
        {
            Profiles.clearValue(element, getEventMatchesProperty());
        }

        @CheckForNull
        public Element getEventMatches(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getEventMatchesProperty());
        }

        public void setProcess(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getProcessProperty(), value);
        }
        public void clearProcess(Element element)
        {
            Profiles.clearValue(element, getProcessProperty());
        }

        @CheckForNull
        public Element getProcess(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getProcessProperty());
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            eventMatches = null;
            process = null;
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.liveAction().getStereotype());
            }
            return false;
        }

    }
    public static class MethodStereotype extends StereotypeWrapper
    {


        //stereotype Method and its tags
        public static final String STEREOTYPE_NAME =  "Method";
        public static final String CODE_DOCUMENTATION =  "code documentation";

        private final OMFMBSWProfile _p;
        protected  MethodStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getCodedocumentationProperty()
        {
            return _p.codeDoc().getCodedocumentationProperty();
        }

        public void setCodedocumentation(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getCodedocumentationProperty(), value);
        }
        public void clearCodedocumentation(Element element)
        {
            _p.codeDoc().clearCodedocumentation(element);
        }
        @CheckForNull
        public String getCodedocumentation(Element element)
        {
            return _p.codeDoc().getCodedocumentation(element);
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.method().getStereotype());
            }
            return false;
        }

    }
    public static class NamespacePackageStereotype extends StereotypeWrapper
    {


        //stereotype Namespace Package and its tags
        public static final String STEREOTYPE_NAME =  "Namespace Package";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        protected  NamespacePackageStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getNamespaceProperty()
        {
            return _p.withNameSpace().getNamespaceProperty();
        }

        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            _p.withNameSpace().clearNamespace(element);
        }
        @CheckForNull
        public String getNamespace(Element element)
        {
            return _p.withNameSpace().getNamespace(element);
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.namespacePackage().getStereotype());
            }
            return false;
        }

    }
    public static class OptionStereotype extends StereotypeWrapper
    {


        //stereotype Option and its tags
        public static final String STEREOTYPE_NAME =  "Option";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property isEncapsulated;
        protected  OptionStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.option().getStereotype());
            }
            return false;
        }

    }
    public static class PluginStereotype extends StereotypeWrapper
    {


        //stereotype Plugin and its tags
        public static final String STEREOTYPE_NAME =  "Plugin";
        public static final String FEATURES =  "features";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property features;
        @CheckForNull
        private Property isEncapsulated;
        protected  PluginStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getFeaturesProperty()
        {
            if (features == null)
            {
                features = getTagByName(getStereotype(), FEATURES);
            }
            return features;
        }

        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setFeatures(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getFeaturesProperty(), value);
        }
        public void clearFeatures(Element element)
        {
            Profiles.clearValue(element, getFeaturesProperty());
        }
        public void addFeatures(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getFeaturesProperty(), value);
        }
        public void removeFeatures(Element element, Element value)
        {
            java.util.List<Element> values = getFeatures(element);
            if (values.remove(value)) {
                setFeatures(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getFeatures(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getFeaturesProperty());
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            features = null;
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.plugin().getStereotype());
            }
            return false;
        }

    }
    public static class UiActionStereotype extends StereotypeWrapper
    {


        //stereotype UIAction and its tags
        public static final String STEREOTYPE_NAME =  "UIAction";
        public static final String ACTIONTOPERFORMED =  "actionToPerformed";
        public static final String CHECKAVAIBILITY =  "checkAvaibility";

        /**
         * If true, then the block is treated as a black box; a part typed by this black box can only be connected via its ports or directly to its outer boundary. If false, or if a value is not present, then connections can be established to elements of its internal structure via deep-nested connector ends.
         */public static final String ISENCAPSULATED =  "isEncapsulated";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property actionToPerformed;
        @CheckForNull
        private Property checkAvaibility;
        @CheckForNull
        private Property isEncapsulated;
        protected  UiActionStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getActionToPerformedProperty()
        {
            if (actionToPerformed == null)
            {
                actionToPerformed = getTagByName(getStereotype(), ACTIONTOPERFORMED);
            }
            return actionToPerformed;
        }

        @CheckForNull
        public Property getCheckAvaibilityProperty()
        {
            if (checkAvaibility == null)
            {
                checkAvaibility = getTagByName(getStereotype(), CHECKAVAIBILITY);
            }
            return checkAvaibility;
        }

        @CheckForNull
        public Property getIsEncapsulatedProperty()
        {
            if (isEncapsulated == null)
            {
                isEncapsulated = getTagByName(getStereotype(), ISENCAPSULATED);
            }
            return isEncapsulated;
        }

        public void setActionToPerformed(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getActionToPerformedProperty(), value);
        }
        public void clearActionToPerformed(Element element)
        {
            Profiles.clearValue(element, getActionToPerformedProperty());
        }

        @CheckForNull
        public Element getActionToPerformed(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getActionToPerformedProperty());
        }

        public void setCheckAvaibility(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getCheckAvaibilityProperty(), value);
        }
        public void clearCheckAvaibility(Element element)
        {
            Profiles.clearValue(element, getCheckAvaibilityProperty());
        }

        @CheckForNull
        public Element getCheckAvaibility(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getCheckAvaibilityProperty());
        }

        public void setIsEncapsulated(Element element, @CheckForNull Boolean value)
        {
            Profiles.setValue(element, getStereotype(), getIsEncapsulatedProperty(), value);
        }
        public void clearIsEncapsulated(Element element)
        {
            Profiles.clearValue(element, getIsEncapsulatedProperty());
        }

        @CheckForNull
        public Boolean isIsEncapsulated(Element element)
        {
            return toBoolean(Profiles.getFirstValue(element, getIsEncapsulatedProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            actionToPerformed = null;
            checkAvaibility = null;
            isEncapsulated = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.uiAction().getStereotype());
            }
            return false;
        }

    }
    public static class WithNameSpaceStereotype extends StereotypeWrapper
    {


        //stereotype WithNameSpace and its tags
        public static final String STEREOTYPE_NAME =  "WithNameSpace";
        public static final String NAMESPACE =  "namespace";

        private final OMFMBSWProfile _p;
        @CheckForNull
        private Property namespace;
        protected  WithNameSpaceStereotype(OMFMBSWProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getNamespaceProperty()
        {
            if (namespace == null)
            {
                namespace = getTagByName(getStereotype(), NAMESPACE);
            }
            return namespace;
        }

        public void setNamespace(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getNamespaceProperty(), value);
        }
        public void clearNamespace(Element element)
        {
            Profiles.clearValue(element, getNamespaceProperty());
        }

        @CheckForNull
        public String getNamespace(Element element)
        {
            return toString(Profiles.getFirstValue(element, getNamespaceProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            namespace = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element != null &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element != null)
            {
                OMFMBSWProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.withNameSpace().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(annotationStereotype);
        wrappers.add(codeClassStereotype);
        wrappers.add(codeDocStereotype);
        wrappers.add(codeFileStereotype);
        wrappers.add(enumClassStereotype);
        wrappers.add(featureStereotype);
        wrappers.add(featureItemStereotype);
        wrappers.add(hookStereotype);
        wrappers.add(_interfaceStereotype);
        wrappers.add(liveActionStereotype);
        wrappers.add(methodStereotype);
        wrappers.add(namespacePackageStereotype);
        wrappers.add(optionStereotype);
        wrappers.add(pluginStereotype);
        wrappers.add(uiActionStereotype);
        wrappers.add(withNameSpaceStereotype);
        wrappers.add(classTypeEnumeration);
        wrappers.add(modifiersEnumeration);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(annotationStereotype.getStereotype());
            stereotypes.add(codeClassStereotype.getStereotype());
            stereotypes.add(codeDocStereotype.getStereotype());
            stereotypes.add(codeFileStereotype.getStereotype());
            stereotypes.add(enumClassStereotype.getStereotype());
            stereotypes.add(featureStereotype.getStereotype());
            stereotypes.add(featureItemStereotype.getStereotype());
            stereotypes.add(hookStereotype.getStereotype());
            stereotypes.add(_interfaceStereotype.getStereotype());
            stereotypes.add(liveActionStereotype.getStereotype());
            stereotypes.add(methodStereotype.getStereotype());
            stereotypes.add(namespacePackageStereotype.getStereotype());
            stereotypes.add(optionStereotype.getStereotype());
            stereotypes.add(pluginStereotype.getStereotype());
            stereotypes.add(uiActionStereotype.getStereotype());
            stereotypes.add(withNameSpaceStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:3824D26DC7D1F54C38EA412C2344A04A