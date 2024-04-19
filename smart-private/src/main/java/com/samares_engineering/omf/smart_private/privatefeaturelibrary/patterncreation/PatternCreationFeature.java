/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation;

import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.ILiveAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.LiveAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.actions.GenerateTemplateFromElement;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.actions.RefreshPatternConfiguration;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.creation.ElementCreatorFromPattern;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.profile.PatternCreatorProfile;

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
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new RefreshPatternConfiguration(),
                new GenerateTemplateFromElement()
        );
    }

    @Override
    public List<ILiveAction> initLiveActions() {
        ILiveAction creationRE = new LiveAction(RECategoryEnum.CREATE);
        creationRE.addRule(new ElementCreatorFromPattern());
        return List.of(creationRE);
    }

    Set<Stereotype> configuredSTR;

    public Set<Stereotype> getConfiguredSTR() {
        return configuredSTR;
    }

    public void refreshPatterConfiguration() {
        configuredSTR= Finder.byTypeRecursively().find(OMFUtils.getProject(), new Class[]{Dependency.class})
                .stream()
                .filter(PatternCreatorProfile.getInstance().onCreation()::is)
                .map(Dependency.class::cast)
                .map(Dependency::getSource)
                .flatMap(Collection::stream)
                .map(Stereotype.class::cast)
                .collect(Collectors.toSet());
    }
}
