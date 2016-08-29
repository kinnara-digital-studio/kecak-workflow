package org.joget.plugin.enterprise;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.datalist.model.DataListFilterTypeDefault;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;

public class DateDataListFilterType extends DataListFilterTypeDefault {

	protected final String DATE_FORMAT = "yyyy-MM-dd";
    protected final String DATE_CREATED = "dateCreated";
    protected final String DATE_MODIFIED = "dateModified";
    
	public String getTemplate(DataList datalist, String name, String label) {
		PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
		HashMap<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("element", (Object) this);
		dataModel.put("name",
				datalist.getDataListEncodedParamName("fn_" + name));
		dataModel.put("label", label);
		dataModel.put("value", this.getValue(datalist, name, this.getPropertyString("defaultValue")));
		dataModel.put("contextPath", WorkflowUtil.getHttpServletRequest()
				.getContextPath());
		return pluginManager.getPluginFreeMarkerTemplate(dataModel,
				this.getClassName(),
				"/templates/dateDataListFilterType.ftl", null);
	}
	public DataListFilterQueryObject getQueryObject(DataList datalist,
			String name) {
		DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        String value = this.getValue(datalist, name, this.getPropertyString("defaultValue"));
        if (datalist != null && datalist.getBinder() != null && value != null && !value.isEmpty()) {
            try {
                String displayFormat = this.getJavaDateFormat(this.getPropertyString("format"));
                String dataFormat = this.getPropertyString("formatJava");
                if (!displayFormat.equals(dataFormat)) {
                    SimpleDateFormat data = new SimpleDateFormat(dataFormat);
                    SimpleDateFormat display = new SimpleDateFormat(displayFormat);
                    Date date = display.parse(value);
                    value = data.format(date);
                }
            }
            catch (Exception e) {
                // empty catch block
            }
            String columnName = datalist.getBinder().getColumnName(name);
            queryObject.setQuery(this.getQuery(name, columnName));
            queryObject.setValues(new String[]{this.getConvertedDate(name, columnName, value)});
            return queryObject;
        }
        return null;
	}
	
	private String getConvertedDate(String name, String columnName, String value) {
		String format = this.getPropertyString("formatJava");
        if ("dateCreated".equals(name) || "dateModified".equals(name)) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(format);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                value = sdf2.format(sdf1.parse(value)) + " %";
            }
            catch (Exception ex) {
                LogUtil.error((String)"DateDataListFilterType", (Throwable)null, (String)("Fail to convert date (" + value + ") from format (" + format + ") to format (" + "yyyy-MM-dd" + ")"));
            }
        }
        return value;
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
	
	private String getQuery(String name, String columnName) {
		return (columnName = "dateCreated".equals(name) || "dateModified".equals(name) ? columnName + " like ?" : columnName + " = ?");
	}
	
	public String getLabel() {
		return "Date";
	}
	public String getClassName() {
		return this.getClass().getName();
	}
	public String getPropertyOptions() {
		return AppUtil.readPluginResource((String)this.getClass().getName(), 
				(String)"/properties/datalist/dateDataListFilterType.json", (Object[])null, (boolean)true, 
				(String)"message/datalist/dateDataListFilterType");
	}
	public String getName() {
		return "Date Data List Filter Type"; 	
	}
	public String getVersion() {
		return "5.0.0";
	}
	public String getDescription() {
		return "Data List Filter Type - Date";
	}
}
