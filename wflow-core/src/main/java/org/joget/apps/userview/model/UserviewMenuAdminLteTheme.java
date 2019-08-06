package org.joget.apps.userview.model;

public interface UserviewMenuAdminLteTheme extends UserviewMenuBootstrapTheme {
    /**
     * Decorate Menu
     *
     * @return
     */
    default String getBootstrapDecoratedMenu() {
        return getAdminLteDecoratedMenu();
    }

    /**
     * JSP Page
     *
     * @return
     */
    default String getBootstrapJspPage() {
        return getAdminLteJspPage();
    }

    /**
     * Decorate Menu
     *
     * @return
     */
    String getAdminLteDecoratedMenu();

    /**
     * JSP Page
     *
     * @return
     */
    String getAdminLteJspPage();
}
