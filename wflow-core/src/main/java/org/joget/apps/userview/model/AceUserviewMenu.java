package org.joget.apps.userview.model;

/**
 * @author aristo
 *
 * Bootstrap Ace Theme for {@link UserviewMenu}
 */
public interface AceUserviewMenu extends BootstrapUserviewMenu, BootstrapAceTheme {
    String getAceJspPage(BootstrapUserview bootstrapTheme);

    String getAceDecoratedMenu(BootstrapUserview bootstrapTheme);
}