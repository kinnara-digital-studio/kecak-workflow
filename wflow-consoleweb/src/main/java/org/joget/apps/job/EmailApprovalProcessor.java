package org.joget.apps.job;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.model.FormData;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.plugin.base.ApplicationPlugin;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.apache.camel.Body;

public class EmailApprovalProcessor {

	public void pollEmail() {

		Properties properties = new Properties();

		// must be call table properties
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.imaps.host", "imap.gmail.com");
		properties.put("mail.imaps.port", "993");
		properties.put("mail.imaps.timeout", (10 * 1000));

		Session session = Session.getInstance(properties);
		IMAPStore store = null;
		Folder inbox = null;

		int messageCount = 0;
		String errors = "";
		try {
			store = (IMAPStore) session.getStore("imaps");
			store.connect("ardi.priasa@gmail.com", "313354nengtri");

			inbox = (IMAPFolder) store.getFolder("INBOX");
			if (null == inbox) {
				throw new Exception(" no  folder [#form.j_ave_listener.folder?java#] for user [#form.j_ave_listener.email?java#] on host [#form.j_ave_listener.host?java#]");
			}

			// opening found folder in read/write
			inbox.open(Folder.READ_WRITE);

			messageCount = inbox.getUnreadMessageCount();
			System.out.println("Unread Messages: " + messageCount);
			

			int count = 0;

			Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			for (Message message : messages) {
				try {
					if (count > 10) {
						System.out.println("Email checking reach max email per check(#form.j_ave_listener.max_email?java#).");
						break;
					}

					// get email details
					String subject = message.getSubject();
					if (!matchFilter(subject)) {
						continue;
					}

					String sender = null;
					Address[] in = message.getFrom();
					for (Address address : in) {
						sender = address.toString();
					}

					String content = "";
					if (message.getContent() instanceof Multipart) {
						Multipart multipart = (Multipart) message.getContent();
						for (int j = 0; j < multipart.getCount(); j++) {
							BodyPart bodyPart = multipart.getBodyPart(j);
							String disposition = bodyPart.getDisposition();
							if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { 
								System.out.println("Attachment found");
							} else {
								content = bodyPart.getContent().toString();
								break;
							}
						}
					} else {
						content = message.getContent().toString();
					}
					System.out.println("--- Mail " + count + ": " + subject + " --- ");
					System.out.println("Sender: " + sender);
					System.out.println("Mail Content: " + content);

					// reset read flag
					message.setFlag(Flags.Flag.SEEN, true);

					// check for approval message
					parseEmail(sender, subject, content);

					count++;
				} catch (MessagingException e) {
					errors += e.getMessage() + "\n";
					LogUtil.error("App: eas - Poll Email tool", e, e.getMessage());
				}
			}

		} catch (Exception e) {
			errors += e.getMessage() + "\n";
			LogUtil.error("App: eas - Poll Email tool", e, "");
		} finally {
			try {
				if (inbox != null && inbox.isOpen()) {
					inbox.close(false);
				}
			} catch (Exception e) {
			}

			try {
				if (store != null && store.isConnected()) {
					store.close();
				}
			} catch (Exception e) {
			}
		}
		if (!errors.isEmpty()) {
//			addError(messageCount, errors);
		}
//		storeFormData("j_ave_log", "log", activitiesLog);
	}

	public boolean matchFilter(String subject) {
	    String filter = "[Kecak Email Approval]";
	    if (subject.contains(filter)) {
	    	return true; 
	    } 
	    
	    return false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parseEmail(String sender, String subject, String content) {
	    String processId = null;
	    String activityId = null;
	    Map variables = new HashMap();
	    FormData formData = new FormData();

	    String subject_pattern = "{unuse}ID: {processId}{unuse}Activity: {activityId}{unuse}";
	    System.out.println("subject_pattern: " + subject_pattern);
	    
	    String subject_reg = StringUtil.escapeString(subject_pattern, StringUtil.TYPE_REGEX, null);
	    subject_reg = subject_reg.replaceAll("\\\\\\{unuse\\\\\\}", "([\\\\s\\\\S]*)");
	    subject_reg = subject_reg.replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
	    System.out.println("subject_reg: " + subject_reg);
	    
	    String content_pattern = "{form_approval_new_application_approval_action_status}{unuse}{unuse}ID: {processId}{unuse}Remarks: [{form_approval_new_application_approval_action_remarks}]{unuse}";
	    System.out.println("content_pattern: " + content_pattern);
	    String content_reg = StringUtil.escapeString(content_pattern, StringUtil.TYPE_REGEX, null);
	    content_reg = content_reg.replaceAll("\\\\\\{unuse\\\\\\}", "([\\\\s\\\\S]*)");
	    content_reg = content_reg.replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
	    System.out.println("content_reg: " + content_reg);

	    if (!subject_pattern.equals(subject_reg)) {
	        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
	        Matcher matcher = pattern.matcher(subject_pattern);

	        Pattern pattern2 = Pattern.compile("^" + subject_reg + "$");
	        Matcher matcher2 = pattern2.matcher(subject);

	        while (matcher2.find()) {
	            int count = 1;
	            while (matcher.find()) {
	                String key = matcher.group(1);
	                String value = matcher2.group(count);
	                if ("processId".equals(key)) {
	                    processId = value;
	                } else if ("activityId".equals(key)) {
	                    activityId = value;
	                } else if (key.startsWith("var_")) {
	                    key = key.replaceAll("var_", "");
	                    variables.put(key, value);
	                } else if (key.startsWith("form_")) {
	                    key = key.replaceAll("form_", "");
	                    formData.addRequestParameterValues(key, new String[] {value});
	                }

	                count++;
	            }
	        }
	    }

	    if (!content_pattern.equals(content_reg)) {
	        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
	        Matcher matcher = pattern.matcher(content_pattern);

	        Pattern pattern2 = Pattern.compile("^" + content_reg + "$");
	        Matcher matcher2 = pattern2.matcher(content);

	        while (matcher2.find()) {
	            int count = 1;
	            while (matcher.find()) {
	                String key = matcher.group(1);
	                String value = matcher2.group(count);
	                if ("processId".equals(key)) {
	                    processId = value.trim();
	                } else if ("activityId".equals(key)) {
	                    activityId = value.trim();
	                } else if (key.startsWith("var_")) {
	                    key = key.replaceAll("var_", "");
	                    variables.put(key, value.trim());
	                } else if (key.startsWith("form_")) {
	                    key = key.replaceAll("form_", "");
	                    formData.addRequestParameterValues(key, new String[] {value});
	                    System.out.println("###formData#key#"+key);
	                    System.out.println("###formData#value#"+value);
	                }

	                count++;
	            }
	        }
	    }

	    System.out.println("###processId:"+processId);
	    System.out.println("###activityId:"+activityId);
	    if (processId != null || activityId != null) {
	        completeActivity(sender, processId, activityId, formData, variables, subject, content);
	    }
	}
	
	@SuppressWarnings("rawtypes")
	public void completeActivity(String sender, String processId, String activityId, FormData formData, Map variables, String subject, String message) {
	    String username = getUsername(sender);
	    System.out.println("###doCompleteActivity#username#"+username);
	    /**
	    if (username != null) {
	        AppService appService = (AppService)AppUtil.getApplicationContext().getBean("appService");
	        WorkflowManager workflowManager = (WorkflowManager)AppUtil.getApplicationContext().getBean("workflowManager");
	        WorkflowUserManager workflowUserManager = (WorkflowUserManager)AppUtil.getApplicationContext().getBean("workflowUserManager");
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
	                        	System.out.println("Submit Form for assignment: " + assignmentId + " " + formData.getRequestParams());
	                            appService.submitForm(activityForm.getForm(), formData, false);
	                        }
	                    }

	                    System.out.println("assignmentComplete: " + assignmentId + " " + variables);
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
	            	System.out.println("assignment not found for process(" + processId + ") or activityId(" + activityId + ")");
	            }

	        } finally {
	            workflowUserManager.setCurrentThreadUser(currentUsername);
	        }
	    } else {
	        System.out.println("No user found for sender : " + sender);
	    }
	    */
	}

	public String getUsername(String sender) {
//	    get sender
//	    User user = null;
//	    InternetAddress ia = new InternetAddress(sender);
//	    String email = ia.getAddress();
//	    DirectoryManager directoryManager = (DirectoryManager)AppUtil.getApplicationContext().getBean("directoryManager");
//	    Collection users = directoryManager.getUserList(email, null, null, 0, 1);
//	    if (!users.isEmpty()) {
//	        user = (User)users.iterator().next();
//	        return user.getUsername();
//	    }
	    return "admin";
	}
}
