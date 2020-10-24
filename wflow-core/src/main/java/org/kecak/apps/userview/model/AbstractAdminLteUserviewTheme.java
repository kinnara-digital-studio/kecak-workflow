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
        assert element instanceof AdminLteFormElement;

        return ((AdminLteFormElement) element).renderAdminLteTemplate(formData, dataModel);
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        assert menu instanceof AdminLteUserviewMenu;

        return ((AdminLteUserviewMenu) menu).getAdminLteJspPage(this);
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        assert menu instanceof AdminLteUserviewMenu;

        return ((AdminLteUserviewMenu) menu).getAdminLteRenderPage();
    }
}
