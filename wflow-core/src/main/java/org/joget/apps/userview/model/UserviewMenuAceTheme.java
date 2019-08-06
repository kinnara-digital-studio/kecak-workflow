package org.joget.apps.userview.model;

public interface UserviewMenuAceTheme extends UserviewMenuBootstrapTheme {
    /**
     * Decorate Menu
     *
     * @return
     */
    default String getBootstrapDecoratedMenu() {
        return getAceDecoratedMenu();
    }

    /**
     * JSP Page
     *
     * @return
     */
    default String getBootstrapJspPage() {
        return getAceLteJspPage();
    }

    /**
     * Decorate Menu
     *
     * @return
     */
    String getAceDecoratedMenu();

    /**
     * JSP Page
     *
     * @return
     */
    String getAceLteJspPage();
}
