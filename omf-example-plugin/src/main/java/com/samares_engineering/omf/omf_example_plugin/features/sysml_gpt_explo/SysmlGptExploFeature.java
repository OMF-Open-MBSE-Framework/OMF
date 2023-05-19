/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo.actions.ImportFromGPT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SysmlGptExploFeature extends AFeature {

    public static final String GPT_GENERATED_JSON_TO_IMPORT = "GPT generated JSON to import";

    public SysmlGptExploFeature() {
        super("Sysml gpt explo");
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return List.of(
                new ImportFromGPT()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IOption> initOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        var projectOpt = new OptionImpl(
                new StringProperty(GPT_GENERATED_JSON_TO_IMPORT,
                        "C:\\Users\\HugoStinson\\OneDrive - SAMARES ENGINEERING\\Bureau\\gptMagicdrawTest.json"),
                "Sysml gpt explo",
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Environment);

        return Arrays.asList(projectOpt);
    }
}
