package org.joget.plugin.enterprise;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateFormatter extends DataListColumnFormatDefault {

	public String format(DataList dataList, DataListColumn column, Object row,
			Object value) {
		String result = "";
        if (value != null) {
            result = value.toString();
            try {
                SimpleDateFormat dataFormat = new SimpleDateFormat(this.getPropertyString("dataFormat"), LocaleContextHolder.getLocale());
                SimpleDateFormat displayFormat = new SimpleDateFormat(this.getPropertyString("displayFormat"), LocaleContextHolder.getLocale());
                Date date = dataFormat.parse(result);
                result = displayFormat.format(date);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        return result;
	}

	public String getLabel() {
		return "Date Formatter";
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return AppUtil.readPluginResource((String)this.getClass().getName(), 
				(String)"/properties/datalist/dateFormatter.json", 
				(Object[])null, (boolean)true, (String)null);
	}

	public String getName() {
		return "Date Formatter";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return "Format data data with various date format.";
	}

}
