package org.joget.apps.app.web;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.spring.web.CustomContextLoaderListener;
import org.joget.commons.spring.web.CustomDispatcherServlet;
import org.joget.commons.util.DynamicDataSourceManager;
import org.joget.commons.util.HostManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.kecak.apps.scheduler.SchedulerManager;
import org.kecak.apps.scheduler.SchedulerPluginJob;
import org.kecak.apps.scheduler.model.SchedulerDetails;
import org.kecak.apps.scheduler.model.TriggerTypes;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Servlet to handle first-time database setup and initialization.
 */
public class SetupServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2501272432350023415L;

	/**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // check for existing setup, return 403 forbidden if already configured (currentProfile key exists in app_datasource.properties) or virtual hosting is enabled
        Properties properties = DynamicDataSourceManager.getProfileProperties();
        if (HostManager.isVirtualHostEnabled() || properties == null || properties.containsKey("currentProfile")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // handle request
        String path = request.getPathInfo();
        if (path == null || "/".equals(path)) {
            // forward to the setup JSP
            request.getRequestDispatcher("/WEB-INF/jsp/setup/setup.jsp").forward(request, response);
        } else if ("/init".equals(path)) {
            // only allow POST for initialization request
            if (!"post".equalsIgnoreCase(request.getMethod())) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }

            // get parameters
            String jdbcDriver = request.getParameter("jdbcDriver");
            String jdbcUrl = request.getParameter("jdbcUrl");
            String jdbcFullUrl = request.getParameter("jdbcFullUrl");
            String jdbcUser = request.getParameter("jdbcUser");
            String jdbcPassword = request.getParameter("jdbcPassword");
            String dbType = request.getParameter("dbType");
            String dbName = request.getParameter("dbName");
            String sampleApps = request.getParameter("sampleApps");
            String sampleUsers = request.getParameter("sampleUsers");
            if ("custom".equals(dbType)) {
                dbName = null;
            }

            // create datasource
            LogUtil.info(getClass().getName(), "===== Starting Database Setup =====");
            boolean success = false;
            boolean exists = false;
            String message = "";
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(jdbcDriver);
            ds.setUsername(jdbcUser);
            ds.setPassword(jdbcPassword);
            ds.setUrl(jdbcUrl);
            try(Connection con = ds.getConnection()) {
                success = true;

                // test connection
                message = ResourceBundleUtil.getMessage("setup.datasource.label.success");

                LogUtil.info(getClass().getName(), "Use database [" + dbName + "]");
                con.setCatalog(dbName);

                // check for existing tables
                try (Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM dir_role")) {

                    exists = rs.next();
                } catch (SQLException ex) {
                    LogUtil.error(getClass().getName(), ex, ex.getMessage());
                }
            } catch (SQLException e) {
            		LogUtil.error(getClass().getName(), e, e.getMessage());
			}
            
            try {
	            if (exists) {
	            	LogUtil.info(getClass().getName(), "Database already initialized [" + jdbcUrl + "]");
	            } else {
                    LogUtil.info(getClass().getName(), "Database not yet initialized [" + jdbcUrl + "]");
	            		
	                // get schema file
	                String schemaFile = null;
	                String quartzFile = null;
	                if ("oracle".equals(dbType) || jdbcUrl.contains("oracle")) {
	                    schemaFile = "/setup/sql/oracle.sql";
	                    quartzFile = "/setup/sql/quartz-tables_oracle.sql";
	                } else if ("sqlserver".equals(dbType) || jdbcUrl.contains("sqlserver")) {
	                    schemaFile = "/setup/sql/mssql.sql";
	                    quartzFile = "/setup/sql/quartz-tables_sqlServer.sql";
	                } else if ("mysql".equals(dbType) || jdbcUrl.contains("mysql")) {
	                    schemaFile = "/setup/sql/mysql.sql";
	                    quartzFile = "/setup/sql/quartz-tables_mysql_innodb.sql";
	                } else if("postgresql".equals(dbType) || jdbcUrl.contains("postgresql")) {
                        schemaFile = "/setup/sql/postgresql.sql";
                        quartzFile = "/setup/sql/quartz-tables_postgres.sql";
	                } else {
	                    throw new SQLException("Unrecognized database type, please setup the datasource manually");
	                }
	
	                if (dbName != null) {
	                     // create database
	                		ds.setUrl(jdbcUrl);
	                     try( Connection con = ds.getConnection();
	                    		 Statement stmt = con.createStatement()) {
                        LogUtil.info(getClass().getName(), "Create database [" + dbName + "]");
                        stmt.executeUpdate("CREATE DATABASE " + dbName + ";");
	                    } catch(SQLException ex) {
	                         LogUtil.error(getClass().getName(), ex, ex.getMessage());
	                    }
	                }
	                
	                ds.setUrl(jdbcFullUrl);
	                try(Connection con = ds.getConnection()) {
                        con.setAutoCommit(false);
                        con.setCatalog(dbName);

	                    {
		                    // execute schema file
		                    LogUtil.info(getClass().getName(), "Execute schema [" + schemaFile + "]");
		                    ScriptRunner runner = new ScriptRunner(con, false, false);
		                    try(BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(schemaFile)))) {
                                runner.runScript(br);
                            } catch (SQLException | IOException e) {
                                LogUtil.error(getClass().getName(), e, e.getMessage());
                            }
	                    }
	                    
	                    {
		                    // execute quartz file
		                    LogUtil.info(getClass().getName(), "Execute quartz [" + quartzFile + "]");
		                    ScriptRunner runner = new ScriptRunner(con, false, false);
		                    try(BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(quartzFile)))) {
                                runner.runScript(br);
                            } catch (SQLException | IOException e) {
                                LogUtil.error(getClass().getName(), e, e.getMessage());
                            }
	                    }

                        con.commit();
	                } catch(SQLException e) {
	                		LogUtil.error(getClass().getName(), e, e.getMessage());
	                }
	            }
            
	            try(Connection con = ds.getConnection()) {
                    con.setAutoCommit(false);
                    con.setCatalog(dbName);

	                if ("true".equals(sampleUsers)) {
	                    // create users
	                    String schemaFile = "/setup/sql/users.sql";
	                    LogUtil.info(getClass().getName(), "Create users using schema [" + schemaFile + "]");
                        con.setCatalog(dbName);
	                    ScriptRunner runner = new ScriptRunner(con, false, true);
	                    runner.runScript(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(schemaFile))));
	                }

	                con.commit();
	                LogUtil.info(getClass().getName(), "Datasource init complete [" + success + "]");
	                
	                // save profile
//	                String profileName = (dbName != null && !dbName.trim().isEmpty()) ? dbName : "custom";
	                String profileName = DynamicDataSourceManager.DEFAULT_PROFILE;
	                String jdbcUrlToSave = (jdbcFullUrl != null && !jdbcFullUrl.trim().isEmpty()) ? jdbcFullUrl : jdbcUrl;
	                LogUtil.info(getClass().getName(), "Save profile [" + profileName + "]");
	                DynamicDataSourceManager.createProfile(profileName);
	                DynamicDataSourceManager.changeProfile(profileName);
	                DynamicDataSourceManager.writeProperty("workflowDriver", jdbcDriver);
	                DynamicDataSourceManager.writeProperty("workflowUrl", jdbcUrlToSave);
	                DynamicDataSourceManager.writeProperty("workflowUser", jdbcUser);
	                DynamicDataSourceManager.writeProperty("workflowPassword", jdbcPassword);
	                
	                // initialize spring application context
	                ServletContext sc = request.getServletContext();
	                ServletContextEvent sce = new ServletContextEvent(sc);
	                CustomContextLoaderListener cll = new CustomContextLoaderListener();
	                cll.contextInitialized(sce);
	                DispatcherServlet servlet = CustomDispatcherServlet.getCustomDispatcherServlet();
	                servlet.init();
	                
	                if (sampleApps != null) {
	                    // import sample apps
	                    ApplicationContext context = AppUtil.getApplicationContext();
	                    try(InputStream setupInput = getClass().getResourceAsStream("/setup/setup.properties")) {
                            Properties setupProps = new Properties();
	                        setupProps.load(setupInput);
	                        String sampleDelimitedApps = setupProps.getProperty("sample.apps");
	                        StringTokenizer appTokenizer = new StringTokenizer(sampleDelimitedApps, ",");
	                        while (appTokenizer.hasMoreTokens()) {
	                            String appPath = appTokenizer.nextToken();
	                            importApp(context, appPath);
	                        }
	                    }
	                }                
	                
	                LogUtil.info(getClass().getName(), "Profile init complete [" + profileName + "]");
	                LogUtil.info(getClass().getName(), "===== Database Setup Complete =====");

	                //Initialize Scheduler Job
                    SchedulerDetails schedulerDetails = new SchedulerDetails();
                    schedulerDetails.setJobName("SchedulerJob");
                    schedulerDetails.setJobClassName(SchedulerPluginJob.class.getName());
//                    schedulerDetails.setCronExpression("0 0/1 * * * ? *"); // run every 1 minute
                    schedulerDetails.setCronExpression("0 0/5 * * * ? *"); // run every 5 minutes
                    schedulerDetails.setGroupJobName("SchedulerJob");
                    schedulerDetails.setGroupTriggerName("SchedulerJob");
                    Date now = new Date();
                    schedulerDetails.setDateCreated(now);
                    schedulerDetails.setCreatedBy("admin");
                    schedulerDetails.setDateModified(now);
                    schedulerDetails.setModifiedBy("admin");
                    schedulerDetails.setTriggerTypes(TriggerTypes.CRON);
                    schedulerDetails.setTriggerName("SchedulerJob");
                    SchedulerManager schedulerManager = (SchedulerManager) AppUtil.getApplicationContext().getBean("schedulerManager");
                    schedulerManager.saveOrUpdateJobDetails(schedulerDetails);
	            }
                
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, ex.toString());
                success = false;
                message = ex.getMessage().replace("'", " ");
            } finally {
                try {
                    ds.close();
                } catch (SQLException ex) {
                    // ignore
                }
            }

            // send result
            response.setContentType("application/json;charset=UTF-8");
            try(PrintWriter out = response.getWriter()) {
                out.println("{\"action\":\"init\",\"result\":\"" + success + "\",\"message\":\"" + StringEscapeUtils.escapeJavaScript(message) + "\"}");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Import an app from a specified path.
     * @param context
     * @param path
     */
    protected void importApp(ApplicationContext context, String path) {
        LogUtil.info(getClass().getName(), "Import app " + path);
        try {
            final AppService appService = (AppService)context.getBean("appService");
            InputStream in = getClass().getResourceAsStream(path);
            byte[] fileContent = readInputStream(in);
            final AppDefinition appDef = appService.importApp(fileContent);
            if (appDef != null) {
                TransactionTemplate transactionTemplate = (TransactionTemplate) AppUtil.getApplicationContext().getBean("transactionTemplate");
                transactionTemplate.execute(new TransactionCallback<Object>() {
                    public Object doInTransaction(TransactionStatus ts) {
                        appService.publishApp(appDef.getId(), null);
                        return null;
                    }
                });
            }
        } catch(Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Failed to import app " + path);
        }
    }    

    /**
     * Reads a specified InputStream, returning its contents in a byte array
     * @param in
     * @return
     * @throws IOException 
     */
    protected byte[] readInputStream(InputStream in) throws IOException {
        byte[] fileContent;

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream bin = new BufferedInputStream(in);) {
            int len;
            byte[] buffer = new byte[4096];
            while ((len = bin.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            fileContent = out.toByteArray();
            return fileContent;
        }
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet to handle first-time database setup and initialization";
    }

}
