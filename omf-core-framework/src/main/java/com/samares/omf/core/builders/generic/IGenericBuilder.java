/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.builders.generic;


import com.samares.omf.core.builders.exceptions.BuilderException;

public interface IGenericBuilder<ConcreteBuiltElement> {

    ConcreteBuiltElement build() throws BuilderException;

}
