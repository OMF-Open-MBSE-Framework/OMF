/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo.actions.ImportFromGPT;

import java.util.Arrays;
import java.util.List;

public class SysmlGptExploFeature extends SimpleFeature {

    public static final String GPT_GENERATED_JSON_TO_IMPORT = "GPT generated JSON to import";

    public SysmlGptExploFeature() {
        super("Sysml gpt explo");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new ImportFromGPT()
        );
    }

    @Override
    protected List<Option> initProjectOnlyOptions() {
        var projectOpt = createEnvOption(
                new StringProperty(GPT_GENERATED_JSON_TO_IMPORT,
                        "C:\\Users\\HugoStinson\\OneDrive - SAMARES ENGINEERING\\Bureau\\gptMagicdrawTest.json"),
                "Sysml gpt explo");

        return Arrays.asList(projectOpt);
    }
}
