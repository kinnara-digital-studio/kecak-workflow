package org.joget.apps.form.model;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface AceFormElement extends FormElementBootstrap {
    String renderAceTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
