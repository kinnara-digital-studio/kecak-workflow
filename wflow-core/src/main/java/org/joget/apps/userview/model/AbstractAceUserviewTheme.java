package org.joget.apps.userview.model;

public abstract class AbstractAceUserviewTheme extends UserviewTheme implements BootstrapUserview, BootstrapAceTheme {
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
