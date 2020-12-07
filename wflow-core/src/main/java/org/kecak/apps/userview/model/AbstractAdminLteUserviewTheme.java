package org.kecak.apps.userview.model;

import org.joget.apps.form.model.AdminLteFormElement;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.userview.model.UserviewTheme;

import java.util.Map;

/**
 * @author aristo
 *
 * Abstract class to handle AdminLTE Theme
 */
public abstract class AbstractAdminLteUserviewTheme extends UserviewTheme implements BootstrapUserviewTheme {
    @Override
    public final String getUserviewJsp() {
        return "ubuilder/AdminLteView";
    }

    @Override
    public final String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public final String getDataListJsp() {
        return "userview/plugin/AdminLteDataList.jsp";
    }

    @Override
    public final String getFormJsp() {
        return "userview/plugin/AdminLteForm.jsp";
    }

    @Override
    public final String getRunProcessJsp() {
        return "userview/plugin/AdminLteRunProcess.jsp";
    }

    @Override
    public final String getLoginJsp() {
        return "ubuilder/AdminLteLogin";
    }

    @Override
    public String renderBootstrapFormElementTemplate(Element element, FormData formData, Map dataModel) {
        if(element instanceof AdminLteFormElement) {
            return ((AdminLteFormElement) element).renderAdminLteTemplate(formData, dataModel);
        } else {
            return element.renderTemplate(formData, dataModel);
        }
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        if(menu instanceof AdminLteUserviewMenu) {
            return ((AdminLteUserviewMenu) menu).getAdminLteJspPage(this);
        } else {
            return menu.getJspPage();
        }
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        if(menu instanceof AdminLteUserviewMenu) {
            return ((AdminLteUserviewMenu) menu).getAdminLteRenderPage();
        } else {
            return menu.getRenderPage();
        }
    }

    @Override
    public String getBootstrapDecoratedMenu(UserviewMenu menu) {
        if(menu instanceof AdminLteUserviewMenu) {
            return ((AdminLteUserviewMenu) menu).getAdminLteDecoratedMenu();
        } else {
            return menu.getDecoratedMenu();
        }
    }

    @Override
    public String getNavigationBarHeader() {
        return "<!-- mini logo for sidebar mini 50x50 pixels -->\n" +
                " <span class=\"logo-mini\"><b>A</b>LT</span>\n" +
                " <!-- logo for regular state and mobile devices -->\n" +
                " <span class=\"logo-lg\">" + getUserview().getPropertyString("name") + "</span>";
    }
}
