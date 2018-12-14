package org.kecak.soap.service;

import org.joget.apps.app.dao.*;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.service.FormService;
import org.joget.apps.userview.service.UserviewService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Service("soapProcessService")
public class SoapProcessServiceImpl implements SoapProcessService {
	@Autowired
	FormService formService;
	@Autowired
	WorkflowManager workflowManager;
	@Autowired
	AppDefinitionDao appDefinitionDao;
	@Autowired
	DatalistDefinitionDao datalistDefinitionDao;
	@Autowired
	UserviewDefinitionDao userviewDefinitionDao;
	@Autowired
	MessageDao messageDao;
	@Autowired
	EnvironmentVariableDao environmentVariableDao;
	@Autowired
	PluginDefaultPropertiesDao pluginDefaultPropertiesDao;
	@Autowired
	PackageDefinitionDao packageDefinitionDao;
	@Autowired
	PluginManager pluginManager;
	@Autowired
	FormDataDao formDataDao;
	@Autowired
	UserviewService userviewService;
	@Autowired
	AppService appService;

	@Override
	public String processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nullable Map<String, String> workflowVariable) {
		String processDefId = appId + "#" + (appVersion == 0 ? appDefinitionDao.getPublishedVersion(appId) : appVersion) + "#" + processId;
		WorkflowProcessResult result = workflowManager.processStart(processDefId);
		return result.getProcess().getId();
	}
	
}
