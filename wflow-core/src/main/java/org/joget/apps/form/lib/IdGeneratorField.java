package org.joget.apps.form.lib;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.springframework.context.ApplicationContext;

public class IdGeneratorField extends Element implements FormBuilderPaletteElement, AceFormElement, AdminLteFormElement {

    @SuppressWarnings("unchecked")
	@Override
    public String renderTemplate(FormData formData, @SuppressWarnings("rawtypes") Map dataModel) {
        String template = "idGeneratorField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    protected String renderTemplate(String template, FormData formData, @SuppressWarnings("rawtypes") Map dataModel){

        String value = FormUtil.getElementPropertyValue(this, formData);
        dataModel.put("value", value);

        String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
        return html;
    }

    @Override
    public FormRowSet formatData(FormData formData) {
        FormRowSet rowSet = null;

        // get value
        String id = getPropertyString(FormUtil.PROPERTY_ID);
        if (id != null) {
            String value = FormUtil.getElementPropertyValue(this, formData);
            if (value == null || value.trim().isEmpty()) {
                // generate new value
                value = getGeneratedValue(formData);
                
                String paramName = FormUtil.getElementParameterName(this);
                formData.addRequestParameterValues(paramName, new String[] {value});
            }
            if (value != null) {
                // set value into Properties and FormRowSet object
                FormRow result = new FormRow();
                result.setProperty(id, value);
                rowSet = new FormRowSet();
                rowSet.add(result);
            }
        }

        return rowSet;
    }

    protected String getGeneratedValue(FormData formData) {
        String value = "";
        if (formData != null) {
            try {
                value = FormUtil.getElementPropertyValue(this, formData);
                if (!(value != null && value.trim().length() > 0)) {
                    String envVariable = getPropertyString("envVariable");
                    AppDefinition appDef = isUsingPublishedAppVersion() ? getPublishedAppDefinition() : AppUtil.getCurrentAppDefinition();
                    EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
                    
                    Integer count = environmentVariableDao.getIncreasedCounter(envVariable, "Used for plugin: " + getName(), appDef);

                    String format = getPropertyString("format");
                    value = format;
                    Matcher m = Pattern.compile("(\\?+)").matcher(format);
                    if (m.find()) {
                        String pattern = m.group(1);
                        String formater = pattern.replaceAll("\\?", "0");
                        pattern = pattern.replaceAll("\\?", "\\\\?");

                        DecimalFormat myFormatter = new DecimalFormat(formater);
                        String runningNumber = myFormatter.format(count);
                        value = value.replaceAll(pattern, runningNumber);
                    }
                }
            } catch (Exception e) {
                LogUtil.error(IdGeneratorField.class.getName(), e, "");
            }
        }
        return value;
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getName() {
        return "Id Generator Field";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "ID Generator Element";
    }

    public String getFormBuilderCategory() {
        return FormBuilderPalette.CATEGORY_CUSTOM;
    }

    public int getFormBuilderPosition() {
        return 3000;
    }

    public String getFormBuilderIcon() {
        return "/plugin/org.joget.apps.form.lib.IdGeneratorField/images/textField_icon.gif";
    }

    public String getFormBuilderTemplate() {
        return "<label class='label'>IdGeneratorField</label><span></span>";
    }

    public String getLabel() {
        return "ID Generator Field";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/form/idGeneratorField.json", null, true, "message/form/IdGeneratorField");
    }

    @Override
    public String renderAceTemplate(FormData formData, Map dataModel) {
        String template = "AceTheme/AceIdGeneratorField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    @Override
    public String renderAdminLteTemplate(FormData formData, Map dataModel) {
        String template = "AdminLteTheme/AdminLteIdGeneratorField.ftl";
        return renderTemplate(template,formData,dataModel);
    }

    /**
     *
     * @return
     */
    protected boolean isUsingPublishedAppVersion() {
        return "true".equalsIgnoreCase(getPropertyString("usePublishedAppVersion"));
    }

    /**
     * Get published application definition
     *
     * @return
     */
    protected AppDefinition getPublishedAppDefinition() {
        AppDefinition currentAppDefinition = AppUtil.getCurrentAppDefinition();
        AppDefinitionDao appDefinitionDao = (AppDefinitionDao) AppUtil.getApplicationContext().getBean("appDefinitionDao");
        String appId = currentAppDefinition.getAppId();
        long version = appDefinitionDao.getPublishedVersion(appId);
        Collection<AppDefinition> appDefinitions = appDefinitionDao.findByVersion(appId, appId, version, null, null, null, null, 1);
        if(appDefinitions == null || appDefinitions.isEmpty())
            return currentAppDefinition;

        return appDefinitions.stream()
                .findFirst()
                .orElse(currentAppDefinition);
    }
}
