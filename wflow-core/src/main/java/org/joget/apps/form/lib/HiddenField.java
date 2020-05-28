package org.joget.apps.form.lib;

import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormBuilderPalette;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;

public class HiddenField extends Element implements FormBuilderPaletteElement {

    public String getName() {
        return "Hidden Field";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Hidden Field Element";
    }

    @SuppressWarnings("unchecked")
	public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "hiddenField.ftl";

        // set value
        String value = getElementValue(formData);
        String priority = getPropertyString("useDefaultWhenEmpty");
        
        if (priority != null && !priority.isEmpty()) {
            if (("true".equals(priority) && (value == null || value.isEmpty()))
                    || "valueOnly".equals(priority)) {
                value = getPropertyString("value");
            }
        } else {
            if (getPropertyString("value") != null && !getPropertyString("value").isEmpty()) {
                value = getPropertyString("value");
            }
        } 

        dataModel.put("value", value);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>HiddenField</label>";
    }

    public String getLabel() {
        return "Hidden Field";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/hiddenField.json", null, true, "message/form/HiddenField");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    public int getFormBuilderPosition() {
        return 100;
    }

    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.HiddenField/images/textField_icon.gif";
    }
}
