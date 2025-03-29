/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders;


import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

public class BetaFactory {
    public static ElementsFactory magicDrawFactory;
    private static BetaFactory instance = new BetaFactory();

    private BetaFactory() {
        magicDrawFactory = OMFUtils.getProject().getElementsFactory();
    }

    public static BetaFactory getInstance() {
        if (null == instance)
            instance = new BetaFactory();
        return instance;
    }


    public static void reInitFactory() {
        instance = new BetaFactory();
    }
}
