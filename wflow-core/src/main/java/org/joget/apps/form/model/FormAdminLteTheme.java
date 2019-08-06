package org.joget.apps.form.model;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface FormAdminLteTheme extends FormBootstrapTheme {
    default String  renderBootstrapTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        return renderAdminLteTemplate(formData, dataModel);
    }

    String renderAdminLteTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
