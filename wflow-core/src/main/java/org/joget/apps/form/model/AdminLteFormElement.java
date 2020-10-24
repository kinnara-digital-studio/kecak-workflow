package org.joget.apps.form.model;

import java.util.Map;

public interface AdminLteFormElement extends BootstrapFormElement {
    String renderAdminLteTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
