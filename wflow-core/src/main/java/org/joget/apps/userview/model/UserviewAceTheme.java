package org.joget.apps.userview.model;

public interface UserviewAceTheme extends UserviewBootstrapTheme, BootstrapAceTheme {
    @Override
    default String getUserviewJsp() {
        return "ubuilder/newView";
    }
}
