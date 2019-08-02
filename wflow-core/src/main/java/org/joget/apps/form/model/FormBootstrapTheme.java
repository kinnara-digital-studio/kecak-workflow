package org.joget.apps.form.model;

import org.joget.apps.BootstrapTheme;

import java.util.Map;

/**
 * Bootstrap Web UI Framework for Form
 */
public interface FormBootstrapTheme extends BootstrapTheme {
    String renderBootstrapTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
