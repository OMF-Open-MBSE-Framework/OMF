/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.stereotypes.utils;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.OpaqueBehavior;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.StateMachine;

public enum String2Class {
    CLASS("Class", Class.class),
    ACTION("Action", Class.class),
    ACTIVITY("Activity", Class.class),
    PROPERTY("Class", Class.class),
    PACKAGE("Package", Package.class),
    ELEMENT("Element", Element.class),
    DATATYPE("DataType", DataType.class),
    STATEMACHINE("StateMachine", StateMachine.class),
    OPAQUEBEHAVIOR("OpaqueBehavior", OpaqueBehavior.class);

    public final String className;
    public final java.lang.Class classValue;

    String2Class(String className, java.lang.Class classValue) {
        this.className = className;
        this.classValue = classValue;
    }

    public String getClassName() {
        return className;
    }

    public java.lang.Class getClassValue() {
        return classValue;
    }
}
