package org.kecak.apps.userview.model;

import org.joget.apps.userview.model.UserviewMenu;

/**
 * @author aristo
 *
 * Bootstrap Ace Theme for {@link UserviewMenu}
 */
public interface AceUserviewMenu extends BootstrapUserviewMenu, BootstrapAceTheme {
    default String getAceJspPage(BootstrapUserview bootstrapTheme) {
        return null;
    }

    String getAceRenderPage();

    String getAceDecoratedMenu(BootstrapUserview bootstrapTheme);
}