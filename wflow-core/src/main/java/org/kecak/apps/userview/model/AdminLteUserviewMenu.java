package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewMenu;

/**
 * @author aristo
 *
 * Bootstrap AdminLTE Theme for {@link UserviewMenu}
 */
public interface AdminLteUserviewMenu extends BootstrapUserviewMenu, BootstrapAdminLteTheme {
    String getAdminLteJspPage(BootstrapUserview bootstrapTheme);

    String getAdminLteDecoratedMenu(BootstrapUserview bootstrapTheme);
}
