package com.kecak.soap.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface SoapProcessesService {
	String processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nullable Map<String, String> workflowVariable);
}
