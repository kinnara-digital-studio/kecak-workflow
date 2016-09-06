package org.joget.apps.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormData;
import org.joget.commons.spring.model.EmailApprovalContent;
import org.joget.commons.spring.model.EmailApprovalContentDao;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailApprovalProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailApprovalProcessor.class);
    
	public static final String MAIL_SUBJECT_PATTERN = "{unuse}processId:{processId}";
	public static final String FROM = "from";
	
	private EmailApprovalContentDao emailApprovalContentDao;
	private WorkflowManager workflowManager;
	private AppService appService;
	private WorkflowUserManager workflowUserManager;
	private DirectoryManager directoryManager;
	
	public void parseEmail(@Body String body, Exchange exchange) {
		//GET EMAIL ADDRESS
		String from = (String) exchange.getIn().getHeader(FROM);
		LOGGER.info("[FROM] : "+from);
	
		int startIndex = from.indexOf("<");
		int length = from.length();
		if (startIndex > -1) {
			from = from.substring(startIndex+1, length-1);
		}
		String username = getUsername(from);
		workflowUserManager.setCurrentThreadUser(username);
				
		String subject = (String) exchange.getIn().getHeader("subject");
		subject = subject.replace("\t","__").replace("\n", "__").replace(" ", "__");

		Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
        Matcher matcher = pattern.matcher(MAIL_SUBJECT_PATTERN);
        
		String subjectRegex = createRegex(MAIL_SUBJECT_PATTERN);
        Pattern pattern2 = Pattern.compile("^" + subjectRegex + "$");
        Matcher matcher2 = pattern2.matcher(subject);

        String processId = null;
        while (matcher2.find()) {
            int count = 1;
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher2.group(count);
                if ("processId".equals(key)) {
                	processId = value;
                } 
                count++;
            }
        }
		LOGGER.info("Process ID : "+processId);
		
		if(processId != null) {
			parseEmailContent(processId, username, body);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseEmailContent(String processId, String username, String emailContent) {
		String content = emailContent.replaceAll("\\r?\\n", "__");
		
		String processDefId = null;
		String activityDefId = null;
		String activityId = null;
		
		workflowUserManager.setCurrentThreadUser(username);
		LOGGER.info("[USERNAME] : "+workflowUserManager.getCurrentUsername());
		
        WorkflowAssignment workflowAssignment = workflowManager.getAssignmentByProcess(processId);
        if (workflowAssignment != null) {
        	processDefId = workflowAssignment.getProcessDefId();
        	activityDefId = workflowAssignment.getActivityDefId();
        	activityId = workflowAssignment.getActivityId();
        	LOGGER.info("processDefId :["+processDefId+"]");
        	LOGGER.info("activityDefId :["+activityDefId+"]");
        	LOGGER.info("activityId :["+activityId+"]");
        } else {
        	LOGGER.error("WorkflowAssignment is NULL");
        }
        	
        EmailApprovalContent emailApprovalContent = null;
        if (processDefId != null && activityDefId != null) {
    		emailApprovalContent = emailApprovalContentDao.getEmailApprovalContent(processDefId, activityDefId);
        }
		
		if (emailApprovalContent != null) {
			if (emailApprovalContent.getContent() != null) {
				String emailContentPattern = emailApprovalContent.getContent();
				emailContentPattern = emailContentPattern.replaceAll("\\r?\\n", "");
				
				String patternRegex = createRegex(emailContentPattern);
				
				Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
		        Matcher matcher = pattern.matcher(emailContentPattern);
		
		        Pattern pattern2 = Pattern.compile("^" + patternRegex + "$");
		        Matcher matcher2 = pattern2.matcher(content);
		        
		        @SuppressWarnings("rawtypes")
				Map variables = new HashMap();
		        FormData formData = new FormData();
		        
		        while (matcher2.find()) {
		            int count = 1;
		            while (matcher.find()) {
		                String key = matcher.group(1);
		                String value = matcher2.group(count);
		                if (key.startsWith("var_")) {
		                    key = key.replaceAll("var_", "");
			                LOGGER.info("variable_key:["+key+"] value:["+value+"]");
		                    variables.put(key, value.trim());
		                } else if (key.startsWith("form_")) {
		                    key = key.replaceAll("form_", "");
		                    formData.addRequestParameterValues(key, new String[] {value});
			                LOGGER.info("form_key:["+key+"] value:["+value+"]");
		                }
		                count++;
		            }
		        }
		        completeActivity(username, processId, activityId, formData, variables, content);
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void completeActivity(String username, String processId, String activityId, FormData formData, @SuppressWarnings("rawtypes") Map variables, String message) {
	    LOGGER.info("USERNAME : ["+username+"]");
	    
	    if (username != null) {	        
	        String currentUsername = workflowUserManager.getCurrentUsername();
	        try {
	            // set current user
	            workflowUserManager.setCurrentThreadUser(username);
	            // find assignment
	            WorkflowAssignment assignment = null;
	            if (activityId != null) {
	                assignment = workflowManager.getAssignment(activityId);
	            }
	            if (processId != null) {
	                assignment = workflowManager.getAssignmentByProcess(processId);
	            }
	            
	            if (assignment != null) {
	                AppDefinition currentAppDef = AppUtil.getCurrentAppDefinition();
	                try {
	                    String assignmentId = assignment.getActivityId();
	                    AppDefinition appDef = appService.getAppDefinitionForWorkflowActivity(assignmentId);
	                    
	                    activityId = assignment.getActivityId();
	                    processId = assignment.getProcessId();
	                    //if has form data to submit
	                    if (!formData.getRequestParams().isEmpty()) {
	                        PackageActivityForm activityForm = appService.viewAssignmentForm(appDef, assignment, formData, "", "");
	                        if (activityForm != null && activityForm.getForm() != null) {
	                            LOGGER.info("Submit Form for assignment: " + assignmentId + " " + formData.getRequestParams());
	                            appService.submitForm(activityForm.getForm(), formData, false);
	                        }
	                    }
	                    LOGGER.info("assignmentComplete: " + assignmentId + " " + variables);
	                    if (!assignment.isAccepted()) {
	                        workflowManager.assignmentAccept(assignmentId);
	                    }
	                    workflowManager.assignmentComplete(assignmentId, variables);
//	                    sendAutoReply(sender, subject);
	                } finally {
	                    AppUtil.setCurrentAppDefinition(currentAppDef);
	                }
//	                addActivityLog(sender, processId, activityId, subject, message, variables, formData.getRequestParams());
	            } else {
	            	LOGGER.info("assignment not found for process(" + processId + ") or activityId(" + activityId + ")");
	            }

	        } finally {
	            workflowUserManager.setCurrentThreadUser(currentUsername);
	        }
	    } else {
	    	LOGGER.info("No user found for sender : " + username);
	    }
	}
	
	public String getUsername(String sender) {
	    // get sender
	    User user = null;
	    InternetAddress ia = null;
		try {
			ia = new InternetAddress(sender);
		} catch (AddressException e) {
			LOGGER.error(e.getMessage());
		}
		if (ia != null) {
		    String email = ia.getAddress();
		    DirectoryManager directoryManager = (DirectoryManager)AppUtil.getApplicationContext().getBean("directoryManager");
		    @SuppressWarnings("rawtypes")
			Collection users = directoryManager.getUserList(email, null, null, 0, 1);
		    if (!users.isEmpty()) {
		        user = (User)users.iterator().next();
		        return user.getUsername();
		    }
		}
	    return null;
	}
	
	public String createRegex(String raw) {
		String result = escapeString(raw, null);
		result = result.replaceAll("\\\\\\{unuse\\\\\\}", "__([\\\\s\\\\S]*)").replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
		if(result.startsWith("__")) {
			result = result.substring(2);
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String escapeString(String inStr, Map<String, String> replaceMap) {
        if (replaceMap != null) {
            Iterator it = replaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                inStr = inStr.replaceAll(pairs.getKey(), escapeRegex(pairs.getValue()));
            }
        }
        
        return escapeRegex(inStr);
    }
	
	public String escapeRegex(String inStr) {
        return (inStr != null) ?  inStr.replaceAll("([\\\\*+\\[\\](){}\\$.?\\^|])", "\\\\$1") : null;
    }

	public EmailApprovalContentDao getEmailApprovalContentDao() {
		return emailApprovalContentDao;
	}

	public void setEmailApprovalContentDao(EmailApprovalContentDao emailApprovalContentDao) {
		this.emailApprovalContentDao = emailApprovalContentDao;
	}

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public void setWorkflowManager(WorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
	}

	public AppService getAppService() {
		return appService;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}

	public DirectoryManager getDirectoryManager() {
		return directoryManager;
	}

	public void setDirectoryManager(DirectoryManager directoryManager) {
		this.directoryManager = directoryManager;
	}
	
}
