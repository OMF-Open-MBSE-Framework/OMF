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

import java.util.List;

public class MDActionRegisterer extends FeatureItemRegisterer<IUIAction> {
    private final OMFBrowserConfigurator browserConfigurator;
    private final OMFDiagramConfigurator diagramConfigurator;
    private final OMFMainMenuConfigurator menuConfigurator;


    public MDActionRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
        this.browserConfigurator = featureRegisterer.getPlugin().getFeatureRegisteringBrowserConfigurator();
        this.diagramConfigurator = featureRegisterer.getPlugin().getFeatureRegisteringDiagramConfigurator();
        this.menuConfigurator = featureRegisterer.getPlugin().getFeatureRegisteringMainMenuConfigurator();
    }

    public void register(MDFeature mdFeature) {
       try{
            registerMDAction(mdFeature.getUIActions());
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
            unregisterMDAction(mdFeature.getUIActions());
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
    
    private void registerMDAction(List<IUIAction> actions) {
        if(browserConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO BROWSER CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            browserConfigurator.addNewActions(actions);

        if(diagramConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO DIAGRAM CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            diagramConfigurator.addNewActions(actions);

        if(menuConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO MAIN MENU CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            menuConfigurator.addNewActions(actions);

    }

    private void unregisterMDAction(List<IUIAction> action) {
        if(browserConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO BROWSER CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            browserConfigurator.removeActions(action);

        if(diagramConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO DIAGRAM CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            diagramConfigurator.removeActions(action);

        if(menuConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO MAIN MENU CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            menuConfigurator.removeActions(action);
    }
}
