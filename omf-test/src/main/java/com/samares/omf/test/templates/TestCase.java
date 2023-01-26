/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.templates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD})
public @interface TestCase {

    // ADD MODEL COMPARATOR ANNOTATION
    String name = "";

}
