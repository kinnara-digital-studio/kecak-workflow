package org.joget.apps.userview.model;

/**
 * Bootstrap AdminLTE Theme for {@link UserviewMenu}
 */
public interface AdminLteUserviewMenu extends BootstrapUserviewMenu, BootstrapAdminLteTheme {
    String getAdminLteJspPage(UserviewBootstrapTheme bootstrapTheme);

    String getAdminLteDecoratedMenu(UserviewBootstrapTheme bootstrapTheme);
}
