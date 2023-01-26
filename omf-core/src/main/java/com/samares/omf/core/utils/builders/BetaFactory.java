/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.builders;


import com.nomagic.uml2.impl.ElementsFactory;
import com.samares.omf.core.utils.OMFUtils;

public class BetaFactory {
    public static ElementsFactory magicDrawFactory;
    private static BetaFactory instance = new BetaFactory();

    private BetaFactory() {
        magicDrawFactory = OMFUtils.currentProject.getElementsFactory();
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
