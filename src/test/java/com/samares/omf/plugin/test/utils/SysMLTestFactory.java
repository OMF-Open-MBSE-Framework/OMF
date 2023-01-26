/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.utils;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.profile.Profile;
import com.samares.omf.test.BatchLauncher;
import org.junit.Assert;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SysMLTestFactory {

    private static SysMLTestFactory instance;
    private Project project;
    private ElementsFactory mdFactory;

    public SysMLTestFactory() {

    }

    /**
     * get the instance of test Factory. If a project is given it will refresh the configuration with it.
     *
     * @param project
     * @return TestFactory singleton
     */
    public static SysMLTestFactory getInstance(Project project) {
        OMFUtils.currentProject = project;
        if (instance == null) {
            instance = new SysMLTestFactory();
        }
        instance.project = project;
        instance.mdFactory = project.getElementsFactory();
        return instance;
    }

    /**
     * get the instance of test Factory initialized with the project opened in the current batch.
     *
     * @return TestFactory singleton
     */
    public static SysMLTestFactory getInstance() {
        return getInstance(BatchLauncher.currentBatch.getInitProject());
    }

    /**
     * Create a port stereotyped 'proxy port' and others attributes under the given element.
     *
     * @param owner future port owner
     * @return a new proxy port
     */
    public Port createProxyPort(Element owner) {
        Port portToCreate = mdFactory.createPortInstance();
        Profile.getInstance(project).get_Sysml().proxyPort().apply(portToCreate);
        portToCreate.setOwner(owner);
        return portToCreate;
    }


    /**
     * Create simple connection without PartWith Port and PropertyPath (e.g Part2Part sister connection)
     *
     * @param src             can be Port or PartProperty
     * @param dst             can be Port or PartProperty
     * @param ownerConnection Common ancestor of src && dst
     * @return createdConnector
     */
    public Connector createConnection(Property src, Property dst, Element ownerConnection) {
        return createConnection(src, dst, new ArrayList<>(), new ArrayList<>(), ownerConnection);
    }

    public Connector createConnection(Property src, Property dst,
                                      List<Property> src_propertyPath, List<Property> dst_propertyPath,
                                      Element ownerConnection) {

        return createConnection(src, dst,
                src_propertyPath, dst_propertyPath,
                null, null,
                ownerConnection);
    }

    public Connector createConnection(@CheckForNull Property src, @CheckForNull Property dst,
                                      List<Property> srcPropertyPath, List<Property> dstPropertyPath,
                                      Property srcPartWithPort, Property dstPartWithPort,
                                      @CheckForNull Element ownerConnection) {
        Connector connector = mdFactory.createConnectorInstance();

        //SET FIRST END
        ConnectorEnd srcEnd = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        srcEnd.setRole(src);
        srcEnd.setPartWithPort(srcPartWithPort);

        if (srcPropertyPath != null && !srcPropertyPath.isEmpty()) {
            Profile.getSysml().nestedConnectorEnd().apply(srcEnd);
            Profile.getSysml().nestedConnectorEnd().setPropertyPath(srcEnd, srcPropertyPath);
        }

        //SET SECOND END
        ConnectorEnd dstEnd = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");
        dstEnd.setRole(dst);
        dstEnd.setPartWithPort(dstPartWithPort);

        if (dstPropertyPath != null && !dstPropertyPath.isEmpty()) {
            Profile.getSysml().nestedConnectorEnd().apply(dstEnd);
            Profile.getSysml().nestedConnectorEnd().setPropertyPath(dstEnd, dstPropertyPath);
        }

        connector.setOwner(ownerConnection);

        return connector;
    }


    public void removeElement(Element element) {
        try {
            ModelElementsManager.getInstance().removeElement(element);
        } catch (ReadOnlyElementException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFlowDirection(Property flowProperty, SysMLProfile.FlowDirectionKindEnum direction) {
        Assert.assertTrue("[ERROR setting direction] " + this.getClass().getSimpleName() + " element provided is not a flowProperty", Profile.getSysml().flowProperty().is(flowProperty));
        Profile.getSysml().flowProperty().setDirection(flowProperty, direction);
    }
}
