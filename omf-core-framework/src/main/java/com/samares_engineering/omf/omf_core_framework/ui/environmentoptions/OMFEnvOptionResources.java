/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.environmentoptions;

import com.nomagic.magicdraw.resources.ResourceManager;

/**
 * Resource handler class.
 * This class is an interface to MagicDraw ResourceManager.
 *
 * @author Mindaugas Genutis
 */
public final class OMFEnvOptionResources {
    /**
     * Resource bundle name.
     */
    public static final String BUNDLE_NAME = "om.samares.samarescore.ui.envoptions.EnvOptionResources_OMF";

    /**
     * Constructs this resource handler.
     */
    private OMFEnvOptionResources() {
        // do nothing.
    }

    /**
     * Gets resource by key.
     *
     * @param key key by which to get the resource.
     * @return translated resource.
     */
    public static String getString(String key) {
        return ResourceManager.getStringFor(key, OMFEnvOptionResources.class.getName(), OMFEnvOptionResources.class.getClassLoader());
    }
}
