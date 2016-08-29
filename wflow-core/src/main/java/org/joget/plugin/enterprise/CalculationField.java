package org.joget.plugin.enterprise;

import java.util.Map;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.json.JSONObject;

public class CalculationField extends Element implements FormBuilderPaletteElement {

	public String getFormBuilderTemplate() {
		return "<label class='label'>Calculation Field</label><input type='text' readonly/>";
	}

	public String getLabel() {
		return "Calculation Field";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return AppUtil.readPluginResource((String)this.getClass().getName(), 
				(String)"/properties/form/calculationField.json", 
				(Object[])null, (boolean)true, (String)"message/form/CalculationField");
	}

	public String getName() {
		return "Calculation Field";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		 return "Used to do calculation on form field values.";
	}

	public String getFormBuilderCategory() {
		return "Enterprise";
	}

	public int getFormBuilderPosition() {
		return 500;
	}

	public String getFormBuilderIcon() {
		 return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
		String template = "calculationField.ftl";
        String value = FormUtil.getElementPropertyValue((Element)this, (FormData)formData);
        dataModel.put("value", value);
        dataModel.put("configJson", this.getConfigJson());
        @SuppressWarnings("rawtypes")
		String html = FormUtil.generateElementHtml((Element)this, (FormData)formData, (String)template, (Map)dataModel);
        return html;
	}

	private String getConfigJson() {
		try {
            JSONObject jsonConfig = new JSONObject();
            jsonConfig.accumulate("equation", (Object)this.getPropertyString("equation"));
            jsonConfig.accumulate("format", (Object)this.getPropertyString("style"));
            jsonConfig.accumulate("numOfDecimal", (Object)this.getPropertyString("numOfDecimal"));
            jsonConfig.accumulate("prefix", (Object)this.getPropertyString("prefix"));
            jsonConfig.accumulate("postfix", (Object)this.getPropertyString("postfix"));
            jsonConfig.accumulate("useThousandSeparator", (Object)this.getPropertyString("useThousandSeparator"));
            jsonConfig.accumulate("variables", (Object)((Object[])this.getProperty("variables")));
            return jsonConfig.toString();
        }
        catch (Exception e) {
            return "{}";
        }
	}
}
