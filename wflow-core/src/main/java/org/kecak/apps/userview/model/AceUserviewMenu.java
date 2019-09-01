package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewMenu;

/**
 * @author aristo
 *
 * Bootstrap Ace Theme for {@link UserviewMenu}
 */
public interface AceUserviewMenu extends BootstrapUserviewMenu, BootstrapAceTheme {
    String getAceJspPage(BootstrapUserview bootstrapTheme);

    String getAceDecoratedMenu(BootstrapUserview bootstrapTheme);
}