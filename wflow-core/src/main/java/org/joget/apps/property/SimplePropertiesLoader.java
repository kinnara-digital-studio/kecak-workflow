package org.joget.apps.property;

import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderSupport;

public class SimplePropertiesLoader extends PropertiesLoaderSupport implements PropertiesLoader {

	public Properties loadProperties() {
		try {
			Properties mergeProperties = new Properties();
			mergeProperties = super.mergeProperties();
			return mergeProperties;
		} catch (Exception e) {
			return null;
		}
	}

}
