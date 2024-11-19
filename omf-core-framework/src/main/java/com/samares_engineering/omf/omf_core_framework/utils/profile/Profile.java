/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.profile;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.MDCustomizationForSysMLProfile;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.MagicDrawProfile;
import com.nomagic.uml2.StandardProfile;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
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
        catch (Exception e){throw new CoreException2("MagicDraw profile not found", e);}
    }

    public StandardProfile getStandard() {
        try{return StandardProfile.getInstanceByProject(project);}
        catch (Exception e){throw new CoreException2("Standard profile not found", e);}
    }

    public SysMLProfile getSysml() {
        try{ return SysMLProfile.getInstanceByProject(project);}
        catch (Exception e){throw new CoreException2("SysML profile not found", e);}
    }

    public MDCustomizationForSysMLProfile getMDCustomSysml() {
         try{return MDCustomizationForSysMLProfile.getInstanceByProject(project);}
         catch (Exception e){throw new CoreException2("SysMLCustomization profile not found", e);}
    }


    public MDCustomizationForSysMLProfile getSysmlAdditionalStereotypes() {
         try{return MDCustomizationForSysMLProfile.getInstanceByProject(project);}
         catch (Exception e){throw new CoreException2("SysMLCustomization profile not found", e);}
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
