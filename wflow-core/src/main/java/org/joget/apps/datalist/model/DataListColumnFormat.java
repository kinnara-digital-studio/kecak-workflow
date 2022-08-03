package org.joget.apps.datalist.model;

import org.joget.apps.form.service.FormUtil;
import org.joget.plugin.property.model.PropertyEditable;

import javax.annotation.Nonnull;
import java.util.Map;

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
     * Get Sort As
     *
     * Manipulate how the column will be sort when being displayed on dataList.
     *
     * When using {@link org.joget.apps.form.dao.FormDataDao} for example binder {@link org.joget.apps.datalist.lib.FormRowDataListBinder},
     * if the return contains question mark character ('?'), the binder will use its own ORDER BY formula;
     * otherwise default CAST([column] AS [type]) function will be used.
     *
     * @param dataList
     * @param column
     * @return
     */
    String getSortAs(@Nonnull DataList dataList, @Nonnull DataListColumn column);
}
