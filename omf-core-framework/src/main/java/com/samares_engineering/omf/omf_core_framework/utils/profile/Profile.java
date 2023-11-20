/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.profile;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.sysml.util.MDCustomizationForSysMLProfile;
import com.nomagic.uml2.MagicDrawProfile;
import com.nomagic.uml2.StandardProfile;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

public class Profile {
    private Project project;
    private static Profile instance = null;

    public Profile(Project project){
        this.project = project;
    }

    public static Profile getInstance(){
        return getInstance(OMFUtils.getProject());
    }

    public static Profile getInstance(Project project){
        if(instance == null) {
            instance = new Profile(project);
            instance.init(project);
        }
        if(project != instance.project) {
            instance.init(project);
        }
        return instance;
    }

    public void init(Project project){
        this.project = project;
        OMFConstants.reinitConstants();
    }

    public MagicDrawProfile getMagicDraw() {
        try{return MagicDrawProfile.getInstanceByProject(project);}
        catch (Exception e){OMFErrorHandler.handleException(new OMFException("MagicDraw profile not found", e, GenericException.ECriticality.CRITICAL));}
        return null;
    }

    public StandardProfile getStandard() {
        try{return StandardProfile.getInstanceByProject(project);}
        catch (Exception e){OMFErrorHandler.handleException(new OMFException("Standard profile not found", e, GenericException.ECriticality.CRITICAL));}
        return null;
    }

    public SysMLProfile getSysml() {
        try{ return SysMLProfile.getInstanceByProject(project);}
        catch (Exception e){OMFErrorHandler.handleException(new OMFException("SysML profile not found", e, GenericException.ECriticality.CRITICAL));}
        return null;
    }

    public MDCustomizationForSysMLProfile getMDCustomSysml() {
         try{return MDCustomizationForSysMLProfile.getInstanceByProject(project);}
         catch (Exception e){OMFErrorHandler.handleException(new OMFException("SysMLCustomization profile not found", e, GenericException.ECriticality.CRITICAL));}
         return null;
    }


    public MDCustomizationForSysMLProfile getSysmlAdditionalStereotypes() {
         try{return MDCustomizationForSysMLProfile.getInstanceByProject(project);}
         catch (Exception e){OMFErrorHandler.handleException(new OMFException("SysMLCustomization profile not found", e, GenericException.ECriticality.CRITICAL));}
         return null;
    }

    public static SysMLProfile _getSysml() {return getInstance().getSysml();}

    public static MDCustomizationForSysMLProfile _getMDCustomSysml() {return getInstance().getMDCustomSysml();}

    public static MDCustomizationForSysMLProfile _getSysmlAdditionalStereotypes() {return getInstance().getSysmlAdditionalStereotypes();}

    public static MagicDrawProfile _getMagicDraw() {return getInstance().getMagicDraw();}

    public static StandardProfile _getStandard() {return getInstance().getStandard();}



    public Project getProject() {
        return project;
    }



}
