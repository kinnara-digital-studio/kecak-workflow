package org.joget.plugin.enterprise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.datalist.model.DataListFilterTypeDefault;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;

public class DateRangeDataListFilterType extends DataListFilterTypeDefault {

	protected final String DATE_FORMAT = "yyyy-MM-dd";
    protected final String DATE_CREATED = "dateCreated";
    protected final String DATE_MODIFIED = "dateModified";
    
	public String getTemplate(DataList datalist, String name, String label) {
		PluginManager pluginManager = (PluginManager)AppUtil.getApplicationContext().getBean("pluginManager");
        HashMap<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("element", (Object)this);
        dataModel.put("name", datalist.getDataListEncodedParamName("fn_" + name));
        dataModel.put("label", label);
        String[] values = this.getValues(datalist, name);
        if (values != null && values[0] != null) {
            dataModel.put("value1", values[0]);
        } else {
            dataModel.put("value1", "");
        }
        if (values != null && values[1] != null) {
            dataModel.put("value2", values[1]);
        } else {
            dataModel.put("value2", "");
        }
        dataModel.put("contextPath", WorkflowUtil.getHttpServletRequest().getContextPath());
        return pluginManager.getPluginFreeMarkerTemplate(dataModel, this.getClassName(), "/templates/dateRangeDataListFilterType.ftl", null);
	}

	public DataListFilterQueryObject getQueryObject(DataList datalist,
			String name) {
		DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        String columnName = datalist.getBinder().getColumnName(name);
        String query = "";
        ArrayList<String> queryValues = new ArrayList<String>();
        String[] values = this.getValues(datalist, name);
        try {
            String displayFormat = this.getJavaDateFormat(this.getPropertyString("format"));
            String dataFormat = this.getPropertyString("formatJava");
            if (!displayFormat.equals(dataFormat)) {
                SimpleDateFormat data = new SimpleDateFormat(dataFormat);
                SimpleDateFormat display = new SimpleDateFormat(displayFormat);
                Date date = display.parse(values[0]);
                values[0] = data.format(date);
                date = display.parse(values[1]);
                values[1] = data.format(date);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        if (values != null && values[0] != null && !values[0].isEmpty()) {
            query = query + this.getQuery(name, columnName, true);
            queryValues.add(this.getConvertedDate(values[0], true));
        }
        if (values != null && values[1] != null && !values[1].isEmpty()) {
            if (!query.isEmpty()) {
                query = query + " AND ";
            }
            query = query + this.getQuery(name, columnName, false);
            queryValues.add(this.getConvertedDate(values[1], false));
        }
        if (!query.isEmpty()) {
            queryObject.setQuery(query);
            queryObject.setValues(queryValues.toArray(new String[0]));
            return queryObject;
        }
        return null;
	}

	public String getLabel() {
		return "Date Range";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return AppUtil.readPluginResource((String)this.getClass().getName(), 
				(String)"/properties/datalist/dateRangeDataListFilterType.json", 
				(Object[])null, (boolean)true, (String)"message/datalist/dateRangeDataListFilterType");
	}

	public String getName() {
		return "Date Range Data List Filter Type";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return "Data List Filter Type - Date Range";
	}

	public String getConvertedDate(String value, Boolean isStart) {
		String format = this.getPropertyString("formatJava");
        SimpleDateFormat sdf1 = new SimpleDateFormat(format);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            value = sdf2.format(sdf1.parse(value));
        }
        catch (Exception ex) {
            LogUtil.error((String)"DateDataListFilterType", (Throwable)null, (String)("Fail to convert date (" + value + ") from format (" + format + ") to format (" + "yyyy-MM-dd" + ")"));
        }
        value = isStart != false ? value + " 00:00:00.0" : value + " 23:59:59.0";
        return value;
	}
	
	private String getQuery(String name, String columnName, Boolean isStart) {
		String query = "";
        if ("dateCreated".equals(name) || "dateModified".equals(name)) {
            query = query + columnName;
        } else {
            String format = this.getPropertyString("formatJava");
            int yearIndex = format.indexOf("yyyy") + 1;
            int monthIndex = format.indexOf("MM") + 1;
            int dayIndex = format.indexOf("dd") + 1;
            query = query + "CONCAT(SUBSTRING(" + columnName + ", " + yearIndex + ", 4), '-', SUBSTRING(" + columnName + ", " + monthIndex + ", 2), '-', SUBSTRING(" + columnName + ", " + dayIndex + ", 2), ' 00:00:00.0')";
        }
        query = isStart != false ? query + " >= ?" : query + " <= ?";
        return query;
	}
	
	public String[] getValues(DataList datalist, String name) {
		String[] tempValues = this.getValues(datalist, name, null);
        String[] values = new String[2];
        values[0] = tempValues != null && tempValues[0] != null ? tempValues[0] : this.getPropertyString("fromDefaultValue");
        values[1] = tempValues != null && tempValues[1] != null ? tempValues[1] : this.getPropertyString("toDefaultValue");
        return values;
	}
	
	private String getJavaDateFormat(String format) {
		if (format == null || format.isEmpty()) {
            return "MM/dd/yyyy";
        }
        format = format.contains("DD") ? format.replaceAll("DD", "EEEE") : format.replaceAll("D", "EEE");
        format = format.contains("MM") ? format.replaceAll("MM", "MMM") : format.replaceAll("M", "MMMM");
        format = format.contains("mm") ? format.replaceAll("mm", "MM") : format.replaceAll("m", "M");
        format = format.contains("yy") ? format.replaceAll("yy", "yyyy") : format.replaceAll("y", "yy");
        return format;
	}
}
