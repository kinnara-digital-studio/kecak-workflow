package org.kecak.soap.service;

import javax.annotation.Nonnull;
import java.util.Map;

public interface SoapFormService {
    /**
     *
     * @param appId
     * @param appVersion
     * @param formDefId
     * @param data
     * @return
     */
    void formSubmit(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String formDefId, @Nonnull Map<String, String> data);
}
