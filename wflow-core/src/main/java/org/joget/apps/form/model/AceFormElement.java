package org.joget.apps.form.model;

import org.kecak.apps.userview.model.BootstrapAceTheme;

import java.util.Map;

/**
 * Form AdminLTE Theme
 */
public interface AceFormElement extends FormElementBootstrap, BootstrapAceTheme {
    String renderAceTemplate( FormData formData, @SuppressWarnings("rawtypes") Map dataModel);
}
