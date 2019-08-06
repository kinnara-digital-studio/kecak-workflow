package org.joget.apps.userview.model;

public interface UserviewAdminLteTheme extends UserviewBootstrapTheme {
    @Override
    default String getView() {
        return "ubuilder/adminLteView";
    }
}
