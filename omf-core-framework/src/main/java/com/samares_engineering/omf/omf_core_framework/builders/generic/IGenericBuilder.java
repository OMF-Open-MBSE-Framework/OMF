/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.generic;


import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;

public interface IGenericBuilder<ConcreteBuiltElement> {

    ConcreteBuiltElement build() throws BuilderException;

}
