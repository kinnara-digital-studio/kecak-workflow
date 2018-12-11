package com.kecak.soap.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface SoapProcessService {
	/**
	 *
	 * @param appId application ID
	 * @param appVersion application version, use 0 for published version
	 * @param processId process ID, the one without #
	 * @param workflowVariable
	 * @return
	 */
	String processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nullable Map<String, String> workflowVariable);
}
