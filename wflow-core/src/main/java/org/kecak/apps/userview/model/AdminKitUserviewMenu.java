package org.kecak.apps.userview.model;

public interface AdminKitUserviewMenu extends BootstrapUserviewMenu {
    String getAdminKitJspPage(BootstrapUserviewTheme theme);

    String getAdminKitRenderPage();

    String getAdminKitDecoratedMenu();
}
