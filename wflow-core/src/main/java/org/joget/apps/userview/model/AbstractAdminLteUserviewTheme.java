package org.joget.apps.userview.model;

/**
 * Base class for
 */
public abstract class BaseAdminLteUserviewTheme extends UserviewTheme implements UserviewBootstrapTheme, BootstrapAdminLteTheme  {
    @Override
    public String getUserviewJsp() {
        return "ubuilder/adminLteView";
    }

    @Override
    public String getDataListJsp() {
        return null;
    }

    @Override
    public String getFormJsp() {
        return null;
    }

    @Override
    public String getRunProcessJsp() {
        return null;
    }
}