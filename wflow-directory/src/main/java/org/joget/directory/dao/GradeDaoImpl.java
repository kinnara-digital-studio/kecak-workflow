package org.joget.directory.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Grade;
import org.joget.workflow.model.service.WorkflowUserManager;

public class GradeDaoImpl extends AbstractSpringDao implements GradeDao {

    private OrganizationDao organizationDao;
    private EmploymentDao employmentDao;
    private WorkflowUserManager workflowUserManager;

    public EmploymentDao getEmploymentDao() {
        return employmentDao;
    }

    public void setEmploymentDao(EmploymentDao employmentDao) {
        this.employmentDao = employmentDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}

	public Boolean addGrade(Grade grade) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
        	Date currentDate = new Date();
        	
        	grade.setCreatedBy(currentUsername);
        	grade.setModifiedBy(currentUsername);
        	grade.setDateCreated(currentDate);
        	grade.setDateModified(currentDate);
            save("Grade", grade);
            return true;
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Add Grade Error!");
            return false;
        }
    }

    public Boolean updateGrade(Grade grade) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
        	grade.setModifiedBy(currentUsername);
        	grade.setDateModified(new Date());
            merge("Grade", grade);
            return true;
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Update Grade Error!");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
	public Boolean deleteGrade(String id) {
        try {
            Grade grade = getGrade(id);

            if (grade != null && grade.getEmployments() != null && grade.getEmployments().size() > 0) {
                for (Employment e : (Set<Employment>) grade.getEmployments()) {
                    employmentDao.unassignUserFromGrade(e.getUserId(), id);
                }
            }
            delete("Grade", grade);
            return true;
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Delete Grade Error!");
            return false;
        }
    }

    public Grade getGrade(String id) {
        try {
            return (Grade) find("Grade", id);
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Get Grade Error!");
            return null;
        }
    }

    public Grade getGradeByName(String name) {
        try {
            Grade grade = new Grade();
            grade.setName(name);
            @SuppressWarnings("rawtypes")
			List grades = findByExample("Grade", grade);

            if (grades.size() > 0) {
                return (Grade) grades.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Get Grade By Name Error!");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	public Collection<Grade> getGradesByOrganizationId(String filterString, String organizationId, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            @SuppressWarnings("rawtypes")
			Collection param = new ArrayList();
            String condition = "where (e.id like ? or e.name like ? or e.description like ?)";
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");

            if (organizationId != null) {
                condition += " and e.organization.id = ?";
                param.add(organizationId);
            }
            return find("Grade", condition, param.toArray(), sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Get Grades By Organization Id Error!");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	public Long getTotalGradesByOrganizationId(String filterString, String organizationId) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            @SuppressWarnings("rawtypes")
			Collection param = new ArrayList();
            String condition = "where (e.id like ? or e.name like ? or e.description like ?)";
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");

            if (organizationId != null) {
                condition += " and e.organization.id = ?";
                param.add(organizationId);
            }
            return count("Grade", condition, param.toArray());
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Get Total Grades By Organization Id Error!");
        }

        return 0L;
    }

    @SuppressWarnings("unchecked")
	public Collection<Grade> findGrades(String condition, Object[] params, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            return find("Grade", condition, params, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Find Grades Error!");
        }

        return null;
    }

    public Long countGrades(String condition, Object[] params) {
        try {
            return count("Grade", condition, params);
        } catch (Exception e) {
            LogUtil.error(GradeDaoImpl.class.getName(), e, "Count Grades Error!");
        }

        return 0L;
    }
}
