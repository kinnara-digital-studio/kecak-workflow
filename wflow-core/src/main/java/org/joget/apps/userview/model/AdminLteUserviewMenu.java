package org.joget.apps.userview.model;

/**
 * Bootstrap AdminLTE Theme for {@link UserviewMenu}
 */
public interface AdminLteUserviewMenu extends BootstrapUserviewMenu, BootstrapAdminLteTheme {
    String getAdminLteJspPage(BootstrapUserview bootstrapTheme);

    String getAdminLteDecoratedMenu(BootstrapUserview bootstrapTheme);
}
