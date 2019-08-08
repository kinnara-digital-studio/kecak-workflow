package org.joget.apps.userview.model;

/**
 * Bootstrap Ace Theme for {@link UserviewMenu}
 */
public interface AceUserviewMenu extends BootstrapUserviewMenu, BootstrapAceTheme {
    String getAceJspPage(UserviewBootstrapTheme bootstrapTheme);

    String getAceDecoratedMenu(UserviewBootstrapTheme bootstrapTheme);
}