package org.joget.apps.datalist.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.lib.PasswordField;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormContainer;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.Userview;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;

public class FormRowDataListBinder extends DataListBinderDefault {

    private Form cachedForm = null;
    private String cachedTableName = null;
    private String cachedFormDefId = null;
    
    public String getClassName() {
        return this.getClass().getName();
    }

    public String getName() {
        return "Form Data Binder";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "Retrieves data rows from a form table.";
    }

    public String getLabel() {
        return "Form Data Binder";
    }

    public String getPropertyOptions() {
        String formDefField = null;
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        if (appDef != null) {
            String formJsonUrl = "[CONTEXT_PATH]/web/json/console/app/" + appDef.getId() + "/" + appDef.getVersion() + "/forms/options";
            formDefField = "{name:'formDefId',label:'@@datalist.formrowdatalistbinder.formId@@',type:'selectbox',options_ajax:'" + formJsonUrl + "'}";
        } else {
            formDefField = "{name:'formDefId',label:'@@datalist.formrowdatalistbinder.formId@@',type:'textfield'}";
        }
        Object[] arguments = new Object[]{formDefField};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/datalist/formRowDataListBinder.json", arguments, true, "message/datalist/formRowDataListBinder");
        return json;
    }

    public DataListColumn[] getColumns() {
        List<DataListColumn> columns = new ArrayList<DataListColumn>();

        // retrieve columns
        Form form = getSelectedForm();
        if (form != null) {
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            String tableName = formDataDao.getFormTableName(form);
            Collection<String> columnNames = formDataDao.getFormDefinitionColumnNames(tableName);
            for (String columnName : columnNames) {
                Element element = FormUtil.findElement(columnName, form, null, true);
                if (element != null && !(element instanceof FormContainer)) {
                    if (!(element instanceof PasswordField)) {
                        String id = element.getPropertyString(FormUtil.PROPERTY_ID);
                        String label = element.getPropertyString(FormUtil.PROPERTY_LABEL);
                        if (id != null && !id.isEmpty()) {
                            if (label == null || label.isEmpty()) {
                                label = id;
                            }
                            columns.add(new DataListColumn(id, label, true));
                        }
                    }
                } else {
                    columns.add(new DataListColumn(columnName, columnName, true));
                }
            }
        }

        // add default metadata fields
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_DATE_MODIFIED, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.dateModified"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_DATE_CREATED, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.dateCreated"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_CREATED_BY, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.createdBy"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_MODIFIED_BY, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.modifiedBy"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_ORG_ID, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.orgId"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_DELETED, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.deleted"), true));
        columns.add(0, new DataListColumn(FormUtil.PROPERTY_ID, ResourceBundleUtil.getMessage("datalist.formrowdatalistbinder.id"), true));

        return columns.toArray(new DataListColumn[0]);
    }

    public String getPrimaryKeyColumnName() {
        return "id";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public DataListCollection getData(DataList dataList, Map properties, DataListFilterQueryObject[] filterQueryObjects, String sort, Boolean desc, Integer start, Integer rows) {
        DataListCollection resultList = new DataListCollection();

        Form form = getSelectedForm();
        if (form != null) {
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

            DataListFilterQueryObject criteria = getCriteria(properties, filterQueryObjects);

            final String sortAs = getSortAs(dataList, sort);
            FormRowSet rowSet = formDataDao.find(form, criteria.getQuery(), criteria.getValues(), sort, sortAs, desc, start, rows);
            resultList.addAll(rowSet);
        }

        return resultList;
    }

    public int getDataTotalRowCount(DataList dataList, @SuppressWarnings("rawtypes") Map properties, DataListFilterQueryObject[] filterQueryObjects) {
        int count = 0;

        Form form = getSelectedForm();
        if (form != null) {
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            DataListFilterQueryObject criteria = getCriteria(properties, filterQueryObjects);

            Long rowCount = formDataDao.count(form, criteria.getQuery(), criteria.getValues());
            count = rowCount.intValue();
        }
        return count;
    }

    protected Form getSelectedForm() {
        Form form = null;
        FormDefinitionDao formDefinitionDao = (FormDefinitionDao) AppUtil.getApplicationContext().getBean("formDefinitionDao");
        FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");
        String formDefId = getPropertyString("formDefId");
        if (formDefId != null) {
            if (cachedForm == null || !formDefId.equals(cachedFormDefId)) {
                AppDefinition appDef = AppUtil.getCurrentAppDefinition();
                FormDefinition formDef = formDefinitionDao.loadById(formDefId, appDef);
                if (formDef != null) {
                    String formJson = formDef.getJson();
                    
                    if (formJson != null) {
                        form = (Form) formService.createElementFromJson(formJson, false);
                        cachedFormDefId = formDefId;
                        cachedForm = form;
                    }
                }
            } else {
                form = cachedForm;
            }
        }
        return form;
    }

    protected String getTableName(String formDefId) {
        String tableName = cachedTableName;
        if (tableName == null) {
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            if (appDef != null && formDefId != null) {
                AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
                tableName = appService.getFormTableName(appDef, formDefId);
                cachedTableName = tableName;
            }
        }
        return tableName;
    }

    @Override
    public String getColumnName(String name) {
        if (name != null && !name.isEmpty() && !FormUtil.PROPERTY_ID.equals(name)) {
            Form form = getSelectedForm();
            String formDefId = form.getPropertyString("id");
            String tableName = getTableName(formDefId);
            if (tableName != null) {
                FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                Collection<String> columnNames = formDataDao.getFormDefinitionColumnNames(tableName);
                if (columnNames.contains(name)) {
                    name = FormUtil.PROPERTY_CUSTOM_PROPERTIES + "." + name;
                } else if (FormUtil.PROPERTY_DATE_CREATED.equals(name) || FormUtil.PROPERTY_DATE_MODIFIED.equals(name)) {
                    name = "cast(" + name + " as string)";
                }
            }
        }
        return name;
    }

    protected DataListFilterQueryObject getCriteria(@SuppressWarnings("rawtypes") Map properties, DataListFilterQueryObject[] filterQueryObjects) {
        Collection<String> params = new ArrayList<String>();
        String condition = "";

        DataListFilterQueryObject filter = processFilterQueryObjects(filterQueryObjects);

        if (filter.getQuery() != null && !filter.getQuery().isEmpty()) {
            condition = " WHERE " + filter.getQuery();
            if (filter.getValues() != null && filter.getValues().length > 0) {
                params.addAll(Arrays.asList(filter.getValues()));
            }
        }

        String extraCondition = (properties.get("extraCondition") != null) ? properties.get("extraCondition").toString() : null;
        String keyName = null;
        if (properties.get(Userview.USERVIEW_KEY_NAME) != null) {
            keyName = properties.get(Userview.USERVIEW_KEY_NAME).toString();
        }
        String keyValue = null;
        if (properties.get(Userview.USERVIEW_KEY_VALUE) != null) {
            keyValue = properties.get(Userview.USERVIEW_KEY_VALUE).toString();
        }

        if (extraCondition != null && extraCondition.contains(USERVIEW_KEY_SYNTAX)) {
            if (keyValue == null) {
                keyValue = "";
            }
            extraCondition = extraCondition.replaceAll(USERVIEW_KEY_SYNTAX, keyValue);
        } else if (keyName != null && !keyName.isEmpty() && keyValue != null && !keyValue.isEmpty()) {
            if (condition.trim().length() > 0) {
                condition += " AND ";
            } else {
                condition += " WHERE ";
            }
            condition += getColumnName(keyName) + " = ?";
            params.add(keyValue);
        }

        if (extraCondition != null && !extraCondition.isEmpty()) {
            if (condition.trim().length() > 0) {
                condition += " AND ";
            } else {
                condition += " WHERE ";
            }
            condition += "( " + extraCondition + " )";
        }

        DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        queryObject.setQuery(condition);
        if (params.size() > 0) {
            queryObject.setValues((String[]) params.toArray(new String[0]));
        }
        return queryObject;
    }

    /**
     * Get sort as
     *
     * @param   dataList
     * @param   columnName
     * @return  sortAs : SQL data type
     */
    protected String getSortAs(DataList dataList, String columnName) {
        final DataListColumn[] columns = dataList.getColumns();
        for (DataListColumn column : columns) {
            if(column != null && column.getName().equalsIgnoreCase(columnName) && column.getFormats() != null) {
                for (DataListColumnFormat format : column.getFormats()) {
                    if(format != null) {
                        return format.getSortAs(dataList, column);
                    }
                }
            }
        }

        return FormUtil.PROPERTY_DATE_CREATED.equals(columnName)
                || FormUtil.PROPERTY_DATE_MODIFIED.equals(columnName) ? "timestamp" : "string";
    }
}