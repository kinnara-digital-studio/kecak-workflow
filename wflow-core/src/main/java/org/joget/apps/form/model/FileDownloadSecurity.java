package org.joget.apps.form.model;

import java.util.Map;

public interface FileDownloadSecurity {
    boolean isDownloadAllowed(@SuppressWarnings("rawtypes") Map requestParameters);
}
