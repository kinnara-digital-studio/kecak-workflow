package org.joget.apps.userview.model;

import org.joget.apps.BootstrapTheme;

/**
 * Bootstrap Web UI Framework for Userview Menu
 */
public interface UserviewMenuBootstrapTheme extends BootstrapTheme {
    /**
     * Decorate Menu
     *
     * @return
     */
    String getBootstrapDecoratedMenu();

    /**
     * JSP Page
     *
     * @return
     */
    String getBootstrapJspPage();
}
