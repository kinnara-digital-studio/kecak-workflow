package org.joget.commons.spring.web;

import javax.servlet.ServletContextEvent;

import org.joget.apps.app.controller.ConsoleWebController;
import org.joget.commons.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * Overrides Spring's ContextLoaderListener to support re-initialization of the 
 * ApplicationContext if previous attempts fail.
 */
public class CustomContextLoaderListener extends ContextLoaderListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomContextLoaderListener.class);

    public CustomContextLoaderListener() {
    }

    public CustomContextLoaderListener(WebApplicationContext context) {
        super(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            LogUtil.info(getClass().getName(), "===== Initializing WebApplicationContext =====");
            super.contextInitialized(event);
        } catch(Exception e) {
//            Exception exceptionToLog = (e instanceof BeanCreationException) ? null : e;
            LOGGER.error(e.getMessage());
            contextDestroyed(event);
        }
    }
    
}
