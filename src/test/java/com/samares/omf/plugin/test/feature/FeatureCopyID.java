/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.feature;

import com.jidesoft.plaf.basic.UIAction;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.AFeature;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.plugin.test.feature.mdActions.CopyElementIDAction;
import com.samares.omf.plugin.test.feature.mdActions.SaveToLocalMDA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeatureCopyID extends AFeature {

    public FeatureCopyID(APlugin plugin) {
        super(plugin,"Copy Element ID");
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new CopyElementIDAction(),
                new SaveToLocalMDA());
    }

    @Override
    public List<IFeatureRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOption> initOptions() {return Collections.EMPTY_LIST;}


}
