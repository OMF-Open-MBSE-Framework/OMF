package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SysMLHelper {

    /**
     * Gets the port direction calculated using the flow properties directions of the port type.
     * If the port has no type, or if the type has no flow properties, an exception is thrown.
     * @param port the port to get the direction from
     * @return the port direction: IN, OUT or INOUT;
     * @throws LegacyOMFException if the port has no type or if the type has no flow properties
     */
    public static SysMLProfile.FlowDirectionKindEnum getPortDirection(Port port) throws LegacyOMFException {
        Type type = port.getType();
        if(type == null)
            throw new LegacyOMFException("Port has no type", GenericException.ECriticality.ALERT);

        List<Element> flowProperties = type.getOwnedElement().stream()
                .filter(Property.class::isInstance)
                .filter(Profile.getInstance().getSysml().flowProperty()::is)
                .collect(Collectors.toList());

        if(flowProperties.isEmpty())
            throw new LegacyOMFException("Port type has no flow properties", GenericException.ECriticality.ALERT);

        boolean allOut = flowProperties.stream().allMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.OUT));

        if(allOut)
            return SysMLProfile.FlowDirectionKindEnum.OUT;
        boolean allIn = flowProperties.stream().allMatch(flow -> Objects.equals(Profile.getInstance().getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.IN));
        if(allIn)
            return SysMLProfile.FlowDirectionKindEnum.IN;
        return SysMLProfile.FlowDirectionKindEnum.INOUT;
    }

}
