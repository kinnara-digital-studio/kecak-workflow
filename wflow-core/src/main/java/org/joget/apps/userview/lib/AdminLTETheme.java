package org.joget.apps.userview.lib;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.userview.model.UserviewBootstrapTheme;
import org.joget.apps.userview.model.UserviewTheme;

public class AdminLTETheme extends UserviewTheme implements UserviewBootstrapTheme {
    private static final String path =  "/plugin/" + AdminLTETheme.class.getName() + "/themes/adminLTE/";

    @Override
    public String getCss() {
        String css = getPropertyString("css");
        css = css.replaceAll("@@contextPath@@", getRequestParameterString("contextPath"));
        css = "<style type=\"text/css\">" + css + "</style>\n";
        css += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + getRequestParameterString("contextPath") + path + "css/AdminLTE.css\"/>\n";
        css += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + getRequestParameterString("contextPath") + path + "css/skins/skin-blue.css\"/>\n";
        return css;
    }

    @Override
    public String getJavascript() {
        String js = "<script type=\"text/javascript\">" + getPropertyString("js") + "</script>";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "js/adminlte.js\"></script>\n";
        return js;
    }

    @Override
    public String getHeader() {
        if (getPropertyString("customHeader") != null && getPropertyString("customHeader").trim().length() > 0) {
            return getPropertyString("customHeader");
        }
        return null;
    }

    @Override
    public String getFooter() {
        if (getPropertyString("customFooter") != null && getPropertyString("customFooter").trim().length() > 0) {
            return getPropertyString("customFooter");
        }
        return null;
    }

    @Override
    public String getPageTop() {
        if (getPropertyString("pageTop") != null && getPropertyString("pageTop").trim().length() > 0) {
            return getPropertyString("pageTop");
        }
        return null;
    }

    @Override
    public String getPageBottom() {
        if (getPropertyString("pageBottom") != null && getPropertyString("pageBottom").trim().length() > 0) {
            return getPropertyString("pageBottom");
        }
        return null;
    }

    @Override
    public String getBeforeContent() {
        if (getPropertyString("beforeContent") != null && getPropertyString("beforeContent").trim().length() > 0) {
            return getPropertyString("beforeContent");
        }
        return null;
    }

    public String getName() {
        return "Admin LTE Theme";
    }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    public String getDescription() {
        return "";
    }

    public String getLabel() {
        return "Admin LTE Theme";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/userview/adminLTETheme.json", null, true, "message/userview/defaultTheme");
    }
}
