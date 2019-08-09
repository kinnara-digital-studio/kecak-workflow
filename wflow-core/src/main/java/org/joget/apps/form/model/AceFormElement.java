package org.joget.apps.form.model;

import org.joget.apps.userview.model.BootstrapAceTheme;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface AceFormElement extends FormElementBootstrap, BootstrapAceTheme {
    String renderAceTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
