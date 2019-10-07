package org.joget.directory.dao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.ClientApp;
import org.joget.directory.model.User;
import org.joget.workflow.model.service.WorkflowUserManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ClientAppDaoImpl extends AbstractSpringDao implements ClientAppDao {

    public final static String ENTITY_NAME = "ClientApp";

    private WorkflowUserManager workflowUserManager;


    public WorkflowUserManager getWorkflowUserManager() {
        return workflowUserManager;
    }

    public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
        this.workflowUserManager = workflowUserManager;
    }

    public Boolean addClientApp(ClientApp ClientApp) {
        try {
            String currentUsername = workflowUserManager.getCurrentUsername();
            Date currentDate = new Date();

            ClientApp.setCreatedBy(currentUsername);
            ClientApp.setModifiedBy(currentUsername);
            ClientApp.setDateCreated(currentDate);
            ClientApp.setDateModified(currentDate);
            save(ENTITY_NAME, ClientApp);
            return true;
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Add ClientApp Error!");
            return false;
        }
    }

    public Boolean updateClientApp(ClientApp ClientApp) {
        try {
            String currentUsername = workflowUserManager.getCurrentUsername();

            ClientApp.setModifiedBy(currentUsername);
            ClientApp.setDateModified(new Date());
            merge(ENTITY_NAME, ClientApp);
            return true;
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Update ClientApp Error!");
            return false;
        }
    }

    public Boolean deleteClientApp(String id) {
        Boolean result = false;
        try {
            ClientApp ClientApp = getClientApp(id);
            if (ClientApp != null) {
                delete(ENTITY_NAME, ClientApp);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Update ClientApp Error!");
        }
        return result;
    }

    public ClientApp getClientApp(String id) {
        ClientApp result = null;
        try {
            result = (ClientApp) find(ENTITY_NAME, id);
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Get ClientApp By Id Error!");
        }
        return result;
    }

    public ClientApp getClientApp(ClientApp clientApp) {
        ClientApp result = null;
        try {
            List ClientApps = findByExample(ENTITY_NAME, clientApp);
            if(ClientApps.size() > 0) {
                result = (ClientApp) ClientApps.get(0);
            }
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Get ClientApp By Id Error!");
        }
        return result;
    }

    public Boolean deleteClientApp(ClientApp ClientApp) {
        Boolean result = false;
        try {
            List ClientApps = findByExample(ENTITY_NAME, ClientApp);
            if(ClientApps.size() > 0) {
                ClientApp uToken = (ClientApp) ClientApps.get(0);
                delete(ENTITY_NAME,uToken);
                result = true;
            }
        } catch (Exception e) {
            LogUtil.error(ClientAppDaoImpl.class.getName(), e, "Delete ClientApp Error!");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Collection<ClientApp> getClientAppList(String filterString, String active, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            @SuppressWarnings("rawtypes")
            Collection param = new ArrayList();
            String condition = "where (e.userId like ? or e.appName like ?)";
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");

            if (active != null) {
                condition += " and e.active = ?";
                param.add(("1".equals(active) ? 1 : 0));
            }

            return find(ENTITY_NAME, condition, param.toArray(), sort, desc, start, rows);
        } catch (Exception e) {
            LogUtil.error(UserDaoImpl.class.getName(), e, "Get Client App List Error!");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public Long getTotalClientApp(String filterString, String active) {
        try {
            if (filterString == null) {
                filterString = "";
            }
            @SuppressWarnings("rawtypes")
            Collection param = new ArrayList();
            String condition = "where (e.userId like ? or e.appName like ?)";
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");

            if (active != null) {
                condition += " and e.active = ?";
                param.add(("1".equals(active) ? 1 : 0));
            }

            return count(ENTITY_NAME, condition, param.toArray());
        } catch (Exception e) {
            LogUtil.error(UserDaoImpl.class.getName(), e, "Count Client App Error!");
        }

        return 0L;
    }


}
