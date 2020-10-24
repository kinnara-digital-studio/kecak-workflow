package org.joget.apps.form.model;

import org.kecak.apps.userview.model.BootstrapAdminKitTheme;

import java.util.Map;

/**
 * Form Admin Kit Theme
 */
public interface AdminKitFormElement extends FormElementBootstrap, BootstrapAdminKitTheme {
    String renderAdminKitTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
