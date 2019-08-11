package org.joget.apps.userview.lib;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.userview.model.AbstractAceUserviewTheme;

public class AceTheme extends AbstractAceUserviewTheme {
    private final String path = "/plugin/" + getClassName() + "/themes/ace" ;

    @Override
    public String getCss() {
        String css = getPropertyString("css");
        css = css.replaceAll("@@contextPath@@", getRequestParameterString("contextPath"));
        css = "<style type=\"text/css\">" + css + "</style>\n";
        css += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + getRequestParameterString("contextPath") + path + "/css/ace.min.css\" class=\"ace-main-stylesheet\" id=\"main-ace-style\"/>\n";
        css += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + getRequestParameterString("contextPath") + path + "/css/ace-skins.min.css\"/>\n";
        css += "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"" + getRequestParameterString("contextPath") + path + "/css/ace-rtl.min.css\"/>\n";
        return css;
    }

    @Override
    public String getJavascript() {
        String js = "<script type=\"text/javascript\">" + getPropertyString("js")+
                "\n\t\t\tif('ontouchstart' in document.documentElement) document.write(\"<script src='"+ getRequestParameterString("contextPath") + path +"/js/jquery.mobile.custom.min.js'>\"+\"<\"+\"/script>\");\n" +
                "\t\t</script>"+
                "<!--[if lte IE 8]>\n" +
                "\t\t  <script src=\""+ getRequestParameterString("contextPath") + path +"/js/excanvas.min.js\"></script>\n" +
                "\t\t<![endif]-->";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "/js/ace-extra.min.js\"></script>\n";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "/js/jquery-ui.custom.min.js\"></script>\n";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "/js/jquery.ui.touch-punch.min.js\"></script>\n";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "/js/ace-elements.min.js\"></script>\n";
        js += "<script src=\"" + getRequestParameterString("contextPath") + path + "/js/ace.min.js\"></script>\n";

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
        return "Ace Plugin Theme";
    }

    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    public String getDescription() {
        return "";
    }

    public String getLabel() {
        return getName();
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/userview/aceTheme.json", null, true, null);
    }
}