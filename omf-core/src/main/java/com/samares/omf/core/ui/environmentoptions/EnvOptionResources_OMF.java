/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.environmentoptions;

import com.nomagic.magicdraw.resources.ResourceManager;

/**
 * Resource handler class.
 * This class is an interface to MagicDraw ResourceManager.
 *
 * @author Mindaugas Genutis
 */
public final class EnvOptionResources_OMF {
    /**
     * Resource bundle name.
     */
    public static final String BUNDLE_NAME = "om.samares.samarescore.ui.envoptions.EnvOptionResources_OMF";

    /**
     * Constructs this resource handler.
     */
    private EnvOptionResources_OMF() {
        // do nothing.
    }

    /**
     * Gets resource by key.
     *
     * @param key key by which to get the resource.
     * @return translated resource.
     */
    public static String getString(String key) {
        return ResourceManager.getStringFor(key, EnvOptionResources_OMF.class.getName(), EnvOptionResources_OMF.class.getClassLoader());
    }
}
