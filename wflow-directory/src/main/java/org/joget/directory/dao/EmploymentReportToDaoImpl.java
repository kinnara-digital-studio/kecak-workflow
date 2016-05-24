package org.joget.directory.dao;

import java.util.Date;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.EmploymentReportTo;
import org.joget.workflow.model.service.WorkflowUserManager;

public class EmploymentReportToDaoImpl extends AbstractSpringDao implements EmploymentReportToDao {

	private WorkflowUserManager workflowUserManager;

    public Boolean addEmploymentReportTo(EmploymentReportTo employmentReportTo) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
			Date currentDate = new Date();

			employmentReportTo.setCreatedBy(currentUsername);
			employmentReportTo.setModifiedBy(currentUsername);
			employmentReportTo.setDateCreated(currentDate);
			employmentReportTo.setDateModified(currentDate);
			
            save("EmploymentReportTo", employmentReportTo);
            return true;
        } catch (Exception e) {
            LogUtil.error(EmploymentReportToDaoImpl.class.getName(), e, "Add Employment Report To Error!");
            return false;
        }
    }

    public Boolean updateEmploymentReportTo(EmploymentReportTo employmentReportTo) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
        	employmentReportTo.setModifiedBy(currentUsername);
			employmentReportTo.setDateModified(new Date());
            merge("EmploymentReportTo", employmentReportTo);
            return true;
        } catch (Exception e) {
            LogUtil.error(EmploymentReportToDaoImpl.class.getName(), e, "Update Employment Report To Error!");
            return false;
        }
    }

    public Boolean deleteEmploymentReportTo(String id) {
        try {
            EmploymentReportTo employmentReportTo = getEmploymentReportTo(id);

            if (employmentReportTo != null) {
                employmentReportTo.setReportTo(null);
                employmentReportTo.setSubordinate(null);

                delete("EmploymentReportTo", employmentReportTo);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LogUtil.error(EmploymentReportToDaoImpl.class.getName(), e, "Delete Employment Report To Error!");
            return false;
        }
    }

    public EmploymentReportTo getEmploymentReportTo(String id) {
        try {
            return (EmploymentReportTo) find("EmploymentReportTo", id);
        } catch (Exception e) {
            LogUtil.error(EmploymentReportToDaoImpl.class.getName(), e, "Get Employment Report To Error!");
            return null;
        }
    }

	public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}
}
