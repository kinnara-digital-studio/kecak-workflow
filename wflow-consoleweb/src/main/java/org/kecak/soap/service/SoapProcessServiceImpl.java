package org.kecak.soap.service;

import java.util.Map;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service("soapProcessService")
public class SoapProcessServiceImpl implements SoapProcessService {

	@Override
	public String processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nullable Map<String, String> workflowVariable) {
		LogUtil.info(getClass().getName(), "Executing SOAP appId [" + appId + "] appVersion [" + appVersion + "] processId [" + processId + "]");

		// TODO Auto-generated method stub
		WorkflowManager wfManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
		AppDefinitionDao appDefinitionDao = (AppDefinitionDao) AppUtil.getApplicationContext().getBean("appDefinitionDao");
		String processDefId = appId + "#" + (appVersion == 0 ? appDefinitionDao.getPublishedVersion(appId) : appVersion) + "#" + processId;
		WorkflowProcessResult result = wfManager.processStart(processDefId);

		return result.getProcess().getId();
	}
	
}
