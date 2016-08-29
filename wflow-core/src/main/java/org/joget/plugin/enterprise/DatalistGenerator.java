package org.joget.plugin.enterprise;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormOptionsElement;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.generator.model.GeneratorPlugin;
import org.joget.apps.generator.model.GeneratorResult;
import org.joget.apps.generator.service.GeneratorUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class DatalistGenerator extends GeneratorPlugin {

	public String getLabel() {
		return ResourceBundleUtil.getMessage((String)"generator.datalist.label");
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		String options = AppUtil.readPluginResource((String)this.getClass().getName(), (String)"/properties/generator/datalistGenerator.json", (Object[])null, (boolean)true, (String)null);
        options = GeneratorUtil.populateFormMeta((String)options, (String)this.getFormId(), (AppDefinition)this.getAppDefinition());
        String selecetdColumns = "";
        Collection<Map<String,String>> columns = FormUtil.getFormColumns((AppDefinition)this.getAppDefinition(), (String)this.getFormId());
        int count = 0;
        for (Map<String, String> c : columns) {
            if (count == 5) break;
            if (count > 0) {
                selecetdColumns = selecetdColumns + ";";
            }
            selecetdColumns = selecetdColumns + (String)c.get("value");
            ++count;
        }
        options = options.replace("[default_selected_columns]", selecetdColumns);
        return options;
	}

	public String getName() {
		return "Data List Generator";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return "Used to generate a Data List";
	}

	@Override
	public String getExplanation() {
		return ResourceBundleUtil.getMessage((String)"generator.datalist.explaination");
	}

	@Override
	public GeneratorResult generate() {
		GeneratorResult result = new GeneratorResult();
        AppDefinition appDef = this.getAppDefinition();
        try {
            DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao)AppUtil.getApplicationContext().getBean("datalistDefinitionDao");
            Form form = GeneratorUtil.getFormObject((String)this.getFormId(), (AppDefinition)appDef);
            String listId = this.getPropertyString("listId");
            String listName = this.getPropertyString("listName");
            int count = 0;
            while (this.isExist(listId, datalistDefinitionDao)) {
                listId = listId + "_" + ++count;
            }
            String json = AppUtil.readPluginResource((String)this.getClass().getName(), (String)"/resources/generator/datalist/list.json", (Object[])new Object[]{listId, listName, this.getFormId(), this.getPropertyString("extraBinderProperties"), this.getFilters(form), this.getColumns(form)}, (boolean)true, (String)null);
            DatalistDefinition list = new DatalistDefinition();
            list.setAppDefinition(appDef);
            list.setId(listId);
            list.setName(listName);
            list.setJson(json);
            datalistDefinitionDao.add(list);
            String editLink = WorkflowUtil.getHttpServletRequest().getContextPath() + "/web/console/app/" + appDef.getAppId() + "/" + appDef.getVersion() + "/datalist/builder/" + listId;
            result.setMessage(ResourceBundleUtil.getMessage((String)"generator.datalist.msg.success", (Object[])new String[]{editLink}));
            result.setItemId(listId);
        }
        catch (Exception e) {
            result.setError(true);
            result.setMessage(ResourceBundleUtil.getMessage((String)"generator.datalist.msg.error"));
            LogUtil.error((String)this.getClassName(), (Throwable)e, (String)"Not able to generate data list");
        }
        return result;
	}

	protected boolean isExist(String id, DatalistDefinitionDao datalistDefinitionDao) {
		Long count = datalistDefinitionDao.count("AND id = ?", (Object[])new String[]{id}, this.getAppDefinition());
        return count > 0;
	}
	
	protected String getFilters(Form form) {
		String[] filters;
        String filters_text = "";
        if (this.getPropertyString("filters") != null && !this.getPropertyString("filters").isEmpty() && (filters = this.getPropertyString("filters").split(";")).length > 0) {
            for (int i = 0; i < filters.length; ++i) {
                Element el;
                String type = "";
                String label = "";
                if (i > 0) {
                    filters_text = filters_text + ",";
                }
                if ((el = FormUtil.findElement((String)filters[i], (Element)form, (FormData)null, (Boolean)true)) != null) {
                    label = el.getPropertyString("label");
                    if (label.isEmpty()) {
                        label = filters[i];
                    }
                    if ("true".equalsIgnoreCase(this.getPropertyString("autoConfigure")) && el instanceof FormOptionsElement) {
                        try {
                            HashMap<String, Object> properties = new HashMap<String, Object>();
                            properties.put("multiple", "");
                            properties.put("size", "");
                            properties.put("defaultValue", "");
                            properties.put("options", el.getProperty("options"));
                            properties.put("optionsBinder", el.getProperty("optionsBinder"));
                            JSONObject jsonProps = FormUtil.generatePropertyJsonObject(properties);
                            type = ",\"type\":{\"className\":\"org.joget.plugin.enterprise.SelectBoxDataListFilterType\",\"properties\":";
                            type = type + jsonProps.toString();
                            type = type + "}";
                        }
                        catch (JSONException e) {}
                    }
                } else if ("id".equals(filters[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.id");
                } else if ("dateCreated".equals(filters[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.dateCreated");
                } else if ("dateModified".equals(filters[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.dateModified");
                }
                filters_text = filters_text + "{\"id\":\"filter_" + i + "\",\"label\":\"" + StringEscapeUtils.escapeJavaScript((String)label) + "\",\"name\":\"" + filters[i] + "\"" + type + "}";
            }
        }
        return filters_text;
	}
	
	protected String getColumns(Form form) {
		String[] columns;
        String columns_text = "";
        if (this.getPropertyString("columns") != null && !this.getPropertyString("columns").isEmpty() && (columns = this.getPropertyString("columns").split(";")).length > 0) {
            for (int i = 0; i < columns.length; ++i) {
                Element el;
                String format = "";
                String label = "";
                if (i > 0) {
                    columns_text = columns_text + ",";
                }
                if ((el = FormUtil.findElement((String)columns[i], (Element)form, (FormData)null, (Boolean)true)) != null) {
                    label = el.getPropertyString("label");
                    if (label.isEmpty()) {
                        label = columns[i];
                    }
                    if ("true".equalsIgnoreCase(this.getPropertyString("autoConfigure")) && el instanceof FormOptionsElement) {
                        try {
                            HashMap<String, Object> properties = new HashMap<String, Object>();
                            properties.put("options", el.getProperty("options"));
                            properties.put("optionsBinder", el.getProperty("optionsBinder"));
                            JSONObject jsonProps = FormUtil.generatePropertyJsonObject(properties);
                            format = ",\"format\":{\"className\":\"org.joget.plugin.enterprise.OptionsValueFormatter\",\"properties\":";
                            format = format + jsonProps.toString();
                            format = format + "}";
                        }
                        catch (JSONException e) {}
                    }
                } else if ("id".equals(columns[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.id");
                } else if ("dateCreated".equals(columns[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.dateCreated");
                } else if ("dateModified".equals(columns[i])) {
                    label = ResourceBundleUtil.getMessage((String)"datalist.formrowdatalistbinder.dateModified");
                }
                columns_text = columns_text + "{\"id\":\"column_" + i + "\",\"label\":\"" + StringEscapeUtils.escapeJavaScript((String)label) + "\",\"name\":\"" + columns[i] + "\"" + format + "}";
            }
        }
        return columns_text;
	}
}
