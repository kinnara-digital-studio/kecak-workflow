package org.joget.apps.form.model;

import org.kecak.apps.form.model.BootstrapFormElement;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface AdminKitFormElement extends BootstrapFormElement {
    String renderAdminKitTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
