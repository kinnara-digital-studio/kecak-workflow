package org.joget.apps.form.model;

import org.kecak.apps.form.model.BootstrapFormElement;

import java.util.Map;

public interface AdminLteFormElement extends BootstrapFormElement {
    String renderAdminLteTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
