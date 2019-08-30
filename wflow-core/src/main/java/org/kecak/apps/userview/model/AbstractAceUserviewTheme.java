package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewTheme;

/**
 * @author aristo
 *
 * Abstract class to handle Ace Theme
 */
public abstract class AbstractAceUserviewTheme extends UserviewTheme implements BootstrapUserview, BootstrapAceTheme {
    @Override
    public String getUserviewJsp() {
        return "ubuilder/AceView";
    }

    @Override
    public String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public String getDataListJsp() {
        return "userview/plugin/AceDataList.jsp";
    }

    @Override
    public String getFormJsp() {
        return "userview/plugin/AceForm.jsp";
    }

    @Override
    public String getRunProcessJsp() {
        return "userview/plugin/AceRunProcess.jsp";
    }

    @Override
    public String getLoginJsp() {
        return "ubuilder/AceLogin";
    }
}
