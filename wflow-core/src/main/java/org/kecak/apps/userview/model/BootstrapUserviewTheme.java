package org.kecak.apps.userview.model;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;

import java.util.Map;

/**
 * @author aristo
 *
 * Bootstrap Web UI Framework for Userview Theme
 */
public interface BootstrapUserview {
    /**
     * WARNING!!!!! these methods have to be implemented in Kecak Core
     * JSP File in Plugins cannot be recognized by Kecak Core
     * @return
     */

    String getUserviewJsp();

    String getPreviewJsp();

    String getDataListJsp();

    String getFormJsp();

    String getRunProcessJsp();

    String getLoginJsp();

    default String getUnauthorizedJsp() {
        return "userview/plugin/unauthorized.jsp";
    }

    /**
     * Render core element
     *
     * @param element
     * @param formData
     * @param dataModel
     * @return
     */
    String renderCoreElementTemplate(Element element, FormData formData, Map dataModel);
}
