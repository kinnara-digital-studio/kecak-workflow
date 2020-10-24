package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewTheme;

/**
 * @author yonathan
 *
 * Abstract class to handle AdminKit Theme
 */
public abstract class AbstractAdminKitUserviewTheme extends UserviewTheme implements BootstrapUserview, BootstrapAdminKitTheme  {
    @Override
    public String getUserviewJsp() {
        return "ubuilder/AdminKitView";
    }

    @Override
    public String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public String getDataListJsp() {
        return "userview/plugin/AdminKitDataList.jsp";
    }

    @Override
    public String getFormJsp() {
        return "userview/plugin/AdminKitForm.jsp";
    }

    @Override
    public String getRunProcessJsp() {
        return "userview/plugin/AdminKitRunProcess.jsp";
    }

    @Override
    public String getLoginJsp() {
        return "ubuilder/AdminKitLogin";
    }
}
