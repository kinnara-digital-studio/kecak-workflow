package org.joget.apps.form.model;

import org.joget.apps.userview.model.BootstrapAdminLteTheme;

import java.util.Map;

public interface AdminLteFormElement extends FormElementBootstrap {
    String renderAdminLteTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
