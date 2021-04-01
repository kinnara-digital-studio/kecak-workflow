package org.joget.apps.form.model;

import org.joget.apps.form.service.FormUtil;

import java.util.Map;

/**
 * Abstract base class for buttons in a form.
 */
public abstract class FormButton extends Element implements FormAction {

    public static final String DEFAULT_ID = "submit";

    @Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "submitButton.ftl";

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }


    @Override
    public String renderAceTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "AceTheme/AceSubmitButton.ftl";

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "AdminLteTheme/AdminLteSubmitButton.ftl";

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    @Override
    public FormRowSet formatData(FormData formData) {
        return null;
    }

    public boolean isActive(Form form, FormData formData) {
        boolean active = false;
        String value = FormUtil.getRequestParameter(this, formData);
        if (value != null && value.trim().length() > 0) {
            active = true;
        }
        return active;
    }
}
