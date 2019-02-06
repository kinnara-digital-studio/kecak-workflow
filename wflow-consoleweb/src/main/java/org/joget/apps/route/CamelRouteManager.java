package org.joget.apps.route;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.joget.commons.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelRouteManager implements CamelContextAware {

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
			LogUtil.info(getClass().getName(), getCamelContext().getName()+" is stopped");
		} catch (Exception e) {
			LogUtil.error(getClass().getName(), e, e.getMessage());
		}
		
		return result;
	}
	
	public boolean startContext() {
		boolean result = false;
		try {
			getCamelContext().start();
			result = true;
			LogUtil.info(getClass().getName(), getCamelContext().getName()+" is started");
		} catch (Exception e) {
			LogUtil.error(getClass().getName(), e, e.getMessage());
		}
		
		return result;
	}
}
