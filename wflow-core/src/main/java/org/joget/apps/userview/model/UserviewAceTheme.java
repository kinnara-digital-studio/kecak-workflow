package org.joget.apps.userview.model;

public interface UserviewAceTheme extends BootstrapUserview, BootstrapAceTheme {
    @Override
    default String getUserviewJsp() {
        return "ubuilder/newView";
    }
}
