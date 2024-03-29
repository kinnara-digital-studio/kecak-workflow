package org.joget.apps.userview.model;

import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.userview.service.UserviewUtil;
import org.joget.commons.util.StringUtil;

/**
 * A base abstract class to develop a Userview Theme plugin for version v5.0 onward.
 * 
 */
public abstract class UserviewV5Theme extends UserviewTheme {
    
    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getCss() {
        //is not using anymore
        return null;
    } 
    
    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getJavascript() {
        //is not using anymore
        return null;
    } 

    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getHeader() {
        //is not using anymore
        return null;
    } 

    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getFooter() {
        //is not using anymore
        return null;
    } 
    
    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getPageTop() {
        //is not using anymore
        return null;
    } 

    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getPageBottom() {
        //is not using anymore
        return null;
    } 

    /**
     * @Deprecated not use for UserviewV5Theme
     * 
     * @return 
     */
    @Deprecated
    public final String getBeforeContent() {
        //is not using anymore
        return null;
    } 

    /**
     * HTML template to handle error when retrieving userview content
     * 
     * @param e
     * @param data
     * @return 
     */
    public String handleContentError(Exception e, Map<String, Object> data) {
        return StringEscapeUtils.escapeHtml(e.getMessage());
    }

    /**
     * HTML template to handle page not found.
     * 
     * @param data
     * @return 
     */
    public String handlePageNotFound(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/pageNotFound.ftl");
    }

    /**
     * HTML template to handle theme layout
     * 
     * @param data
     * @return 
     */
    public String getLayout(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/layout.ftl");
    }

    /**
     * HTML template to handle page header
     * 
     * @param data
     * @return 
     */
    public String getHeader(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/header.ftl");
    }

    /**
     * HTML template to handle page footer
     * 
     * @param data
     * @return 
     */
    public String getFooter(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/footer.ftl");
    }

    /**
     * HTML template to handle userview menu content
     * 
     * @param data
     * @return 
     */
    public String getContentContainer(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/contentContainer.ftl");
    }

    /**
     * HTML template to handle menus
     * 
     * @param data
     * @return 
     */
    public String getMenus(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/menus.ftl");
    }

    /**
     * HTML template for putting javascript and css link for {@link #getHead(Map)} template
     * 
     * @param data
     * @return 
     */
    public String getJsCssLib(Map<String, Object> data) {
        return "<link href=\"" + data.get("context_path") + "/css/empty_userview.css?build=" + data.get("build_number") + "\" rel=\"stylesheet\" type=\"text/css\" />";
    }

    /**
     * Gets dynamic generated CSS for {@link #getHead(Map)} template
     * 
     * @param data
     * @return 
     */
    public String getCss(Map<String, Object> data) {
        return "";
    }

    /**
     * Gets dynamic generated javascript for {@link #getHead(Map)} template
     * 
     * @param data
     * @return 
     */
    public String getJs(Map<String, Object> data) {
        return "";
    }

    /**
     * Gets dynamic generated meta data for {@link #getHead(Map)} template
     * 
     * @param data
     * @return 
     */
    public String getMetas(Map<String, Object> data) {
        return "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n"
                + "<meta charset=\"utf-8\" />";
    }

    /**
     * HTML template to handle for &lt;head&gt; tag
     * 
     * @param data
     * @return 
     */
    public final String getHead(Map<String, Object> data) {
        return UserviewUtil.getTemplate(this, data, "/templates/userview/head.ftl");
    }

    /**
     * Gets the fav icon relative path for {@link #getHead(Map)} template
     * 
     * @param data
     * @return 
     */
    public String getFavIconLink(Map<String, Object> data) {
        return data.get("context_path") + "/images/favicon_uv.ico";
    }

    /**
     * HTML template for login form
     * 
     * @param data
     * @return 
     */
    public String getLoginForm(Map<String, Object> data) {
        if (!data.containsKey("login_form_before")) {
            data.put("login_form_before", this.userview.getSetting().getPropertyString("loginPageTop"));
        }
        if (!data.containsKey("login_form_after")) {
            data.put("login_form_after", this.userview.getSetting().getPropertyString("loginPageBottom"));
        }
        return UserviewUtil.getTemplate(this, data, "/templates/userview/login.ftl");
    }

    /**
     * HTML template for menu category label
     * 
     * @param data
     * @return 
     */
    public String decorateCategoryLabel(UserviewCategory category) {
        return StringUtil.stripHtmlRelaxed(category.getPropertyString("label"));
    }
}
