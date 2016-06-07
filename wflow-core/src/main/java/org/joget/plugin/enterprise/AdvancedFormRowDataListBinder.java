package org.joget.plugin.enterprise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.lib.FormRowDataListBinder;
import org.joget.apps.datalist.model.DataListFilterQueryObject;

public class AdvancedFormRowDataListBinder extends FormRowDataListBinder {

	public String getClassName() {
		return this.getClass().getName();
	}
	
	public String getName() {
		return this.getClass().getName();
	}
	
	public String getVersion() {
		return "5.0.0";
	}
	
	public String getDescription() {
		return "Retrieves data rows from a form table.";
	}
	
	public String getLabel() {
		return "Advanced Form Data Binder";
	}
	
	public String getPropertyOptions() {
		AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        return AppUtil.readPluginResource((String)this.getClass().getName(), 
        		(String)"/properties/datalist/advancedFormRowDataListBinder.json", 
        		(Object[])new String[]{appDef.getId(), appDef.getVersion().toString()}, 
        		(boolean)true, 
        		(String)"message/datalist/advancedFormRowDataListBinder");
    
	}
	
	public DataListFilterQueryObject getCriteria(@SuppressWarnings("rawtypes") Map properties, DataListFilterQueryObject[] filterQueryObjects) {
		ArrayList<DataListFilterQueryObject> newFilterObjects = new ArrayList<DataListFilterQueryObject>();
        Object[] filters = (Object[])properties.get("filters");
        if (filters != null && filters.length > 0) {
        	ArrayList<DataListFilterQueryObject> conditionObjects = new ArrayList<DataListFilterQueryObject>();
            for (Object o : filters) {
                @SuppressWarnings("rawtypes")
				HashMap filterMap = (HashMap)o;
                DataListFilterQueryObject obj = new DataListFilterQueryObject();
                obj.setOperator(filterMap.get("join").toString());
                String field = this.getColumnName(filterMap.get("field").toString());
                String operator = filterMap.get("operator").toString();
                String value = filterMap.get("value").toString();
                String query = "";
                String[] values = null;
                if ("IS TRUE".equals(operator) || "IS FALSE".equals(operator) || "IS NULL".equals(operator) || "IS NOT NULL".equals(operator)) {
                    query = field + " " + operator;
                    values = new String[]{};
                } else if ("IN".equals(operator) || "NOT IN".equals(operator)) {
                    values = value.split(";");
                    if (values.length > 0) {
                        query = field + " " + operator + " (";
                        for (@SuppressWarnings("unused") String v : values) {
                            query = query + "?,";
                        }
                        query = query.replaceFirst(",$", ")");
                    }
                } else if ("=".equals(operator) || "<>".equals(operator)) {
                    query = field + " " + operator + " ?";
                    values = new String[]{value};
                } else if (!value.isEmpty()) {
                    query = field + " " + operator + " ?";
                    values = new String[]{value};
                }
                if (query.isEmpty()) continue;
                obj.setQuery(query);
                obj.setValues(values);
                conditionObjects.add(obj);
            }
            DataListFilterQueryObject condition = this.processFilterQueryObjects(conditionObjects.toArray(new DataListFilterQueryObject[0]));
            if (condition != null && !condition.getQuery().isEmpty()) {
                condition.setOperator("AND");
                condition.setQuery("(" + condition.getQuery() + ")");
                newFilterObjects.add(condition);
            }
        }
        if (filterQueryObjects != null && filterQueryObjects.length > 0) {
            newFilterObjects.addAll(Arrays.asList(filterQueryObjects));
        }
        return this.getCriteria(properties, newFilterObjects.toArray(new DataListFilterQueryObject[0]));
	}
}
