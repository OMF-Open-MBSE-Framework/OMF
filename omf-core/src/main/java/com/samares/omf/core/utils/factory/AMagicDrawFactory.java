/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFUtils;

import java.util.Objects;

public abstract class AMagicDrawFactory {
    protected Project project;

    public void reInitFactory(String className){
        reInitFactory(className, OMFUtils.currentProject);
    }

    public void reInitFactory(String className, Project project) {
        setProject(project);
        ColorPrinter.status(className + " reinitialized for " + project.getName());
    }

    // General element operations
    public void removeElement(Element element) {
        try {
            ModelElementsManager.getInstance().removeElement(element);
        } catch (ReadOnlyElementException e) {
            throw new RuntimeException(e);
        }
    }
    public ElementsFactory getMagicDrawFactory() {
        return project.getElementsFactory();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = Objects.requireNonNull(project, "Trying to use a magicdraw factory while specifying a null" +
                "project");
    }
}
