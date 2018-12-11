package org.kecak.soap.service;

import java.util.Map;

import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;

public class KecakSoapServiceImpl implements KecakSoapService{

	@Override
	public String processStart(String appId, String processDefId, Map<String, String> workflowVariable) {
		// TODO Auto-generated method stub
		System.out.println("Application Id: " + appId + ", ProcessId: " + processDefId);
		WorkflowManager wfManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
		WorkflowProcessResult result = wfManager.processStart(processDefId);
		
		return result.getProcess().getId();
	}
	
}
