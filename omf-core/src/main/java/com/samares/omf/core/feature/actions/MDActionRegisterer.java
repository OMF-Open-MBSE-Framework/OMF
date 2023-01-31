package com.samares.omf.core.feature.actions;

import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares.omf.core.actions.v2.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.IFeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.ui.OMFDiagramConfigurator;
import com.samares.omf.core.ui.OMFMainMenuConfigurator;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;

public class MDActionRegisterer implements IFeatureRegisterer {
    private final OMFBrowserConfigurator browserConfigurator;
    private final OMFDiagramConfigurator diagramConfigurator;
    private final OMFMainMenuConfigurator menuConfigurator;


    public MDActionRegisterer(OMFBrowserConfigurator browserConfigurator, OMFDiagramConfigurator diagramConfigurator, OMFMainMenuConfigurator menuConfigurator) {
        this.browserConfigurator = browserConfigurator;
        this.diagramConfigurator = diagramConfigurator;
        this.menuConfigurator    = menuConfigurator;
    }

    public void registerFeature(MDFeature mdFeature) {
       try{
            registerMDAction(mdFeature);
            refreshConfigurators();
       }catch (Exception e){
           OMFErrorHandler.handleException(new FeatureException(
                   "[Feature Registerer] Unable to register MDAction for feature: " + mdFeature.getName(),
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

    public void refreshMainMenuConfigurator(){
        if(menuConfigurator != null)
            menuConfigurator.configure(ActionsProvider.getInstance().getMainMenuActions());
    }

    private void registerMDAction(MDFeature mdFeature) {
        if(browserConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO BROWSER CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            browserConfigurator.addNewAction(mdFeature.getMDActions());

        if(diagramConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO DIAGRAM CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            diagramConfigurator.addNewAction(mdFeature.getMDActions());

        if(menuConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO MAIN MENU CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            menuConfigurator.addNewAction(mdFeature.getMDActions());

    }

    private void unregisterMDAction(MDFeature mdFeature) {
        if(browserConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO BROWSER CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            browserConfigurator.removeAction(mdFeature.getMDActions());

        if(diagramConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO DIAGRAM CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            diagramConfigurator.removeAction(mdFeature.getMDActions());

        if(menuConfigurator == null)
            OMFErrorHandler.handleException(new FeatureException("[Feature] NO MAIN MENU CONFIGURATOR REGISTERED", GenericException.ECriticality.CRITICAL), false);
        else
            menuConfigurator.removeAction(mdFeature.getMDActions());
    }



    public OMFBrowserConfigurator getBrowserConfigurator() {
        return browserConfigurator;
    }

    public OMFDiagramConfigurator getDiagramConfigurator() {
        return diagramConfigurator;
    }

    public OMFMainMenuConfigurator getMenuConfigurator() {
        return menuConfigurator;
    }

    public void unregisterFeature(MDFeature mdFeature) {
        try {
            resetConfigurators();
            unregisterMDAction(mdFeature);
            refreshConfigurators();
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException(
                    "[Feature Registerer] Unable to unregister MDAction for feature: " + mdFeature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }
}
