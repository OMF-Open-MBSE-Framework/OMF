/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer;

import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.IFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.OMFMainMenuConfigurator;

import java.util.List;
import java.util.Objects;

public class MDActionRegisterer implements IFeatureItemRegisterer<IUIAction> {
    private OMFBrowserConfigurator browserConfigurator;
    private OMFDiagramConfigurator diagramConfigurator;
    private OMFMainMenuConfigurator menuConfigurator;
    private FeatureRegisterer featureRegisterer;


    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        this.browserConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getBrowserConfigurator(),
                "NO BROWSER CONFIGURATOR REGISTERED");
        this.diagramConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getDiagramConfigurator(),
                "NO DIAGRAM CONFIGURATOR REGISTERED");
        this.menuConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getMenuConfigurator(),
                "NO MENU CONFIGURATOR REGISTERED");
    }

    public void registerFeatureItems(List<IUIAction> actions) {
        if (actions == null) {
            throw new OMFFeatureRegisteringException(
                    "Trying to register actions but passed action list is null");
        }
        try {
            actions.forEach(this::registerFeatureItem);
            refreshConfigurators();
        } catch (Exception e) {
            throw new OMFFeatureRegisteringException("Unable to register MDActions", e);
        }
    }

    public void unregisterFeatureItems(List<IUIAction> actions) {
        if (actions == null) {
            throw new OMFFeatureRegisteringException("Trying to unregister actions but passed action list is null");
        }
        try {
            resetConfigurators();
            actions.forEach(this::unregisterFeatureItem);
            refreshConfigurators();
        } catch (Exception e) {
            throw new OMFFeatureRegisteringException("Unable to unregister MDActions", e);
        }
    }

    private void resetConfigurators() {
        if (menuConfigurator != null)
            menuConfigurator.resetMDActions(ActionsProvider.getInstance().getMainMenuActions());
    }

    private void refreshConfigurators() {
//        browserConfigurator.configure(ActionsProvider.getInstance().getDiagramContextActions());
//        diagramConfigurator.configure(ActionsProvider.getInstance().getContainmentBrowserShortcutsActions();
        refreshMainMenuConfigurator();
    }

    private void refreshMainMenuConfigurator() {
        if (menuConfigurator != null)
            menuConfigurator.configure(ActionsProvider.getInstance().getMainMenuActions());
    }

    public void registerFeatureItem(IUIAction action) {
        if (browserConfigurator != null)
            browserConfigurator.addNewAction((AUIAction) action);

        if (diagramConfigurator != null)
            diagramConfigurator.addNewAction((AUIAction) action);

        if (menuConfigurator != null)
            menuConfigurator.addNewAction((AUIAction) action);
    }

    public void unregisterFeatureItem(IUIAction action) {
        if (browserConfigurator != null)
            browserConfigurator.removeAction((AUIAction) action);

        if (diagramConfigurator != null)
            diagramConfigurator.removeAction((AUIAction) action);

        if (menuConfigurator != null)
            menuConfigurator.removeAction((AUIAction) action);
    }

    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getUIActions());
    }

    @Override
    public void unregisterFeatureItems(MDFeature feature) {
        unregisterFeatureItems(feature.getUIActions());
    }


    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
}
