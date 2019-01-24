package org.kecak.apps.route;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelRouteManager implements CamelContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelRouteManager.class);

    protected CamelContext camelContext;

	public CamelContext getCamelContext() {
		return camelContext;
	}

	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public boolean stopContext() {
		boolean result = false;
		try {
			getCamelContext().stop();
			result = true;
			LOGGER.info(getCamelContext().getName()+" is stopped");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return result;
	}
	
	public boolean startContext() {
		boolean result = false;
		try {
			getCamelContext().start();
			result = true;
			LOGGER.info(getCamelContext().getName()+" is started");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return result;
	}
}
