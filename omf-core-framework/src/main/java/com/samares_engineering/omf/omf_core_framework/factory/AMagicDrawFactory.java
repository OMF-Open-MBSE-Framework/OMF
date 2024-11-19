/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.Objects;

public abstract class AMagicDrawFactory {
    protected Project project;

    public void reInitFactory(String className){
        reInitFactory(className, OMFUtils.getProject());
    }

    public void reInitFactory(String className, Project project) {
        setProject(project);
        SysoutColorPrinter.status(className + " reinitialized for " + project.getName());
    }

    // General element operations
    public void removeElement(Element element) {
        try {
            ModelElementsManager.getInstance().removeElement(element);

        } catch (ReadOnlyElementException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the magicdraw factory, if the project is null, throw an exception
     * @return the magicdraw factory
     */
    public ElementsFactory getMagicDrawFactory() {
        Objects.requireNonNull(project, "Trying to use a magicdraw factory while specifying a null" +
                "project");
        return project.getElementsFactory();
    }

    /**
     * Get the project
     * @return the project
     */

    public Project getProject() {
        return project;
    }

    /**
     * Set the project, if the project is null, log a warning.
     * @param project the project to set
     */
    public void setProject(Project project) {
        if(project == null){
            SysoutColorPrinter.warn("Trying to set a null project to the factory: " + getClass().getSimpleName());
        }
        this.project = project;
    }
}
