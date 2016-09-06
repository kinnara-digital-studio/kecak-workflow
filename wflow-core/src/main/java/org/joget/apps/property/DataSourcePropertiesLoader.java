package org.joget.apps.property;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.joget.apps.property.dao.PropertyDao;
import org.joget.apps.property.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.CollectionUtils;

/**
 * @author ARDI PRIASA
 * 
 */
public class DataSourcePropertiesLoader extends PropertiesLoaderSupport
		implements PropertiesLoader {

	private static final Logger logger = LoggerFactory
			.getLogger(DataSourcePropertiesLoader.class);

	protected boolean dbOverride = true;

	private PropertyDao propertyDao = null;
	
	public void setDbOverride(boolean dbOverride) {
		this.dbOverride = dbOverride;
	}

	public Properties loadProperties() throws IOException {
		Properties mergeProperties = new Properties();

		if (this.dbOverride) {
			mergeProperties = super.mergeProperties();
		}

		CollectionUtils.mergePropertiesIntoMap(loadDbProperties(),
				mergeProperties);
		if (!this.dbOverride) {
			CollectionUtils.mergePropertiesIntoMap(super.mergeProperties(),
					mergeProperties);
		}
		return mergeProperties;
	}

	protected Properties loadDbProperties() {
		Properties result = new Properties();
		if (propertyDao != null) {
			List<Property> properties = propertyDao.getAll();
			for (Property property : properties) {
				result.put(property.getPropertyLabel(), property.getPropertyValue());
			}
		} else {
			logger.info("ParameterDao is not set for ParameterServiceImpl");
		}

		StringBuffer sb = new StringBuffer();
		for (Object key : result.keySet().toArray()) {
			sb.append(key).append("=").append(result.get(key)).append("\n");
		}
		logger.info("\n-- listing properties from database --\n{}",
				sb.toString());
		return result;
	}

	public PropertyDao getPropertyDao() {
		return propertyDao;
	}

	public void setPropertyDao(PropertyDao propertyDao) {
		this.propertyDao = propertyDao;
	}

	
}
