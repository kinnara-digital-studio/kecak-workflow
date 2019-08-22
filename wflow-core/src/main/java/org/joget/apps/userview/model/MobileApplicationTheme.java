package org.joget.apps.userview.model;

import java.awt.*;

/**
 * @author aristo
 *
 * Theming for mobile application. This interface should be implemented in {@link UserviewTheme}
 * or its children
 */
public interface MobileApplicationTheme {
    /**
     * Color for:
     *  <li>Application Bar</li>
     *  <li>Button</li>
     */
    Color getPrimaryColor();

    /**
     * Color for:
     *  <li></li>
     *
     * @return
     */
    Color getAccentColor();

    /**
     * Color for app's background
     *
     * @return
     */
    Color getBackgroundColor();

    /**
     * Color for text in:
     *  <li>Navigation drawer</li>
     *  <li>Element text</li>
     *
     * @return
     */
    Color getPrimaryFontColor();

    /**
     * Color for:
     *  <li>Application Bar's title</li>
     *
     * @return
     */
    Color getSecondaryFontColor();

    /**
     *
     * @return
     */
    String getUserviewLogo();
}
