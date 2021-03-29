package org.joget.apps.form.lib;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.SecurityUtil;

import java.util.Map;

public class TextField extends Element implements FormBuilderPaletteElement {

    public String getName() {
        return "Text Field";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Text Field Element";
    }

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "textField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    private String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel){
        // set value
        String value = FormUtil.getElementPropertyValue(this, formData);

        value = SecurityUtil.decrypt(value);

        dataModel.put("value", value);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }
    
    public FormRowSet formatData(FormData formData) {
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (value != null) {
                
                if ("true".equalsIgnoreCase(getPropertyString("encryption"))) {
                    value = SecurityUtil.encrypt(value);
                }
                
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>TextField</label><input type='text' />";
    }

    public String getLabel() {
        return "Text Field";
    }

    public String getPropertyOptions() {
        String encryption = "";
        if (SecurityUtil.getDataEncryption() != null) {
            encryption = ",{name : 'encryption', label : '@@form.textfield.encryption@@', type : 'checkbox', value : 'false', ";
            encryption += "options : [{value : 'true', label : '' }]}";
        }
        
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/textField.json", new Object[]{encryption}, true, "message/form/TextField");
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_GENERAL;
    }

    public int getFormBuilderPosition() {
        return 100;
    }

    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.TextField/images/textField_icon.gif";
    }

    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceTextField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel){
        String template = "AdminLteTheme/AdminLteTextField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

	@Override
	public String renderAdminKitTemplate(FormData formData, Map dataModel) {
		String template = "AdminKitTheme/AdminLteTextField.ftl";
        return renderTemplate(template,formData,dataModel);
	}
}
