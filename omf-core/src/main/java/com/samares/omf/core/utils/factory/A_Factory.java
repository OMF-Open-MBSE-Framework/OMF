/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFUtils;

public class A_Factory {

    public static ElementsFactory magicDrawFactory;

    public A_Factory(){
        this(OMFUtils.currentProject);
    }

    public A_Factory(Project project){
        magicDrawFactory = project.getElementsFactory();
    }

    public void reInitFactory(String className){
        reInitFactory(className, OMFUtils.currentProject);
    }

    private void reInitFactory(String className, Project project) {
        magicDrawFactory = project.getElementsFactory();
        ColorPrinter.status(className + " reinitialized for " + project.getName());
    }
}
