package org.joget.apps.form.model;

import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.service.FormUtil;

public class Column extends Element implements FormBuilderEditable, FormContainer {

    public String getName() {
        return "Column";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Column Element";
    }

    @Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "column.ftl";
        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getLabel() {
        return getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/column.json", null, true, "message/form/Column");
    }

    public String getFormBuilderTemplate() {
        return "";
    }
}
