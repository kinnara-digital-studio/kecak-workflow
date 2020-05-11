package org.kecak.apps.form.model;

import java.util.Map;

/**
 * @author aristo
 *
 * Indicator for Grid Element
 */
public interface GridElement {

    /**
     * Value formatting when displaying to grid element
     *
     * @param name
     * @param properties
     * @param recordId
     * @param value
     * @param appId
     * @param appVersion
     * @param contextPath
     * @return
     */
    String formatColumn(String name, Map<String, Object> properties, String recordId, String value, String appId, Long appVersion, String contextPath);

    /**
     * Get properties of grid column
     *
     * @return
     */
    Map<String, Object>[] getColumnProperties();
}
