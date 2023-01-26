/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.profile;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.sysml.util.MDCustomizationForSysMLProfile;
import com.nomagic.uml2.MagicDrawProfile;
import com.nomagic.uml2.StandardProfile;
import com.samares.omf.core.utils.OMFConstants;
import com.samares.omf.core.utils.OMFUtils;

public class Profile {

    private MagicDrawProfile mdProfile  = null;
    private StandardProfile standard  = null;
    private SysMLProfile sysml  = null;
    private MDCustomizationForSysMLProfile sysml_additional_stereotypes = null;
    private Project project;
    private static Profile instance = null;

    public Profile(Project project){
        this.project = project;
    }

    public static Profile getInstance(){
        return getInstance(OMFUtils.currentProject);
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

        this.standard = StandardProfile.getInstanceByProject(project);
        this.sysml = SysMLProfile.getInstanceByProject(project);
        this.sysml_additional_stereotypes = MDCustomizationForSysMLProfile.getInstanceByProject(project);
        this.mdProfile = MagicDrawProfile.getInstanceByProject(project);

        OMFConstants.reinitConstants();
    }

    public MagicDrawProfile get_MdProfile() {
        return mdProfile;
    }

    public StandardProfile get_Standard() {
        return standard;
    }

    public SysMLProfile get_Sysml() {
        return sysml;
    }

    public MDCustomizationForSysMLProfile get_SysML_Additional_stereotypes() {
        return sysml_additional_stereotypes;
    }

    public static MagicDrawProfile getMdProfile() {
        return getInstance().get_MdProfile();
    }

    public static StandardProfile getStandard() {
        return getInstance().get_Standard();
    }

    public static SysMLProfile getSysml() {
        return getInstance().get_Sysml();
    }
    public Project getProject() {
        return project;
    }

    public static MDCustomizationForSysMLProfile getSysmlAdditionalStereotypes() {
        return getInstance().get_SysML_Additional_stereotypes();
    }

}
