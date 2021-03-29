package org.joget.apps.form.lib;

import com.kinnarastudio.commons.Declutter;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HiddenField extends Element implements FormBuilderPaletteElement, Declutter {

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
        String value = FormUtil.getElementPropertyValue(this, formData);
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

    @Override
    public Object handleElementValueResponse(@Nonnull Element element, @Nonnull FormData formData) throws JSONException {
        String value = FormUtil.getElementPropertyValue(element, formData);

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

        return value;
    }

    @Override
    public String[] handleMultipartDataRequest(String[] values, Element element, FormData formData) {
        return handleDataRequestParameter(element, formData);
    }

    @Override
    public String[] handleJsonDataRequest(@Nonnull Object value, @Nonnull Element element, @Nonnull FormData formData) {
        return handleDataRequestParameter(element, formData);
    }

    protected String[] handleDataRequestParameter(Element element, FormData formData) {
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        WorkflowAssignment assignment = workflowManager.getAssignment(formData.getActivityId());

        String elementId = element.getPropertyString(FormUtil.PROPERTY_ID);
        String defaultValue = AppUtil.processHashVariable(element.getPropertyString(FormUtil.PROPERTY_VALUE), assignment, null, null);
        FormRowSet rows = formData.getLoadBinderData(element);
        if(rows == null || rows.isEmpty()) {
            return new String[] { defaultValue };
        }

        String databaseValue = rows.get(0).getProperty(elementId, "");

        String priority = element.getPropertyString("useDefaultWhenEmpty");

        if (isNotEmpty(priority) && (("true".equals(priority) && isEmpty(databaseValue)) || "valueOnly".equals(priority))) {
            return new String[] { defaultValue };
        }

        return new String[] { ifEmptyThen(databaseValue, defaultValue) };
    }
}
