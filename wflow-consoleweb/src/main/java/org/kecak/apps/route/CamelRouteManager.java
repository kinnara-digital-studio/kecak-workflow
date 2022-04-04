package org.kecak.apps.route;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.joget.commons.util.LogUtil;

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
			getCamelContext().getRoutes().forEach(r -> {
				try {
					// remove all existing routes
					getCamelContext().removeEndpoint(r.getEndpoint());
					getCamelContext().stop();
					getCamelContext().removeRoute(r.getId());
				} catch (Exception e) {
					LogUtil.error(getClass().getName(), e, e.getMessage());
				}
			});

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
			// add again deleted routes
			getCamelContext().addRoutes(new EmailProcessorRouteBuilder());
			getCamelContext().start();
			result = true;
			LogUtil.info(getClass().getName(), getCamelContext().getName()+" is started");
		} catch (Exception e) {
			LogUtil.error(getClass().getName(), e, e.getMessage());
		}
		
		return result;
	}
}
