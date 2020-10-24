package org.kecak.apps.userview.model;

import org.joget.apps.form.model.AceFormElement;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.apps.userview.model.UserviewTheme;

import java.util.Map;

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
        return "userview/plugin/AceDataList.jsp";
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
        assert element instanceof AceFormElement;

        return ((AceFormElement) element).renderAceTemplate(formData, dataModel);
    }

    @Override
    public String getBootstrapJspPage(UserviewMenu menu) {
        assert menu instanceof AceUserviewMenu;

        return ((AceUserviewMenu) menu).getAceJspPage(this);
    }

    @Override
    public String getBootstrapRenderPage(UserviewMenu menu) {
        assert menu instanceof AceUserviewMenu;

        return ((AceUserviewMenu) menu).getAceRenderPage();
    }

    @Override
    public String getBootstrapDecoratedMenu(UserviewMenu menu) {
        assert menu instanceof AceUserviewMenu;

        return ((AceUserviewMenu) menu).getAceDecoratedMenu(this);
    }
}
