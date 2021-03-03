package org.joget.apps.form.lib;

import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.kecak.apps.form.model.AceFormElement;
import org.kecak.apps.form.model.AdminLteFormElement;

public class TextArea extends Element implements FormBuilderPaletteElement, AceFormElement, AdminLteFormElement {

    public String getName() {
        return "Text Area";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Text Area Element";
    }

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "textArea.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel){
        // set value
        String value = getElementValue(formData);
        dataModel.put("value", value);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>TextArea</label><textarea cols='20' rows='5'></textarea>";
    }

    public String getLabel() {
        return "Text Area";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/textArea.json", null, true, "message/form/TextArea");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    public int getFormBuilderPosition() {
        return 200;
    }

    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.TextArea/images/textArea_icon.gif";
    }


    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceTextArea.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel){
        String template = "AdminLteTheme/AdminLteTextArea.ftl";
        return renderTemplate(template,formData,dataModel);
    }
}
