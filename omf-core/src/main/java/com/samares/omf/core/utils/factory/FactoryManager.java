/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.factory;

import com.nomagic.magicdraw.core.Project;
import com.samares.omf.core.utils.OMFUtils;

import java.util.Arrays;
import java.util.List;

public class FactoryManager {
    public static List<A_Factory> l_factory = Arrays.asList(
            OMFHelper.getInstance(),
            SysMLFactory.getInstance()
    );

    public static void initAllFactories(){
        initAllFactories(OMFUtils.currentProject);
    }

    public static void initAllFactories(Project project){
        l_factory.forEach(factory -> factory.reInitFactory(factory.getClass().getSimpleName()));
    }
}
