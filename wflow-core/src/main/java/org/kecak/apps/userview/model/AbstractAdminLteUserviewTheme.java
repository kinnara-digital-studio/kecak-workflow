package org.kecak.apps.userview.model;

import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterType;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.userview.model.UserviewTheme;
import org.kecak.apps.form.model.AdminLteFormElement;

import java.util.Map;
import java.util.Objects;

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
        Objects.requireNonNull(element);
        return ((AdminLteFormElement) element).renderAdminLteTemplate(formData, dataModel);
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        return ((AdminLteUserviewMenu) menu).getAdminLteJspPage(this);
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        return ((AdminLteUserviewMenu) menu).getAdminLteRenderPage();
    }

    @Override
    public String getBootstrapDecoratedMenu(UserviewMenu menu) {
        return ((AdminLteUserviewMenu) menu).getAdminLteDecoratedMenu();
    }

    @Override
    public String getNavigationBarHeader() {
        return "<!-- mini logo for sidebar mini 50x50 pixels -->\n" +
                " <span class=\"logo-mini\"><b>A</b>LT</span>\n" +
                " <!-- logo for regular state and mobile devices -->\n" +
                " <span class=\"logo-lg\">" + getUserview().getPropertyString("name") + "</span>";
    }

    @Override
    public String renderBootstrapDataListFilterTemplate(DataList dataList, DataListFilterType filterType, String name, String label) {
        Objects.requireNonNull(dataList);
        Objects.requireNonNull(filterType);
        return filterType.getAdminLteTemplate(dataList, name, label);
    }
}
