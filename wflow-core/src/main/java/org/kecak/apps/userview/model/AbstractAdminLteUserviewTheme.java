package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewTheme;

/**
 * @author aristo
 *
 * Abstract class to handle AdminLTE Theme
 */
public abstract class AbstractAdminLteUserviewTheme extends UserviewTheme implements BootstrapUserview, BootstrapAdminLteTheme  {
    @Override
    public String getUserviewJsp() {
        return "ubuilder/AdminLteView";
    }

    @Override
    public String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public String getDataListJsp() {
        return "userview/plugin/AdminLteDataList.jsp";
    }

    @Override
    public String getFormJsp() {
        return "userview/plugin/AdminLteForm.jsp";
    }

    @Override
    public String getRunProcessJsp() {
        return "userview/plugin/AdminLteRunProcess.jsp";
    }

    @Override
    public String getLoginJsp() {
        return "ubuilder/AdminLteLogin";
    }
}
