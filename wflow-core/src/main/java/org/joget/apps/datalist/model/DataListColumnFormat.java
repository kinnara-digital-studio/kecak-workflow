package org.joget.apps.datalist.model;

import org.joget.apps.form.service.FormUtil;
import org.joget.plugin.property.model.PropertyEditable;

import javax.annotation.Nonnull;

/**
 * Interface of Datalist Column Formatter plugin
 */
public interface DataListColumnFormat extends PropertyEditable {

    /**
     * Format column value 
     * 
     * @param dataList
     * @param column
     * @param row
     * @param value
     * @return
     */
    String format(DataList dataList, DataListColumn column, Object row, Object value);

    /**
     *
     * @param dataList
     * @param column
     * @return SQL data type
     */
    default String getSortAs(@Nonnull DataList dataList, @Nonnull DataListColumn column) {
        final String sort = column.getName();
        return FormUtil.PROPERTY_DATE_CREATED.equals(sort) || FormUtil.PROPERTY_DATE_MODIFIED.equals(sort) ? "timestamp" : "string";
    }
}
