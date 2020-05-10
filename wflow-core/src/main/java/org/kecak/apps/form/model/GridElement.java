package org.kecak.apps.form.model;

import java.util.Map;

/**
 * @author aristo
 *
 * Indicator for Grid Element
 */
public interface GridElement {

    String formatColumn(String name, Map<String, Object> properties, String recordId, String value, String appId, Long appVersion, String contextPath);

    Map<String, Object>[] getPropertyGrid();
}
