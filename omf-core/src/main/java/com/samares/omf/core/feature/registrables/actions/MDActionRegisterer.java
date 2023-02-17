/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.actions;

import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.FeatureItemRegisterer;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
import com.samares.omf.core.errors.exceptions.GenericException;

import java.util.List;
import java.util.Objects;

public class MDActionRegisterer extends FeatureItemRegisterer<IUIAction> {
    private final OMFBrowserConfigurator browserConfigurator;
    private final OMFDiagramConfigurator diagramConfigurator;
    private final OMFMainMenuConfigurator menuConfigurator;

    public MDActionRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
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

    private void  resetConfigurators() {
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
            browserConfigurator.addNewAction((AUIAction) action);

        if(diagramConfigurator != null)
            diagramConfigurator.addNewAction((AUIAction) action);

        if(menuConfigurator != null)
            menuConfigurator.addNewAction((AUIAction) action);
    }

    public void unregisterFeatureItem(IUIAction action) {
        if(browserConfigurator != null)
            browserConfigurator.removeAction((AUIAction) action);

        if(diagramConfigurator != null)
            diagramConfigurator.removeAction((AUIAction) action);

        if(menuConfigurator != null)
            menuConfigurator.removeAction((AUIAction) action);
    }
}
