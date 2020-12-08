package org.kecak.apps.userview.model;

import org.joget.apps.form.model.AdminKitFormElement;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.userview.model.UserviewTheme;

import java.util.Map;

/**
 *
 * Abstract class to handle AdminKit Theme
 */
public abstract class AbstractAdminKitUserviewTheme extends UserviewTheme implements BootstrapUserviewTheme {
    @Override
    public final String getUserviewJsp() {
        return "ubuilder/AdminKitView";
    }

    @Override
    public final String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public final String getDataListJsp() {
        return "userview/plugin/AdminKitDataList.jsp";
    }

    @Override
    public final String getFormJsp() {
        return "userview/plugin/AdminKitForm.jsp";
    }

    @Override
    public final String getRunProcessJsp() {
        return "userview/plugin/AdminKitRunProcess.jsp";
    }

    @Override
    public final String getLoginJsp() {
        return "ubuilder/AdminKitLogin";
    }

    @Override
    public String renderBootstrapFormElementTemplate(Element element, FormData formData, Map dataModel) {
        if(element instanceof AdminKitFormElement) {
            return ((AdminKitFormElement) element).renderAdminKitTemplate(formData, dataModel);
        } else {
            return element.renderTemplate(formData, dataModel);
        }
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        if(menu instanceof AdminKitUserviewMenu) {
            return ((AdminKitUserviewMenu) menu).getAdminKitJspPage(this);
        } else {
            return menu.getJspPage();
        }
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        if(menu instanceof AdminKitUserviewMenu) {
            return ((AdminKitUserviewMenu) menu).getAdminKitRenderPage();
        } else {
            return menu.getRenderPage();
        }
    }

    @Override
    public String getBootstrapDecoratedMenu(UserviewMenu menu) {
        if(menu instanceof AdminKitUserviewMenu) {
            return ((AdminKitUserviewMenu) menu).getAdminKitDecoratedMenu();
        } else {
            return menu.getDecoratedMenu();
        }
    }

    @Override
    public String getNavigationBarHeader() {

        return "<span class=\"align-middle\">" + userview.getPropertyString("name") + "</span>";
    }
}
