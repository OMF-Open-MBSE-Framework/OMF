/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.exportdiagram;

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_example_plugin.features.exportdiagram.options.ExportDiagramOptionHelper;
import com.samares_engineering.omf.omf_example_plugin.features.exportdiagram.actions.SaveDiagramAsSVG;

import java.util.List;

/**
 *  Feature that can be used to export diagram images.
 */
public class ExportDiagramImagesFeature extends SimpleFeature {

    public ExportDiagramImagesFeature(){
       super("Export Diagram Images");
    }


    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new SaveDiagramAsSVG()
        );
    }

    @Override
    protected List<Option> initOptions() {
        return getEnvOptionsHelper().getAllOptions();
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new ExportDiagramOptionHelper(this);
    }

    @Override
    public ExportDiagramOptionHelper getEnvOptionsHelper() {
        return (ExportDiagramOptionHelper) super.getEnvOptionsHelper();
    }
}
