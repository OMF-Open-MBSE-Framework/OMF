/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly;

import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;

import java.util.List;
import java.util.Objects;

/**
 * This class is used to register and unregister UIActions from MDFeatures.
 * It is used by the {@link MDFeature} class.
 * It is used to register MDActions that are only available in the context of a project.
 * It is used to register MDActions that are available in the context of a project and a diagram.
 * It is used to register MDActions that are available in the context of a project and a browser.
 * It is used to register MDActions that are available in the context of a project and a menu.
 */
public class ProjectOnlyMDActionRegisterer implements IProjectOnlyFeatureItemRegisterer<IUIAction> {
    private OMFBrowserConfigurator browserConfigurator;
    private OMFDiagramConfigurator diagramConfigurator;
    private OMFMainMenuConfigurator menuConfigurator;
    private FeatureRegisterer featureRegister;

    /**
     * Initialize the registerer with the feature registerer.
     * This method is called by the {@link FeatureRegisterer} class.
     * It requires Configurators to be registered in the plugin:
     * a {@link OMFBrowserConfigurator},
     * a {@link OMFDiagramConfigurator}
     * and a {@link OMFMainMenuConfigurator}.
     * @param featureRegisterer
     */
    public void init(FeatureRegisterer featureRegisterer) {
        this.featureRegister = featureRegisterer;
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
    /**
     * Register a list of UIActions and refresh the configurators.
     * @param actions
     * @throws FeatureException
     */
    public void registerFeatureItems(List<IUIAction> actions) throws FeatureException {
       try{
           actions.forEach(this::registerFeatureItem);
           refreshConfigurators();
       } catch (Exception e){
           throw new FeatureException(
                   "[Feature Registerer] Unable to register MDActions",
                   e, GenericException.ECriticality.CRITICAL);
       }
    }

    /**
     * Unregister a list of UIActions and refresh the configurators.
     * @param actions
     * @throws FeatureException
     */
    public void unregisterFeatureItems(List<IUIAction> actions) throws FeatureException {
        try {
            resetConfigurators();
            actions.forEach(this::unregisterFeatureItem);
            refreshConfigurators();
        } catch (Exception e){
           throw new FeatureException(
                    "[Feature Registerer] Unable to unregister MDActions",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }

    /**
     * Reset the configurators to their initial state.
     */
    private void resetConfigurators() {
        if(menuConfigurator != null)
            menuConfigurator.resetMDActions(ActionsProvider.getInstance().getMainMenuActions());
    }
    /**
     * Refresh the configurators with the new actions.
     */
    private void refreshConfigurators() {
//        browserConfigurator.configure(ActionsProvider.getInstance().getDiagramContextActions());
//        diagramConfigurator.configure(ActionsProvider.getInstance().getContainmentBrowserShortcutsActions();
        refreshMainMenuConfigurator();
    }
    /**
     * Refresh the main menu configurator with the new actions.
     */
    private void refreshMainMenuConfigurator(){
        if(menuConfigurator != null)
            menuConfigurator.configure(ActionsProvider.getInstance().getMainMenuActions());
    }

    /**
     * Register a UIAction in the configurators.
     * @param action
     */
    public void registerFeatureItem(IUIAction action) {
        if(browserConfigurator != null)
            browserConfigurator.addNewAction((AUIAction) action);

        if(diagramConfigurator != null)
            diagramConfigurator.addNewAction((AUIAction) action);

        if(menuConfigurator != null)
            menuConfigurator.addNewAction((AUIAction) action);
    }

    /**
     * Unregister a UIAction in the configurators.
     * @param action
     */
    public void unregisterFeatureItem(IUIAction action) {
        if(browserConfigurator != null)
            browserConfigurator.removeAction((AUIAction) action);

        if(diagramConfigurator != null)
            diagramConfigurator.removeAction((AUIAction) action);

        if(menuConfigurator != null)
            menuConfigurator.removeAction((AUIAction) action);
    }

    @Override
    public void registerFeature(MDFeature feature) throws FeatureException {
        registerFeatureItems(feature.getProjectOnlyUIActions());
    }

    @Override
    public void unregisterFeature(MDFeature feature) throws FeatureException {
        unregisterFeatureItems(feature.getProjectOnlyUIActions());
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegister;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegister = featureRegisterer;
    }
}
