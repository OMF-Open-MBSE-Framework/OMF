/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.factory;

import com.nomagic.magicdraw.core.Project;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactoryManager {
    public static List<AMagicDrawFactory> factories = new ArrayList<>(Arrays.asList(
            OMFFactory.getInstance(),
            SysMLFactory.getInstance()
    ));

    public static void initAllFactories(){
        initAllFactories(OMFUtils.getProject());
    }

    public static void initAllFactories(Project project){
        factories.forEach(factory -> factory.reInitFactory(factory.getClass().getSimpleName(), project));
    }

    public static void addFactory(AMagicDrawFactory factory){
        factories.add(factory);
    }

    public static void removeFactory(AMagicDrawFactory factory){
        factories.remove(factory);
    }

}
