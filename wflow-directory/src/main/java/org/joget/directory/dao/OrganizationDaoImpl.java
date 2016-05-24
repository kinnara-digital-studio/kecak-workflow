package org.joget.directory.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.Department;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Grade;
import org.joget.directory.model.Group;
import org.joget.directory.model.Organization;
import org.joget.workflow.model.service.WorkflowUserManager;

public class OrganizationDaoImpl extends AbstractSpringDao implements OrganizationDao {

    private GradeDao gradeDao;
    private GroupDao groupDao;
    private DepartmentDao departmentDao;
    private EmploymentDao employmentDao;
    private WorkflowUserManager workflowUserManager;

    public EmploymentDao getEmploymentDao() {
        return employmentDao;
    }

    public void setEmploymentDao(EmploymentDao employmentDao) {
        this.employmentDao = employmentDao;
    }

    public DepartmentDao getDepartmentDao() {
        return departmentDao;
    }

    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public GradeDao getGradeDao() {
        return gradeDao;
    }

    public void setGradeDao(GradeDao gradeDao) {
        this.gradeDao = gradeDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}

	public Boolean addOrganization(Organization organization) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
        	Date currentDate = new Date();
        	
        	organization.setCreatedBy(currentUsername);
        	organization.setModifiedBy(currentUsername);
        	organization.setDateCreated(currentDate);
        	organization.setDateModified(currentDate);
            save("Organization", organization);
            return true;
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Add Organization Error!");
            return false;
        }
    }

    public Boolean updateOrganization(Organization organization) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();

        	organization.setModifiedBy(currentUsername);
        	organization.setDateModified(new Date());
            merge("Organization", organization);
            return true;
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Update Organization Error!");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
	public Boolean deleteOrganization(String id) {
        try {
            Organization organization = getOrganization(id);
            if (organization != null) {
				Set<Department> departments = organization.getDepartments();
                Set<Grade> grades = organization.getGrades();
                Set<Group> groups = organization.getGroups();
                Set<Employment> employments = organization.getEmployments();
                
                if (employments != null) {
                    for (Employment employment : employments) {
                        getEmploymentDao().unassignUserFromOrganization(employment.getUserId(), id);
                    }
                    employments.clear();
                }
                
                if (groups != null) {
                    organization.getGroups().removeAll(groups);
                }

                if (departments != null) {
                    for (Department department : departments) {
                        getDepartmentDao().deleteDepartment(department.getId());
                    }
                }

                if (grades != null) {
                    for (Grade grade : grades) {
                        getGradeDao().deleteGrade(grade.getId());
                    }
                }
                
                delete("Organization", organization);
            }
            return true;
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Delete Organization Error!");
            return false;
        }
    }

    public Organization getOrganization(String id) {
        try {
            return (Organization) find("Organization", id);
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Get Organization Error!");
            return null;
        }
    }

    public Organization getOrganizationByName(String name) {
        try {
            Organization organization = new Organization();
            organization.setName(name);
            @SuppressWarnings("rawtypes")
			List organizations = findByExample("Organization", organization);

            if (organizations.size() > 0) {
                return (Organization) organizations.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Get Organization By Name Error!");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	public Collection<Organization> getOrganizationsByFilter(String filterString, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            return find("Organization", "where e.id like ? or e.name like ? or e.description like ?", new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"}, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Get Organizations By Name Filter Error!");
        }

        return null;
    }

    public Long getTotalOrganizationsByFilter(String filterString) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            return count("Organization", "where e.id like ? or e.name like ? or e.description like ?", new Object[]{"%" + filterString + "%", "%" + filterString + "%", "%" + filterString + "%"});
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Get Total Organizations By Name Filter Error!");
        }

        return 0L;
    }

    @SuppressWarnings("unchecked")
	public Collection<Organization> findOrganizations(String condition, Object[] params, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            return find("Organization", condition, params, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Find Organizations Error!");
        }

        return null;
    }

    public Long countOrganizations(String condition, Object[] params) {
        try {
            return count("Organization", condition, params);
        } catch (Exception e) {
            LogUtil.error(OrganizationDaoImpl.class.getName(), e, "Count Organizations Error!");
        }

        return 0L;
    }
}
