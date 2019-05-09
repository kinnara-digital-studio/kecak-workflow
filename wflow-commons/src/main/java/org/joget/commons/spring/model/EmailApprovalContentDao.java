package org.joget.commons.spring.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class EmailApprovalContentDao extends AbstractSpringDao {

	public static final String ENTITY_NAME = "EmailApprovalContent";
	
	public void saveOrUpdate(EmailApprovalContent eaContent) {
		super.saveOrUpdate(ENTITY_NAME, eaContent);
	}

	public void delete(EmailApprovalContent eaContent) {
		super.delete(ENTITY_NAME, eaContent);
	}

	public EmailApprovalContent getEmailApprovalContentById(String id) {
		return (EmailApprovalContent) super.find(ENTITY_NAME, id);
	}

	@SuppressWarnings("unchecked")
	public EmailApprovalContent getEmailApprovalContent(String processId) {

		Collection<EmailApprovalContent> results = new ArrayList<EmailApprovalContent>();
		results = super.find(ENTITY_NAME, "WHERE e.processId = ?", new String[] { processId }, null, null, null, null);
		
		EmailApprovalContent result = null;
		if (results != null && results.size() != 0) {
			result = results.iterator().next();
		}
		return result;

	}
	
	@SuppressWarnings("unchecked")
	public EmailApprovalContent getEmailApprovalContent(String processId, String activityId) {

		Collection<EmailApprovalContent> results = new ArrayList<EmailApprovalContent>();
		results = super.find(ENTITY_NAME, "WHERE e.processId = ? AND e.activityId = ?", new String[] { processId, activityId }, null, null, null, null);
		
		EmailApprovalContent result = null;
		if (results != null && results.size() != 0) {
			result = results.iterator().next();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<EmailApprovalContent> getEmailApprovalContents(String condition, String[] param, String sort, Boolean desc, Integer start, Integer rows) {
        if (condition != null && condition.trim().length() != 0) {
            return (List<EmailApprovalContent>) super.find(ENTITY_NAME, condition, param, sort, desc, start, rows);
        } else {
            return (List<EmailApprovalContent>) super.find(ENTITY_NAME, "", new Object[]{}, sort, desc, start, rows);
        }
    }

    @SuppressWarnings("unchecked")
	public List<EmailApprovalContent> getEmailApprovalContents(String locale, String sort, Boolean desc, Integer start, Integer rows) {
        if (locale != null && locale.trim().length() != 0) {
            return (List<EmailApprovalContent>) super.find(ENTITY_NAME, "WHERE e.locale = ?", new String[]{locale}, sort, desc, start, rows);
        } else {
            return (List<EmailApprovalContent>) super.find(ENTITY_NAME, "", new Object[]{}, sort, desc, start, rows);
        }
    }

    public Long count(String condition, Object[] params) {
        return super.count(ENTITY_NAME, condition, params);
    }

   
}
