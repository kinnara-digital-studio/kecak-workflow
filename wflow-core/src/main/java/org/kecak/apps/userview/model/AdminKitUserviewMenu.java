package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewMenu;

/**
 * @author Yonathan
 *
 * Bootstrap AdminLTE Theme for {@link UserviewMenu}
 */
public interface AdminKitUserviewMenu extends BootstrapUserviewMenu, BootstrapAdminKitTheme {
    String getAdminKitJspPage(BootstrapUserview bootstrapTheme);

    String getAdminKitDecoratedMenu(BootstrapUserview bootstrapTheme);
}
