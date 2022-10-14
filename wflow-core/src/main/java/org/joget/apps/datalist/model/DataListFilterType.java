package org.joget.apps.datalist.model;

import org.joget.plugin.property.model.PropertyEditable;
import org.kecak.apps.datalist.model.AceDataListFilterType;
import org.kecak.apps.datalist.model.AdminKitDataListFilterType;
import org.kecak.apps.datalist.model.AdminLteDataListFilterType;

/**
 * Interface of Datalist Filter Type Plugin
 * 
 */
public interface DataListFilterType extends PropertyEditable, AceDataListFilterType,
        AdminLteDataListFilterType, AdminKitDataListFilterType {

    /**
     * HTML template of the filter
     * 
     * @param datalist
     * @param name
     * @param label
     * @return 
     */
    public String getTemplate(DataList datalist, String name, String label);

    /**
     * Condition and parameters to construct query
     * 
     * @param datalist
     * @param name
     * @return 
     */
    public DataListFilterQueryObject getQueryObject(DataList datalist, String name);
}
