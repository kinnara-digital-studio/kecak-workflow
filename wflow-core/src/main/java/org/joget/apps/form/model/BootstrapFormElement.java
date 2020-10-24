package org.joget.apps.form.model;

import java.util.Map;

/**
 * Bootstrap Web UI Framework for Form
 */
public interface FormElementBootstrap {
    String renderBootstrapTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
