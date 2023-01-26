/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.builders.generic;


import com.samares.omf.core.utils.builders.exceptions.BuilderException;

public interface IGenericBuilder<ConcreteBuiltElement> {

    ConcreteBuiltElement build() throws BuilderException;

}
