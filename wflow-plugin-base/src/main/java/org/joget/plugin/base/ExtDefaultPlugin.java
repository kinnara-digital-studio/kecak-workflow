package org.joget.plugin.base;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base abstract class that must be extended by every plugins
 * 
 */
public abstract class ExtDefaultPlugin extends DefaultPlugin {
    private Map<String, Object> properties;
    
    /**
     * Get plugin properties.
     * @return 
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set plugin properties.
     * @param properties 
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    /**
     * Get a plugin property value by property key.
     * 
     * @param property 
     */
    public Object getProperty(String property) {
        Object value = (properties != null) ? properties.get(property) : null;
        return value;
    }
    
    /**
     * Get a plugin property value by property key and return in java.lang.String. Non-exist key 
     * will return an empty string instead of NULL value.
     * 
     * @param property 
     */
    @Nonnull
    public String getPropertyString(String property) {
        String value = (properties != null && properties.get(property) != null) ? (String) properties.get(property) : "";
        return value;
    }

    /**
     * Get a plugin property value by property key and return in {@link Set}. Non-exist key
     * will return an empty set instead of NULL value.
     *
     * @param property
     */
    @Nonnull
    public Set<String> getPropertySet(String property) {
        return Arrays.stream(getPropertyString(property).split(";"))
                .collect(Collectors.toSet());
    }

    /**
     * Get a plugin property value by property key and return in {@link Map}. Non-exist key
     * will return an empty map instead of NULL value.
     *
     * @param property
     */
    @Nonnull
    public Map<String, String>[] getPropertyGrid(String property) {
        return Optional.ofNullable(getProperty(property))
                .map(o -> (Object[]) o)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .map(o -> (Map<String, String>) o)
                .toArray(Map[]::new);
    }
    
    /**
     * Set a plugin property
     * 
     * @param property A property key
     * @param value 
     */
    public void setProperty(String property, Object value) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        properties.put(property, value);
    }
    
    /**
     * Return a set of plugin properties to configure by admin user
     * 
     * @Deprecated Since version 3, Joget Workflow introduced a better UI for plugin
     * configuration. A plugin should implement org.joget.plugin.property.model.PropertyEditable 
     * interface to provide the plugin configuration options.
     * 
     * @return 
     */
    public PluginProperty[] getPluginProperties() {
        return null;
    }
    
    /**
     * To execute a plugin
     * 
     * @Deprecated This method is only use by Process Tool plugin therefore it had
     * been moved to org.joget.plugin.base.DefaultApplicationPlugin
     * 
     * @param pluginProperties Properties to be used by the plugin during execution
     * 
     * @return
     */
    public Object execute(@SuppressWarnings("rawtypes") Map properties) {
        return null;
    }
}
