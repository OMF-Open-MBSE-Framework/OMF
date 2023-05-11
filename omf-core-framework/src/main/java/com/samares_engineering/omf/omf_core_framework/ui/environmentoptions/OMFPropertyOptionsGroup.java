/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.environmentoptions;

public class OMFPropertyOptionsGroup extends APropertyOptionsGroup {

    /**
     * ID of the example options group.
     */
    public static final String DEFAULT_ID = "env.options.omf.conf";
    private static final String DEFAULT_OMF_PLUGIN_CATEGORY_NAME = "OMF Plugin";

    public static OMFPropertyOptionsGroup instance = null;

    /**
     * Constructs this options group.
     */
    public OMFPropertyOptionsGroup() {
        this(DEFAULT_ID, DEFAULT_OMF_PLUGIN_CATEGORY_NAME);
    }
    public OMFPropertyOptionsGroup(String name) {
        this(DEFAULT_ID, name);
    }
    public OMFPropertyOptionsGroup(String ID, String categoryName) {
        super(ID, categoryName);
    }

    public static OMFPropertyOptionsGroup getInstance() {
        if (null == instance){
            instance = new OMFPropertyOptionsGroup();
        }
        return instance;
    }

}
