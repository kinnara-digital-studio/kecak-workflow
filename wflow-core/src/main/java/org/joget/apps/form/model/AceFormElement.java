package org.joget.apps.form.model;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface FormAceTheme extends FormElementBootstrap<AceTheme> {
    default String  renderBootstrapTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        return renderAceTemplate(formData, dataModel);
    }

    String renderAceTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
