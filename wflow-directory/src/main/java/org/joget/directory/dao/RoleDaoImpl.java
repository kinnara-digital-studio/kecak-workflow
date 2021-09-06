package org.joget.directory.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.Role;
import org.joget.workflow.model.service.WorkflowUserManager;

public class RoleDaoImpl extends AbstractSpringDao implements RoleDao {

	private WorkflowUserManager workflowUserManager;
	
	
    public WorkflowUserManager getWorkflowUserManager() {
		return workflowUserManager;
	}

	public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
		this.workflowUserManager = workflowUserManager;
	}

	public Boolean addRole(Role role) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();
        	Date currentDate = new Date();
        	
        	role.setCreatedBy(currentUsername);
        	role.setModifiedBy(currentUsername);
        	role.setDateCreated(currentDate);
        	role.setDateModified(currentDate);
            save("Role", role);
            return true;
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Add Role Error!");
            return false;
        }
    }

    public Boolean updateRole(Role role) {
        try {
        	String currentUsername = workflowUserManager.getCurrentUsername();

        	role.setModifiedBy(currentUsername);
        	role.setDateModified(new Date());
            merge("Role", role);
            return true;
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Update Role Error!");
            return false;
        }
    }

    public Boolean deleteRole(String id) {
        try {
            Role role = getRole(id);
            delete("Role", role);
            return true;
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Delete Department Error!");
            return false;
        }
    }

    public Role getRole(String id) {
        try {
            return (Role) find("Role", id);
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get Role Error!");
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
	public Role getRoleByName(String name) {
        try {
            Role role = new Role();
            role.setName(name);
            List roles = findByExample("Role", role);

            if (roles.size() > 0) {
                return (Role) roles.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get Role By Name Error!");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
	public Collection<Role> getRoles(String filterString, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            return find("Role", "where e.name like ? or e.description like ?", new Object[]{"%" + filterString + "%", "%" + filterString + "%"}, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get Roles Error!");
        }

        return null;
    }

    public Long getTotalRoles(String filterString) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            return count("Role", "where e.name like ? or e.description like ?", new Object[]{"%" + filterString + "%", "%" + filterString + "%"});
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get Total Roles Error!");
        }

        return 0L;
    }

    @SuppressWarnings("unchecked")
	public Collection<Role> getUserRoles(String username, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            return find("Role", "INNER JOIN e.users u WHERE u.username=?", new Object[]{username}, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get User Roles Error!");
        }

        return null;
    }

    public Long getTotalUserRoles(String username) {
        try {
            return count("Role", "INNER JOIN e.users u WHERE u.username=?", new Object[]{username});
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Get Total Roles Error!");
        }

        return 0L;
    }

    @SuppressWarnings("unchecked")
	public Collection<Role> findRoles(String condition, Object[] params, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            return find("Role", condition, params, sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Find Roles Error!");
        }

        return null;
    }

    public Long countRoles(String condition, Object[] params) {
        try {
            return count("Role", condition, params);
        } catch (Exception e) {
            LogUtil.error(RoleDaoImpl.class.getName(), e, "Count Roles Error!");
        }

        return 0L;
    }
}
