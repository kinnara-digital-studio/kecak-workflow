package org.kecak.apps.userview.model;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;

import java.util.Map;

/**
 * @author aristo
 *
 * Bootstrap Web UI Framework for Userview Theme
 */
@Deprecated
public interface BootstrapUserview {

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
