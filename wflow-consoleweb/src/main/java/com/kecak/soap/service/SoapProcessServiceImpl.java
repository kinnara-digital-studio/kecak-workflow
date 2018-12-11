package com.kecak.soap.service;

import java.util.Map;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
public class SoapProcessesServiceImpl implements SoapProcessesService {

	@Override
	public String processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processDefId, @Nullable Map<String, String> workflowVariable) {
		LogUtil.info(getClass().getName(), "Executing SOAP appId ["+appId+"] appVersion ["+appVersion+"] processDefId ["+processDefId+"]");

		// TODO Auto-generated method stub
		System.out.println("Application Id: " + appId + ", ProcessId: " + processDefId);
		WorkflowManager wfManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
		WorkflowProcessResult result = wfManager.processStart(processDefId);
		
		return result.getProcess().getId();
	}
	
}
