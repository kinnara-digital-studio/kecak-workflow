package org.kecak.apps.userview.model;

import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterType;
import org.kecak.apps.datalist.model.AceDataListFilterType;
import org.kecak.apps.form.model.AceFormElement;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.userview.model.UserviewTheme;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author aristo
 *
 * Abstract class to handle Ace Theme
 */
public abstract class AbstractAceUserviewTheme extends UserviewTheme implements BootstrapUserviewTheme {
    @Override
    public final String getUserviewJsp() {
        return "ubuilder/AceView";
    }

    @Override
    public final String getPreviewJsp() {
        return getUserviewJsp();
    }

    @Override
    public final String getDataListJsp() {
//        return "userview/plugin/AceDataList.jsp";
        return "userview/plugin/datalist.jsp";
    }

    @Override
    public final String getFormJsp() {
        return "userview/plugin/AceForm.jsp";
    }

    @Override
    public final String getRunProcessJsp() {
        return "userview/plugin/AceRunProcess.jsp";
    }

    @Override
    public final String getLoginJsp() {
        return "ubuilder/AceLogin";
    }

    @Override
    public String renderBootstrapFormElementTemplate(Element element, FormData formData, Map dataModel) {
        Objects.requireNonNull(element);
        return ((AceFormElement) element).renderAceTemplate(formData, dataModel);
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        if(menu instanceof AceUserviewMenu) {
            return ((AceUserviewMenu) menu).getAceJspPage(this);
        } else {
            return menu.getJspPage();
        }
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        if(menu instanceof AceUserviewMenu) {
            return ((AceUserviewMenu) menu).getAceRenderPage();
        } else {
            return menu.getRenderPage();
        }
    }

    @Override
    public String getBootstrapDecoratedMenu(UserviewMenu menu) {
        return ((AceUserviewMenu) menu).getAceDecoratedMenu();
    }

    @Override
    public String getNavigationBarHeader() {
        return "<small>" + getUserview().getPropertyString("name") + "</small>";
    }

    @Override
    public String renderBootstrapDataListFilterTemplate(DataList dataList, DataListFilterType filterType, String name, String label) {
        Objects.requireNonNull(dataList);
        Objects.requireNonNull(filterType);
        return filterType.getAceTemplate(dataList, name, label);
    }
}
