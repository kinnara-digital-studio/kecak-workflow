package org.joget.apps.userview.model;

public interface UserviewAceTheme extends UserviewBootstrapTheme {
    @Override
    default String getView() {
        return "ubuilder/newView";
    }
}
