/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation;

import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RuleEngine;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.patterncreation.actions.*;
import com.samares_engineering.omf.omf_public_features.patterncreation.creation.ElementCreatorFromPattern;
import com.samares_engineering.omf.omf_public_features.patterncreation.profile.PatternCreatorProfile;

import java.util.*;
import java.util.stream.Collectors;

public class PatternCreationFeature extends SimpleFeature {

    public PatternCreationFeature() {
        super("PATTERN CREATION FEATURE");
        configuredSTR = new HashSet<>();
    }

    @Override
    public void onProjectOpen() {
        super.onProjectOpen();
        refreshPatterConfiguration();
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new RefreshPatternConfiguration(),
                new GenerateTemplateFromSTR(),
                new GenerateTemplateFromElement(),
                new ReplaceA_with_B(),
                new ReplaceB_with_A()
        );
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        IRuleEngine creationRE = new RuleEngine(RECategoryEnum.CREATE);
        creationRE.addRule(new ElementCreatorFromPattern());
        return List.of(creationRE);
    }

    Set<Stereotype> configuredSTR;

    public Set<Stereotype> getConfiguredSTR() {
        return configuredSTR;
    }

    public void refreshPatterConfiguration() {
        configuredSTR= Finder.byTypeRecursively().find(OMFUtils.currentProject, new java.lang.Class[]{Dependency.class})
                .stream()
                .filter(PatternCreatorProfile.getInstance().onCreation()::is)
                .map(Dependency.class::cast)
                .map(Dependency::getSource)
                .flatMap(Collection::stream)
                .map(Stereotype.class::cast)
                .collect(Collectors.toSet());
    }
}
