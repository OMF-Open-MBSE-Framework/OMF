package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.PluginArchitectureProfile;

public class PluginArchitectureFactory {

    private static PluginArchitectureFactory instance;
    private static ElementsFactory factory;

    private PluginArchitectureFactory(){
        factory = OMFUtils.currentProject.getElementsFactory();
    }


    public static PluginArchitectureFactory getInstance(){
        if(instance == null)
            instance = new PluginArchitectureFactory();
        return instance;
    }


    public void createFunctionCall(Class classSrc, Operation operation, String parameterName, String parameterType) {
        Property functionCall = factory.createPropertyInstance();
        functionCall.setOwner(classSrc);
        functionCall.setName(operation.getName());
        PluginArchitectureProfile.getInstance().methods().apply(functionCall);
    }

    public void createParameter(Operation operation, String parameterName, ParameterDirectionKind kind) {
        createParameter(operation, parameterName, null, kind);
    }
    public void createParameter(Operation operation, String parameterName, Type parameterType, ParameterDirectionKind kind) {
        Parameter returnParameter = factory.createParameterInstance();
        returnParameter.setName(parameterName);
        returnParameter.setDirection(kind);
        returnParameter.setOperation(operation);
        returnParameter.setOwner(operation);
        if (parameterType != null)
            returnParameter.setType(parameterType);
    }
}
