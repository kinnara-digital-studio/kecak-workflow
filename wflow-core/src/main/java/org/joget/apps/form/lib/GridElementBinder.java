package org.joget.apps.form.lib;

import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

import java.util.*;
import java.util.stream.Stream;

public class GridElementBinder
        extends FormBinder
        implements FormLoadBinder,
        FormStoreBinder,
        FormLoadMultiRowElementBinder,
        FormStoreMultiRowElementBinder {

    private final static String LABEL = "Grid Element Binder";

    @Override
    public String getName() {
        return LABEL;
    }
    
    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
    
    @Override
    public String getDescription() {
        return "Artifact ID " + getClass().getPackage().getImplementationTitle();
    }
    
    @Override
    public String getClassName() {
        return this.getClass().getName();
    }
    
    @Override
    public String getLabel() {
        return LABEL;
    }
    
    @Override
    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        Object[] arguments = {appDef.getId(), appDef.getVersion()};
        return AppUtil.readPluginResource(getClass().getName(),
                "/properties/form/gridFormBinder.json", arguments, true);
    }
    
    @Override
    public FormRowSet load(Element element, String primaryKey, FormData formData) {
        final AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        FormRowSet rows;
        Form form = this.getSelectedForm();
        if (form != null && primaryKey != null) {
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            String propertyName = this.getFormPropertyName(form, this.getPropertyString("foreignKey"));
            final StringBuilder condition = new StringBuilder(propertyName != null && !propertyName.isEmpty() ? " WHERE " + propertyName + " = ?" : "");
            final ArrayList<Object> paramsArray = new ArrayList<>();
            paramsArray.add(primaryKey);

            Optional.ofNullable(getProperty("extraCondition"))
                    .map(o -> (Object[])o)
                    .map(Arrays::stream)
                    .orElse(Stream.empty())
                    .map(o -> (Map<String, String>)o)
                    .filter(m -> Objects.nonNull(m.get("key")) && Objects.nonNull(m.get("value")))
                    .filter(m -> !m.get("key").isEmpty() && !m.get("value").isEmpty())
                    .forEach(m -> {
                        condition.append(" AND ").append(this.getFormPropertyName(form, m.get("key"))).append(" = ? ");
                        paramsArray.add(AppUtil.processHashVariable(m.get("value").trim(), null, null, null, appDef));
                    });

            rows = formDataDao.find(form, condition.toString(), paramsArray.toArray(), "dateCreated", false, null, null);
        } else {
            rows = new FormRowSet();
        }

        rows.setMultiRow(true);
        return rows;
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
        if (rows == null) {
            return null;
        }
        
        FormDataDao formDataDao = (FormDataDao) FormUtil.getApplicationContext().getBean("formDataDao");
        AppService appService = (AppService) FormUtil.getApplicationContext().getBean("appService");
        Form form = this.getSelectedForm();
        if (form != null) {
            Form parentForm = FormUtil.findRootForm(element);
            String primaryKeyValue = parentForm.getPrimaryKeyValue(formData);
            FormRowSet originalRowSet = this.load(element, primaryKeyValue, formData);
            try {
                if (originalRowSet != null && !originalRowSet.isEmpty()) {
                    ArrayList<String> ids = new ArrayList<>();
                    for (FormRow r : originalRowSet) {
                        if (rows.contains(r)) {
                            continue;
                        }
                        ids.add(r.getId());
                    }
                    if (ids.size() > 0) {
                        formDataDao.delete(form, ids.toArray(new String[ids.size()]));
                    }
                }
                
                for (FormRow row : rows) {
                    row.put(this.getPropertyString("foreignKey"), primaryKeyValue);
                }
                
                rows = appService.storeFormData(form, rows, null);
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, e.getMessage());
            }
        }
        return rows;
    }
    
    protected Form getSelectedForm() {
        Form form = null;
        FormDefinitionDao formDefinitionDao = (FormDefinitionDao) AppUtil.getApplicationContext().getBean("formDefinitionDao");
        FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");
        String formDefId = this.getPropertyString("formDefId");
        if (formDefId != null) {
            String formJson;
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            FormDefinition formDef = formDefinitionDao.loadById(formDefId, appDef);
            if (formDef != null && (formJson = formDef.getJson()) != null) {
                form = (Form) formService.createElementFromJson(formJson);
            }
        }
        return form;
    }
    
    protected String getFormPropertyName(Form form, String propertyName) {
        if (propertyName != null && !propertyName.isEmpty() && (((FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao")).getFormDefinitionColumnNames(form.getPropertyString("tableName"))).contains(propertyName) && !"id".equals(propertyName)) {
            propertyName = "customProperties." + propertyName;
        }
        return propertyName;
    }
}
