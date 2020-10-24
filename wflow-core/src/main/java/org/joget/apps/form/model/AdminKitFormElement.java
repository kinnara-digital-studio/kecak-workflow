package org.joget.apps.form.model;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface AdminKitFormElement extends BootstrapFormElement {
    String renderAdminKitTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
