package org.joget.apps.form.dao;

import net.sf.ehcache.Cache;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.DynamicDataSourceManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class FormDataDaoImpl extends HibernateDaoSupport implements FormDataDao {

    public static final String FORM_MAPPING_DIRECTORY = "app_forms";
    public static final String FORM_PREFIX_ENTITY = "FormRow_";
    public static final String FORM_PREFIX_TABLE_NAME = "app_fd_";
    public static final String FORM_ANY_ORG_ID = "*";
    public static final String FORM_PROPERTY_TABLE_NAME = "tableName";
    public static final String FORM_PREFIX_COLUMN = "c_";
    public static final int ACTION_TYPE_LOAD = 0;
    public static final int ACTION_TYPE_STORE = 1;

    private FormDefinitionDao formDefinitionDao;
    private FormService formService;
    private FormColumnCache formColumnCache;
    private Cache formSessionFactoryCache;
    private Cache formPersistentClassCache;
    private Document formRowDocument;

    public FormDefinitionDao getFormDefinitionDao() {
        return formDefinitionDao;
    }

    public void setFormDefinitionDao(FormDefinitionDao formDefinitionDao) {
        this.formDefinitionDao = formDefinitionDao;
    }

    public FormService getFormService() {
        return formService;
    }

    public void setFormService(FormService formService) {
        this.formService = formService;
    }

    public FormColumnCache getFormColumnCache() {
        return formColumnCache;
    }

    public void setFormColumnCache(FormColumnCache formColumnCache) {
        this.formColumnCache = formColumnCache;
    }

    public Cache getFormSessionFactoryCache() {
        return formSessionFactoryCache;
    }

    public void setFormSessionFactoryCache(Cache formSessionFactoryCache) {
        this.formSessionFactoryCache = formSessionFactoryCache;
    }

    public Cache getFormPersistentClassCache() {
        return formPersistentClassCache;
    }

    public void setFormPersistentClassCache(Cache formPersistentClassCache) {
        this.formPersistentClassCache = formPersistentClassCache;
    }

    /**
     * Loads a data row for a form based on the primary key
     *
     * @param form
     * @param primaryKey
     * @return null if the row does not exist
     */
    public FormRow load(Form form, String primaryKey) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        return internalLoad(entityName, tableName, primaryKey);
    }

    /**
     * Loads a data row for a form based on the primary key
     *
     * @param formDefId
     * @param tableName
     * @param primaryKey
     * @return null if the row does not exist
     */
    public FormRow load(String formDefId, String tableName, String primaryKey) {
        String entityName = getFormEntityName(formDefId);
        tableName = getFormTableName(formDefId, tableName);
        return internalLoad(entityName, tableName, primaryKey);
    }

    /**
     * Loads a data row for a form based on the primary key.
     * This method is transactional (since v5), but retains the method name for backward compatibility reasons.
     *
     * @param form
     * @param primaryKey
     * @return
     */
    public FormRow loadWithoutTransaction(Form form, String primaryKey) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        return internalLoad(entityName, tableName, primaryKey);
    }

    /**
     * Loads a data row for a form based on the primary key.
     * This method is transactional (since v5), but retains the method name for backward compatibility reasons.
     *
     * @param formDefId
     * @param tableName
     * @param primaryKey
     * @return
     */
    public FormRow loadWithoutTransaction(String formDefId, String tableName, String primaryKey) {
        String entityName = getFormEntityName(formDefId);
        tableName = getFormTableName(formDefId, tableName);
        return internalLoad(entityName, tableName, primaryKey);
    }

    /**
     * Loads a data row for an entity and table based on the primary key
     *
     * @param entityName
     * @param tableName
     * @param primaryKey
     * @return null if the row does not exist
     */
    protected FormRow internalLoad(String entityName, String tableName, String primaryKey) {
        return internalLoad(entityName, tableName, primaryKey, false);
    }

    /**
     * Loads a data row for an entity and table based on the primary key
     *
     * @param entityName
     * @param tableName
     * @param primaryKey
     * @param loadDeleted
     * @return null if the row does not exist
     */
    protected FormRow internalLoad(String entityName, String tableName, String primaryKey, final boolean loadDeleted) {
        // get hibernate session
        Session session = getHibernateSession(entityName, tableName, null, ACTION_TYPE_LOAD);

        // load by primary key
        FormRow row = null;
        try {
            String userOrgId = WorkflowUtil.getCurrentUserOrgId();
            row = Optional.ofNullable(primaryKey)
                    .map(s -> session.load(tableName, s))
                    .map(o -> (FormRow) o)
                    .filter(r -> (loadDeleted || !r.getDeleted())
                            && (r.getOrgId() == null || r.getOrgId().equals(FORM_ANY_ORG_ID) || r.getOrgId().equals(userOrgId)))
                    .orElse(null);
        } catch (ObjectRetrievalFailureException | ObjectNotFoundException ignore) {

        } finally {
            closeSession(session);
        }
        return row;
    }


    /**
     * Loads a data row for a table based on the primary key
     *
     * @param tableName
     * @param columnName is not used
     * @param primaryKey
     * @return null if the row does not exist
     */
    @Override
    public FormRow loadByTableNameAndColumnName(String tableName, String columnName, String primaryKey) {
        if (!tableName.startsWith(FORM_PREFIX_TABLE_NAME)) {
            tableName = FormDataDaoImpl.FORM_PREFIX_TABLE_NAME + tableName;
        }

        return internalLoad(tableName, tableName, primaryKey);
    }

    /**
     * Query to find a list of matching form rows.
     *
     * @param formDefId
     * @param tableName
     * @param condition
     * @param params
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    @Override
    public FormRowSet find(String formDefId, String tableName, final String condition, final Object[] params, final String sort, final Boolean desc, final Integer start, final Integer rows) {
        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);
        final String sortAs = FormUtil.PROPERTY_DATE_CREATED.equals(sort) || FormUtil.PROPERTY_DATE_MODIFIED.equals(sort) ? "timestamp" : "string";

        return internalFind(entityName, newTableName, condition, params, sort, sortAs, desc, start, rows, false);
    }

    /**
     *
     * @param formDefId
     * @param tableName
     * @param condition
     * @param params
     * @param sort
     * @param sortAs
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    @Override
    public FormRowSet find(String formDefId, String tableName, final String condition, final Object[] params, final String sort, final String sortAs, final Boolean desc, final Integer start, final Integer rows) {
        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);

        return internalFind(entityName, newTableName, condition, params, sort, sortAs, desc, start, rows, false);
    }

    /**
     * Query to find a list of matching form rows.
     *
     * @param formDefId
     * @param tableName
     * @param condition
     * @param params
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @param loadDeleted
     * @return
     */
    @Override
    public FormRowSet find(String formDefId, String tableName, final String condition, final Object[] params, final String sort, final Boolean desc, final Integer start, final Integer rows, final Boolean loadDeleted) {
        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);
        final String sortAs = FormUtil.PROPERTY_DATE_CREATED.equals(sort) || FormUtil.PROPERTY_DATE_MODIFIED.equals(sort) ? "timestamp" : "string";

        return internalFind(entityName, newTableName, condition, params, sort, sortAs, desc, start, rows, loadDeleted);
    }

    /**
     *
     * @param formDefId
     * @param tableName
     * @param condition
     * @param params
     * @param sort
     * @param sortAs
     * @param desc
     * @param start
     * @param rows
     * @param loadDeleted
     * @return
     */
    @Override
    public FormRowSet find(String formDefId, String tableName, final String condition, final Object[] params, final String sort, final String sortAs, final Boolean desc, final Integer start, final Integer rows, final Boolean loadDeleted) {
        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);

        return internalFind(entityName, newTableName, condition, params, sort, sortAs, desc, start, rows, loadDeleted);
    }

    /**
     * Query to find a list of matching form rows.
     *
     * @param form
     * @param condition
     * @param params
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    @Override
    public FormRowSet find(Form form, final String condition, final Object[] params, final String sort, final Boolean desc, final Integer start, final Integer rows) {
        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);
        final String sortAs = FormUtil.PROPERTY_DATE_CREATED.equals(sort) || FormUtil.PROPERTY_DATE_MODIFIED.equals(sort) ? "timestamp" : "string";

        return internalFind(entityName, tableName, condition, params, sort, sortAs, desc, start, rows, false);
    }

    /**
     *
     * @param form
     * @param condition
     * @param params
     * @param sort
     * @param sortAs
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    @Override
    public FormRowSet find(Form form, final String condition, final Object[] params, final String sort, final String sortAs, final Boolean desc, final Integer start, final Integer rows) {
        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);

        return internalFind(entityName, tableName, condition, params, sort, sortAs, desc, start, rows, false);
    }

    /**
     *
     * @param form
     * @param condition
     * @param params
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @param loadDeleted
     * @return
     */
    @Override
    public FormRowSet find(Form form, final String condition, final Object[] params, final String sort, final Boolean desc, final Integer start, final Integer rows, final Boolean loadDeleted) {
        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);
        final String sortAs = FormUtil.PROPERTY_DATE_CREATED.equals(sort) || FormUtil.PROPERTY_DATE_MODIFIED.equals(sort) ? "timestamp" : "string";

        return internalFind(entityName, tableName, condition, params, sort, sortAs, desc, start, rows, loadDeleted);
    }

    /**
     *
     * @param form
     * @param condition
     * @param params
     * @param sort
     * @param sortAs
     * @param desc
     * @param start
     * @param rows
     * @param loadDeleted
     * @return
     */
    @Override
    public FormRowSet find(Form form, final String condition, final Object[] params, final String sort, final String sortAs, final Boolean desc, final Integer start, final Integer rows, final Boolean loadDeleted) {
        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);

        return internalFind(entityName, tableName, condition, params, sort, sortAs, desc, start, rows, loadDeleted);
    }

    /**
     * Query to find a list of matching form rows.
     *
     * @param entityName
     * @param tableName
     * @param condition
     * @param params
     * @param sort          Order by column
     * @param sortAs        Order based on SQL type
     * @param desc
     * @param start
     * @param rows
     * @param loadDeleted load soft deleted rows
     * @return
     */
    @SuppressWarnings("unchecked")
    protected FormRowSet internalFind(final String entityName, final String tableName, final String condition, final Object[] params, final String sort, final String sortAs, final Boolean desc, final Integer start, final Integer rows, final Boolean loadDeleted) {
        // get hibernate template
        Session session = getHibernateSession(tableName, tableName, null, ACTION_TYPE_LOAD);

        try {
            String query = "SELECT e FROM " + tableName + " e ";
            query += (condition != null && !condition.isEmpty() ? (condition + " AND ") : " WHERE ")
                    + "(" + (FormUtil.PROPERTY_ORG_ID + " IS NULL OR " + FormUtil.PROPERTY_ORG_ID + " in ('" + FORM_ANY_ORG_ID + "', ?)") + ") AND "
                    + "(" + (loadDeleted != null && loadDeleted ? " 1 = 1 " : (FormUtil.PROPERTY_DELETED + " = false OR " + FormUtil.PROPERTY_DELETED + " IS NULL ")) + ")";

            if ((sort != null && !sort.trim().isEmpty()) && !query.toLowerCase().contains("order by")) {
                String sortProperty = sort;
                if (!isDefaultFormColumns(sort)) {
                    Collection<String> columnNames = getFormDefinitionColumnNames(tableName);
                    if (columnNames.contains(sort)) {
                        sortProperty = FormUtil.PROPERTY_CUSTOM_PROPERTIES + "." + sort;
                    }
                }

                if(sortAs.contains("?")) {
                    query += " ORDER BY " + sortAs.replaceAll("\\?", "e." + sortProperty);
                } else {
                    query += " ORDER BY cast(e." + sortProperty + " as " + sortAs + ")";
                }

                if (desc) {
                    query += " DESC";
                }
            }
            Query q = session.createQuery(query);

            int s = (start == null) ? 0 : start;
            q.setFirstResult(s);

            if (rows != null && rows > 0) {
                q.setMaxResults(rows);
            }

            int i = 0;
            if (params != null) {
                for (Object param : params) {
                    q.setParameter(i, param);
                    i++;
                }
            }

            String orgId = WorkflowUtil.getCurrentUserOrgId();
            q.setParameter(i, orgId);

            @SuppressWarnings("rawtypes")
            Collection result = q.list();

            FormRowSet rowSet = new FormRowSet();
            rowSet.addAll(result);
            return rowSet;
        } finally {
            closeSession(session);
        }
    }

    /**
     * Query total row count for a form.
     *
     * @param formDefId
     * @param tableName
     * @param condition
     * @param params
     * @return
     */
    public Long count(String formDefId, String tableName, final String condition, final Object[] params) {

        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);

        return internalCount(entityName, newTableName, condition, params);
    }

    /**
     * Query total row count for a form.
     *
     * @param form
     * @param condition
     * @param params
     * @return
     */
    public Long count(Form form, final String condition, final Object[] params) {

        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);

        return internalCount(entityName, tableName, condition, params);
    }

    /**
     * Query total row count for a form.
     *
     * @param entityName
     * @param tableName
     * @param condition
     * @param params
     * @return
     */
    protected Long internalCount(final String entityName, final String tableName, final String condition, final Object[] params) {
        // get hibernate template
        Session session = getHibernateSession(tableName, tableName, null, ACTION_TYPE_LOAD);
        try {
            String query = "SELECT COUNT(*) FROM " + tableName + " e ";
            query += (condition != null && !condition.isEmpty() ? (condition + " AND ") : " WHERE ") + "(" + (FormUtil.PROPERTY_DELETED + " = false OR " + FormUtil.PROPERTY_DELETED + " IS NULL) AND (" + FormUtil.PROPERTY_ORG_ID + " IS NULL OR " + FormUtil.PROPERTY_ORG_ID + " IN ('" + FORM_ANY_ORG_ID + "', ?))");
            Query q = session.createQuery(query);

            int i = 0;
            if (params != null) {
                for (Object param : params) {
                    q.setParameter(i, param);
                    i++;
                }
            }
            q.setParameter(i, WorkflowUtil.getCurrentUserOrgId());

            return ((Long) q.iterate().next());
        } finally {
            closeSession(session);
        }
    }

    /**
     * Query to find find primary key based on a field name and it's value.
     *
     * @param form
     * @param fieldName
     * @param value
     * @return
     */
    public String findPrimaryKey(Form form, final String fieldName, final String value) {
        final String entityName = getFormEntityName(form);
        final String tableName = getFormTableName(form);

        return internalFindPrimaryKey(entityName, tableName, fieldName, value);
    }

    /**
     * Query to find find primary key based on a field name and it's value.
     *
     * @param formDefId
     * @param tableName
     * @param fieldName
     * @param value
     * @return
     */
    public String findPrimaryKey(String formDefId, String tableName, final String fieldName, final String value) {
        final String entityName = getFormEntityName(formDefId);
        final String newTableName = getFormTableName(formDefId, tableName);

        return internalFindPrimaryKey(entityName, newTableName, fieldName, value);
    }

    /**
     * Query to find find primary key based on a field name and it's value.
     *
     * @param entityName
     * @param tableName
     * @param fieldName
     * @param value
     * @return
     */
    protected String internalFindPrimaryKey(final String entityName, final String tableName, final String fieldName, final String value) {
        // get hibernate template
        Session session = getHibernateSession(tableName, tableName, null, ACTION_TYPE_LOAD);
        try {
            String query = "SELECT e.id FROM " + tableName + " e WHERE " + FormUtil.PROPERTY_CUSTOM_PROPERTIES + "." + fieldName + " = ? AND (" + FormUtil.PROPERTY_DELETED + " IS NULL OR " + FormUtil.PROPERTY_DELETED + " = false) AND (" + FormUtil.PROPERTY_ORG_ID + " IS NULL OR " + FormUtil.PROPERTY_ORG_ID + " IN ('" + FORM_ANY_ORG_ID + "', ?))";

            Query q = session.createQuery(query);

            q.setFirstResult(0);
            q.setMaxResults(1);
            q.setParameter(0, value);
            q.setParameter(1, WorkflowUtil.getCurrentUserOrgId());

            if (q.list().size() > 0) {
                return ((String) q.iterate().next());
            }
            return null;
        } finally {
            closeSession(session);
        }
    }

    /**
     * Saves (creates or updates) form data
     *
     * @param form
     * @param rowSet
     */
    public void saveOrUpdate(Form form, FormRowSet rowSet) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        internalSaveOrUpdate(entityName, tableName, rowSet);
    }

    /**
     * Saves (creates or updates) form data
     *
     * @param formDefId
     * @param tableName
     * @param rowSet
     */
    public void saveOrUpdate(String formDefId, String tableName, FormRowSet rowSet) {
        String entityName = getFormEntityName(formDefId);
        String newTableName = getFormTableName(formDefId, tableName);
        internalSaveOrUpdate(entityName, newTableName, rowSet);
    }

    /**
     * Saves (creates or updates) form data
     *
     * @param entityName
     * @param tableName
     * @param rowSet
     */
    protected void internalSaveOrUpdate(String entityName, String tableName, FormRowSet rowSet) {
        // get hibernate template
        Session session = getHibernateSession(entityName, tableName, rowSet, ACTION_TYPE_STORE);

        try {
            final String username = WorkflowUtil.getCurrentUsername();
            final String orgId = WorkflowUtil.getCurrentUserOrgId();
            final Date now = new Date();

            // save the form data
            for (FormRow row : rowSet) {
                if(row.getCreatedBy() == null) row.setCreatedBy(username);
                if(row.getDateCreated() == null) row.setDateCreated(now);

                row.setModifiedBy(username);
                row.setDateModified(now);

                if (row.getOrgId() == null) row.setOrgId(orgId);
                if (row.getOrgId().equals("*") || row.getOrgId().equals(orgId)) session.saveOrUpdate(entityName, row);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    /**
     * Call Hibernate to update DB schema
     *
     * @param form
     * @param rowSet
     */
    public void updateSchema(Form form, FormRowSet rowSet) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        Session session = getHibernateSession(entityName, tableName, rowSet, ACTION_TYPE_STORE);

        closeSession(session);
    }

    /**
     * Call Hibernate to update DB schema
     *
     * @param formDefId
     * @param tableName
     * @param rowSet
     */
    public void updateSchema(String formDefId, String tableName, FormRowSet rowSet) {
        String entityName = getFormEntityName(formDefId);
        String newTableName = getFormTableName(formDefId, tableName);
        Session session = getHibernateSession(entityName, newTableName, rowSet, ACTION_TYPE_STORE);

        closeSession(session);
    }

    protected void internalSoftDelete(String entityName, String tableName, String[] primaryKeyValues) {
        if (primaryKeyValues.length > 0) {
            String currentUsername = WorkflowUtil.getCurrentUsername();
            String condition = Arrays.stream(primaryKeyValues)
                    .map(s -> "?")
                    .collect(Collectors.joining(", ", "where id in (", ")"));

            FormRowSet rowSet = Optional.ofNullable(internalFind(entityName, tableName, condition, primaryKeyValues, null, null, null, null, null, false))
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .peek(row -> {
                        row.setDateModified(new Date());
                        row.setModifiedBy(currentUsername);
                        row.setDeleted(true);
                    })
                    .collect(Collectors.toCollection(FormRowSet::new));

            internalSaveOrUpdate(entityName, tableName, rowSet);
        }
    }

    /**
     * Delete form data by primary keys
     *
     * @param form
     * @param primaryKeyValues
     */
    public void delete(Form form, String[] primaryKeyValues) {
        delete(form, primaryKeyValues, false);
    }

    /**
     * @param form
     * @param primaryKeyValues
     * @param isHardDelete     true for deleting data from database, false to flag data as deleted
     */
    public void delete(Form form, String[] primaryKeyValues, boolean isHardDelete) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        if (isHardDelete) {
            internalDelete(entityName, tableName, primaryKeyValues);
        } else {
            internalSoftDelete(entityName, tableName, primaryKeyValues);
        }
    }

    /**
     * Delete form data by primary keys
     *
     * @param formDefId
     * @param tableName
     * @param primaryKeyValues
     */
    public void delete(String formDefId, String tableName, String[] primaryKeyValues) {
        delete(formDefId, tableName, primaryKeyValues, false);
    }

    /**
     * @param formDefId
     * @param tableName
     * @param primaryKeyValues
     * @param isHardDelete     true for deleting data from database, false to flag data as deleted
     */
    public void delete(String formDefId, String tableName, String[] primaryKeyValues, boolean isHardDelete) {
        String entityName = getFormEntityName(formDefId);
        String newTableName = getFormTableName(formDefId, tableName);
        if (isHardDelete) {
            internalDelete(entityName, newTableName, primaryKeyValues);
        } else {
            internalSoftDelete(entityName, newTableName, primaryKeyValues);
        }

    }

    /**
     * Delete form data by primary keys
     *
     * @param formDefId
     * @param tableName
     * @param rows
     */
    public void delete(String formDefId, String tableName, FormRowSet rows) {
        delete(formDefId, tableName, rows, false);
    }

    public void delete(String formDefId, String tableName, FormRowSet rows, boolean isHardDelete) {
        String entityName = getFormEntityName(formDefId);
        String newTableName = getFormTableName(formDefId, tableName);
        if (isHardDelete) {
            internalDelete(entityName, newTableName, rows);
        } else {
            internalSoftDelete(entityName, newTableName, rows.stream().map(FormRow::getId).toArray(String[]::new));
        }
    }

    /**
     * @param entityName
     * @param tableName
     * @param rows
     */
    protected void internalDelete(String entityName, String tableName, FormRowSet rows) {
        // get hibernate template
        Session session = getHibernateSession(entityName, tableName, null, ACTION_TYPE_STORE);
        try {
            // save the form data
            for (FormRow row : rows) {
                session.delete(entityName, row);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    /**
     * Delete form data by primary keys
     *
     * @param entityName
     * @param tableName
     * @param primaryKeyValues
     */
    protected void internalDelete(String entityName, String tableName, String[] primaryKeyValues) {
        // get hibernate template
        Session session = getHibernateSession(entityName, tableName, null, ACTION_TYPE_STORE);

        try {
            // save the form data
            for (String key : primaryKeyValues) {
                Object obj = session.load(entityName, key);
                session.delete(entityName, obj);
            }
            session.flush();
        } finally {
            closeSession(session);
        }
    }

    /**
     * Gets the generated hibernate entity name for the form
     *
     * @param form
     * @return
     */
    public String getFormEntityName(Form form) {
        String formDefId = form.getPropertyString(FormUtil.PROPERTY_ID);
        String entityName = FormDataDaoImpl.FORM_PREFIX_ENTITY + formDefId;
        return entityName;
    }

    /**
     * Gets the generated hibernate entity name for the form
     *
     * @param formDefId
     * @return
     */
    public String getFormEntityName(String formDefId) {
        String entityName = FormDataDaoImpl.FORM_PREFIX_ENTITY + formDefId;
        return entityName;
    }

    /**
     * Gets the defined table name for the form
     *
     * @param form
     * @return
     */
    public String getFormTableName(Form form) {
        // determine table name
        String tableName = form.getPropertyString("tableName");
        if (tableName == null || tableName.trim().length() == 0) {
            // no table name specified, temp use ID
            String formDefId = form.getPropertyString(FormUtil.PROPERTY_ID);
            tableName = formDefId;
        }
        tableName = FormDataDaoImpl.FORM_PREFIX_TABLE_NAME + tableName;
        return tableName;
    }

    /**
     * Gets the defined table name for the form
     *
     * @param formDefId
     * @param tableName
     * @return
     */
    public String getFormTableName(String formDefId, String tableName) {
        // determine table name
        if (tableName == null || tableName.trim().length() == 0) {
            // no table name specified, temp use ID
            tableName = formDefId;
        }
        tableName = FormDataDaoImpl.FORM_PREFIX_TABLE_NAME + tableName;
        return tableName;
    }

    /**
     * Clears in-memory cache of generated hibernate templates
     */
    public void clearCache() {
        formSessionFactoryCache.removeAll();
        formPersistentClassCache.removeAll();
        formColumnCache.clear();
    }

    public void clearFormCache(Form form) {
        String entityName = getFormEntityName(form);
        String tableName = getFormTableName(form);
        formSessionFactoryCache.remove(getSessionFactoryCacheKey(entityName, tableName, null));
        formPersistentClassCache.remove(getPersistentClassCacheKey(entityName));

        // strip table prefix for form column cache
        if (tableName.startsWith(FORM_PREFIX_TABLE_NAME)) {
            tableName = tableName.substring(FORM_PREFIX_TABLE_NAME.length());
        }

        formColumnCache.remove(tableName);
    }

    protected String getSessionFactoryCacheKey(String entityName, String tableName, FormRowSet rowSet) {
        String profile = DynamicDataSourceManager.getCurrentProfile();
        String cacheKey = profile + ";" + entityName + ";" + tableName + ";";
        if (rowSet != null) {
            for (FormRow row : rowSet) {
                cacheKey += ";" + row.keySet();
                break;
            }
        }
        return cacheKey;
    }

    protected String getPersistentClassCacheKey(String entityName) {
        String profile = DynamicDataSourceManager.getCurrentProfile();
        String cacheKey = profile + ";PC:" + entityName + ";";
        return cacheKey;
    }

    /**
     * Gets the hibernate session for the entity
     *
     * @param entityName
     * @param tableName
     * @param rowSet
     * @param actionType
     * @return
     */
    protected Session getHibernateSession(String entityName, String tableName, FormRowSet rowSet, int actionType) throws HibernateException {
        Session session;
        if (actionType == ACTION_TYPE_LOAD) {
            // for load, use the db table as entity name
            entityName = tableName;

            // find session factory and open session
            SessionFactory sf = findSessionFactory(entityName, tableName, null, actionType);
            session = sf.withOptions().autoJoinTransactions(true).openSession();
        } else {
            // find session factory and open session
            SessionFactory sf = findSessionFactory(entityName, tableName, rowSet, actionType);
            session = sf.withOptions().autoJoinTransactions(true).openSession();
        }
        return session;
    }

    protected SessionFactory findSessionFactory(String entityName, String tableName, FormRowSet rowSet, int actionType) throws HibernateException {
        SessionFactory sf = null;
        PersistentClass pc = null;

        // lookup cache
        String cacheKey = getSessionFactoryCacheKey(entityName, tableName, rowSet);
        net.sf.ehcache.Element cacheElement = formSessionFactoryCache.get(cacheKey);
        if (cacheElement != null) {
            sf = (SessionFactory) cacheElement.getObjectValue();
            LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " session factory found in cache");
        }

        if (actionType == ACTION_TYPE_LOAD) {
            // find existing persistent class for comparison
            if (sf != null) {
                net.sf.ehcache.Element pcElement = formPersistentClassCache.get(getPersistentClassCacheKey(entityName));
                if (pcElement != null) {
                    pc = (PersistentClass) pcElement.getObjectValue();
                    LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " PersistentClass found in cache");
                }
            }

            Configuration configuration = null;
            if (pc == null) {
                // locate entity hbm mapping file
                String path = getFormMappingPath();
                String filename = entityName + ".hbm.xml";
                File mappingFile = new File(path, filename);
                if (mappingFile.exists()) {
                    // load existing mapping
                    configuration = new Configuration().configure();
                    configuration.addFile(mappingFile);
                    configuration.buildMappings();
                    pc = configuration.getClassMapping(entityName);
                    LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " loaded from mapping file " + mappingFile.getName());
                    // save into cache
                    formPersistentClassCache.put(new net.sf.ehcache.Element(getPersistentClassCacheKey(entityName), pc));
                }
            }
            if (pc != null) {
                // mapping exists, check for changes
                boolean changes = false;

                // check for table change
                if (!tableName.equals(pc.getTable().getName())) {
                    changes = true;
                }

                if (!changes) {
                    // get form fields
                    Collection<String> formFields = getFormDefinitionColumnNames(tableName);

                    if (!formFields.isEmpty()) {
                        Property custom = pc.getProperty(FormUtil.PROPERTY_CUSTOM_PROPERTIES);
                        Component customComponent = (Component) custom.getValue();

                        // compare existing mappings with new properties
                        int size = customComponent.getPropertySpan();
                        if (size == formFields.size()) {
                            // similar size, so compare individual fields
                            boolean found;
                            @SuppressWarnings("rawtypes")
                            Iterator i = customComponent.getPropertyIterator();
                            while (i.hasNext()) {
                                Property property = (Property) i.next();
                                String propertyName = property.getName();
                                found = formFields.contains(propertyName);
                                if (!found) {
                                    // property not found, fields changed
                                    changes = true;
                                    break;
                                }
                            }
                        } else {
                            // dissimilar size, fields changed
                            changes = true;
                        }
                    }
                }

                if (changes) {
                    LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " changes detected");

                    // properties changed, close session factory
                    if (sf != null) {
                        sf.close();
                        LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " existing session factory closed");
                    }

                    // clear session factory to be recreated
                    sf = null;

                    //remove persistentClassCache
                    formPersistentClassCache.remove(getPersistentClassCacheKey(entityName));
                } else if (configuration != null && sf == null) {
                    //no change, use existing mapping file if session factory not exist
                    LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " get session factory");
                    sf = getSessionFactory(entityName, cacheKey, actionType, configuration);
                }
            }
        }

        if (sf == null) {
            LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " create session factory");
            sf = createSessionFactory(entityName, tableName, rowSet, actionType);
        }
        return sf;
    }

    protected SessionFactory createSessionFactory(String entityName, String tableName, FormRowSet rowSet, int actionType) throws HibernateException {
        // create configuration
        Configuration configuration = new Configuration();
        configuration.setProperty("show_sql", "false");
        configuration.setProperty("cglib.use_reflection_optimizer", "true");

        // get columns
        Collection<String> formFields;
        if (rowSet != null) {
            // column names from submitted fields
            formFields = getFormRowColumnNames(rowSet);
        } else {
            // column names from all forms mapped to this table
            formFields = getFormDefinitionColumnNames(tableName);
        }

        // load default FormRow hbm xml
        synchronized (this) {
            if (formRowDocument == null) {
                try (InputStream is = Form.class.getResourceAsStream("/org/joget/apps/form/model/FormRow.hbm.xml")) {
                    formRowDocument = XMLUtil.loadDocument(is);
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    throw new HibernateException("Unable to load FormRow.hbm.xml", e);
                }
            }
        }
        Document document = (Document) formRowDocument.cloneNode(true);

        // update entity-name
        NodeList classTags = document.getElementsByTagName("class");
        Node classNode = classTags.item(0);
        NamedNodeMap attributeMap = classNode.getAttributes();
        Node entityNameNode = attributeMap.getNamedItem("entity-name");
        entityNameNode.setNodeValue(entityName);
        attributeMap.setNamedItem(entityNameNode);

        // update table name
        Node tableNameNode = attributeMap.getNamedItem("table");
        tableNameNode.setNodeValue(tableName);
        attributeMap.setNamedItem(tableNameNode);

        // remove existing dynamic components
        NodeList componentTags = document.getElementsByTagName("dynamic-component");
        Node node = componentTags.item(0);
        XMLUtil.removeChildren(node);

        // add dynamic components
        for (String field : formFields) {
            Element element = document.createElement("property");
            String propName = field;
            String columnName = FORM_PREFIX_COLUMN + field;
            if (propName != null && !propName.isEmpty()) {
                String propType = "text";
                element.setAttribute("name", propName);
                element.setAttribute("column", columnName);
                element.setAttribute("type", propType);
                element.setAttribute("not-null", String.valueOf(false));
                node.appendChild(element);
            }
        }

        if (actionType == ACTION_TYPE_LOAD) {
            // locate entity hbm mapping file
            String path = getFormMappingPath();
            String filename = entityName + ".hbm.xml";
            File mappingFile = new File(path, filename);
            try {
                // delete existing mapping file
                if (mappingFile.exists()) {
                    mappingFile.delete();
                }

                // save new mapping file
                XMLUtil.saveDocument(document, mappingFile.getPath());
            } catch (TransformerException | IOException e) {
                throw new HibernateException("Unable to save " + mappingFile.getPath(), e);
            }

            // add mapping to config
            configuration.addFile(mappingFile);
        } else {
            configuration.addDocument(document);
        }

        String cacheKey = getSessionFactoryCacheKey(entityName, tableName, rowSet);
        SessionFactory sf = getSessionFactory(entityName, cacheKey, actionType, configuration);

        return sf;
    }

    protected SessionFactory getSessionFactory(final String entityName, String cacheKey, int actionType, final Configuration configuration) {
        // set datasource
        DataSource dataSource = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        configuration.getProperties().put(Environment.DATASOURCE, dataSource);

        // build session factory
        final ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        SessionFactory sf = configuration.buildSessionFactory(sr);
        LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " session factory created");

        // update schema
        boolean requiresNewTransaction = false;
        try {
            Object dialect = org.apache.commons.beanutils.PropertyUtils.getProperty(sf, "dialect");
            requiresNewTransaction = (dialect.getClass().getName().startsWith("org.hibernate.dialect.SQLServer") || dialect.getClass().getName().startsWith("org.hibernate.dialect.PostgreSQL"));
        } catch (Exception ex) {
            LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Error retrieving hibernate dialect " + ex.toString());
        }
        if (requiresNewTransaction) {
            TransactionTemplate transactionTemplate = (TransactionTemplate) AppUtil.getApplicationContext().getBean("transactionTemplateRequiresNew");
            transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction(TransactionStatus ts) {
                    internalUpdateSchema(sr, configuration);
                    LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " schema updated in new transaction");
                    return null;
                }
            });
        } else {
            internalUpdateSchema(sr, configuration);
            LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " schema updated");
        }

        if (actionType == ACTION_TYPE_LOAD) {
            PersistentClass pc = configuration.getClassMapping(entityName);
            // save into cache
            formPersistentClassCache.put(new net.sf.ehcache.Element(getPersistentClassCacheKey(entityName), pc));
        }

        // save into cache
        formSessionFactoryCache.remove(cacheKey);
        formSessionFactoryCache.put(new net.sf.ehcache.Element(cacheKey, sf));
        LogUtil.debug(FormDataDaoImpl.class.getName(), "  --- Form " + entityName + " saved in cache");

        return sf;
    }


    /**
     * Trigger actual Hibernate schema update
     *
     * @param sr
     * @param configuration
     * @throws HibernateException
     */
    protected void internalUpdateSchema(ServiceRegistry sr, Configuration configuration) throws HibernateException {
        try {
            new SchemaExport(sr, configuration).setImportSqlCommandExtractor(sr.getService(ImportSqlCommandExtractor.class)).execute(false, true, false, true);
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error creating schema");
        }
        try {
            new SchemaUpdate(sr, configuration).execute(false, true);
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error updating schema");
        }
    }

    /**
     * Returns the base directory used to store hibernate mapping files
     *
     * @return
     */
    protected String getFormMappingPath() {
        // determine path to mapping directory
        String path = SetupManager.getBaseDirectory();
        String dataFileBasePath = SetupManager.getSettingValue("dataFileBasePath");
        if (dataFileBasePath != null && dataFileBasePath.length() > 0) {
            path = dataFileBasePath;
        }

        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        path += FormDataDaoImpl.FORM_MAPPING_DIRECTORY;
        new File(path).mkdirs();
        return path;
    }

    /**
     * Returns collection of all column names to be saved
     *
     * @param rowSet
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getFormRowColumnNames(FormRowSet rowSet) {
        Collection<String> columnList = new ArrayList<String>();
        Collection<String> lowerCaseColumnSet = new HashSet<String>();
        if (rowSet != null && !rowSet.isEmpty()) {
            @SuppressWarnings("rawtypes")
            Set columnsName = new HashSet();

            for (FormRow row : rowSet) {
                if (row != null && !row.isEmpty()) {
                    columnsName.addAll(row.keySet());
                }
            }

            for (Object column : columnsName) {
                String columnName = (String) column;
                if (columnName != null && !columnName.isEmpty() && !FormUtil.PROPERTY_ID.equals(columnName)
                        && !FormUtil.PROPERTY_DATE_CREATED.equals(columnName) && !FormUtil.PROPERTY_DATE_MODIFIED.equals(columnName)
                        && !FormUtil.PROPERTY_CREATED_BY.equals(columnName) && !FormUtil.PROPERTY_MODIFIED_BY.equals(columnName)
                        && !FormUtil.PROPERTY_ORG_ID.equals(columnName) && !FormUtil.PROPERTY_DELETED.equals(columnName)) {
                    String lowerCasePropName = columnName.toLowerCase();
                    if (!lowerCaseColumnSet.contains(lowerCasePropName)) {
                        columnList.add(columnName);
                        lowerCaseColumnSet.add(lowerCasePropName);
                    }
                }
            }

            //remove predefined column
            columnList.remove(FormUtil.PROPERTY_ID);
            columnList.remove(FormUtil.PROPERTY_DATE_CREATED);
            columnList.remove(FormUtil.PROPERTY_DATE_MODIFIED);
            columnList.remove(FormUtil.PROPERTY_CREATED_BY);
            columnList.remove(FormUtil.PROPERTY_MODIFIED_BY);
            columnList.remove(FormUtil.PROPERTY_ORG_ID);
            columnList.remove(FormUtil.PROPERTY_DELETED);
        }
        return columnList;
    }

    /**
     * Returns collection of all columns from forms mapped to a table
     *
     * @param tableName
     * @return
     */
    public Collection<String> getFormDefinitionColumnNames(String tableName) {
        Collection<String> columnList;
        Map<String, String> checkDuplicateMap = new HashMap<String, String>();

        // strip table prefix
        if (tableName.startsWith(FORM_PREFIX_TABLE_NAME)) {
            tableName = tableName.substring(FORM_PREFIX_TABLE_NAME.length());
        }

        // get forms mapped to the table name
        columnList = formColumnCache.get(tableName);
        if (columnList == null) {
            LogUtil.debug("", "======== Build Form Column Cache for table \"" + tableName + "\" START ========");
            columnList = new HashSet<String>();
            Collection<FormDefinition> formList = getFormDefinitionDao().loadFormDefinitionByTableName(tableName);
            if (formList != null && !formList.isEmpty()) {
                for (FormDefinition formDef : formList) {
                    // get JSON
                    String json = formDef.getJson();
                    if (json != null) {
                        Form form = (Form) getFormService().createElementFromJson(json, false);
                        Collection<String> tempColumnList = new HashSet<String>();
                        findAllElementIds(form, tempColumnList);

                        LogUtil.debug("", "Columns of Form \"" + formDef.getId() + "\" [" + formDef.getAppId() + " v" + formDef.getAppVersion() + "] - " + tempColumnList.toString());
                        for (String c : tempColumnList) {
                            if (!c.isEmpty()) {
                                String exist = checkDuplicateMap.get(c.toLowerCase());
                                if (exist != null && !exist.equals(c)) {
                                    LogUtil.warn("", "Detected duplicated column in Form \"" + formDef.getId() + "\" [" + formDef.getAppId() + " v" + formDef.getAppVersion() + "]: \"" + exist + "\" and \"" + c + "\". Removed \"" + exist + "\" and replaced with \"" + c + "\".");
                                    columnList.remove(exist);
                                }
                                checkDuplicateMap.put(c.toLowerCase(), c);
                                columnList.add(c);
                            }
                        }
                    }
                }

                //remove predefined column
                columnList.remove(FormUtil.PROPERTY_ID);
                columnList.remove(FormUtil.PROPERTY_DATE_CREATED);
                columnList.remove(FormUtil.PROPERTY_DATE_MODIFIED);
                columnList.remove(FormUtil.PROPERTY_CREATED_BY);
                columnList.remove(FormUtil.PROPERTY_MODIFIED_BY);
                columnList.remove(FormUtil.PROPERTY_ORG_ID);
                columnList.remove(FormUtil.PROPERTY_DELETED);

                LogUtil.debug("", "All Columns - " + columnList.toString());
                formColumnCache.put(tableName, columnList);
            }
            LogUtil.debug("", "======== Build Form Column Cache for table \"" + tableName + "\" END   ========");
        }
        return columnList;
    }

    /**
     * Returns EntityName of form mapped to a table & column
     *
     * @param tableName
     * @param columnName
     * @return
     */
    public String getEntityName(String tableName, String columnName) {
        // strip table prefix
        if (tableName.startsWith(FORM_PREFIX_TABLE_NAME)) {
            tableName = tableName.substring(FORM_PREFIX_TABLE_NAME.length());
        }

        // get forms mapped to the table name
        Collection<FormDefinition> formList = getFormDefinitionDao().loadFormDefinitionByTableName(tableName);
        for (FormDefinition formDef : formList) {
            // get JSON
            String json = formDef.getJson();
            if (json != null) {
                Form form = (Form) getFormService().createElementFromJson(json);
                String formTableName = form.getPropertyString(FormUtil.PROPERTY_TABLE_NAME);
                if (tableName.equals(formTableName) && FormUtil.findElement(columnName, form, null) != null) {
                    return getFormEntityName(form);
                }
            }
        }
        return null;
    }

    /**
     * Recurse into child elements to find add all element property ID to columnList.
     *
     * @param element
     * @param columnList
     */
    protected void findAllElementIds(org.joget.apps.form.model.Element element, Collection<String> columnList) {
        Collection<String> fieldNames = element.getDynamicFieldNames();
        if (fieldNames != null && !fieldNames.isEmpty()) {
            columnList.addAll(fieldNames);
        }
        if (!(element instanceof FormContainer) && element.getProperties() != null) {
            String id = element.getPropertyString(FormUtil.PROPERTY_ID);
            if (id != null && !id.isEmpty()) {
                columnList.add(id);
            }
        }
        if (!(element instanceof AbstractSubForm)) { // do not recurse into subforms
            Collection<org.joget.apps.form.model.Element> children = element.getChildren();
            if (children != null) {
                for (org.joget.apps.form.model.Element child : children) {
                    findAllElementIds(child, columnList);
                }
            }
        }
    }

    protected void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    protected boolean isDefaultFormColumns(String column) {
        return FormUtil.PROPERTY_ID.equals(column)
                || FormUtil.PROPERTY_DATE_CREATED.equals(column) || FormUtil.PROPERTY_DATE_MODIFIED.equals(column)
                || FormUtil.PROPERTY_CREATED_BY.equals(column) || FormUtil.PROPERTY_MODIFIED_BY.equals(column)
                || FormUtil.PROPERTY_DELETED.equals(column);
    }
}