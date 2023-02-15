/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.actions;

import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.FeatureItemRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;

import java.util.Objects;

public class MDActionRegisterer extends FeatureItemRegisterer<IUIAction> {
    private final OMFBrowserConfigurator browserConfigurator;
    private final OMFDiagramConfigurator diagramConfigurator;
    private final OMFMainMenuConfigurator menuConfigurator;

    public MDActionRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
        this.browserConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getFeatureRegisteringBrowserConfigurator(),
                "NO BROWSER CONFIGURATOR REGISTERED");
        this.diagramConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getFeatureRegisteringDiagramConfigurator(),
                "NO DIAGRAM CONFIGURATOR REGISTERED");
        this.menuConfigurator = Objects.requireNonNull(
                featureRegisterer.getPlugin().getFeatureRegisteringMainMenuConfigurator(),
                "NO MENU CONFIGURATOR REGISTERED");
    }

    public void register(MDFeature mdFeature) {
       try{
            mdFeature.getUIActions().forEach(action -> {
                action.setFeature(mdFeature);
                this.registerFeatureItem(action);
            });
            refreshConfigurators();
       } catch (Exception e){
           OMFErrorHandler.handleException(new FeatureException(
                   "[Feature Registerer] Unable to register MDAction for feature: " + mdFeature.getName(),
                   e, GenericException.ECriticality.CRITICAL), false);
       }
    }

    public void unregister(MDFeature mdFeature) {
        try {
            resetConfigurators();
            mdFeature.getUIActions().forEach(this::unregisterFeatureItem);
            refreshConfigurators();
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException(
                    "[Feature Registerer] Unable to unregister MDAction for feature: " + mdFeature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    private void resetConfigurators() {
        if(menuConfigurator != null)
            menuConfigurator.resetMDActions(ActionsProvider.getInstance().getMainMenuActions());
    }

    private void refreshConfigurators() {
//        browserConfigurator.configure(ActionsProvider.getInstance().getDiagramContextActions());
//        diagramConfigurator.configure(ActionsProvider.getInstance().getContainmentBrowserShortcutsActions();
        refreshMainMenuConfigurator();
    }

    private void refreshMainMenuConfigurator(){
        if(menuConfigurator != null)
            menuConfigurator.configure(ActionsProvider.getInstance().getMainMenuActions());
    }
    
    public void registerFeatureItem(IUIAction action) {
        if(browserConfigurator != null)
            browserConfigurator.addNewAction(action);

        if(diagramConfigurator != null)
            diagramConfigurator.addNewAction(action);

        if(menuConfigurator != null)
            menuConfigurator.addNewAction(action);
    }

    public void unregisterFeatureItem(IUIAction action) {
        if(browserConfigurator != null)
            browserConfigurator.removeAction(action);

        if(diagramConfigurator != null)
            diagramConfigurator.removeAction(action);

        if(menuConfigurator != null)
            menuConfigurator.removeAction(action);
    }
}
