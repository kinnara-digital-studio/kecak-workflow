package org.joget.apps.form.model;

import java.util.Map;

public interface AdminLteFormElement extends FormElementBootstrap {
    String renderAdminLteTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
