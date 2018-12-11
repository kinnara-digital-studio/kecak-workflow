package org.kecak.soap.service;

import java.util.Map;

public interface KecakSoapService {
	String processStart(String appId, String processId, Map<String, String> workflowVariable);
}
