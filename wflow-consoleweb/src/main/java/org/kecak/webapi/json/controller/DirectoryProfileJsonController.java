package org.kecak.webapi.json.controller;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.dao.PackageDefinitionDao;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AuditTrailManager;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.service.FormService;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.User;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.model.dao.WorkflowProcessLinkDao;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.apps.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

@Controller
public class DirectoryProfileJsonController {
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private AppService appService;
    @Autowired
    private AppDefinitionDao appDefinitionDao;
    @Autowired
    private DataListService dataListService;
    @Autowired
    private DatalistDefinitionDao datalistDefinitionDao;
    @Autowired
    private FormService formService;
    @Autowired
    private WorkflowProcessLinkDao workflowProcessLinkDao;
    @Autowired
    private FormDataDao formDataDao;
    @Autowired
    private AuditTrailManager auditTrailManager;
    @Autowired
    private WorkflowHelper workflowHelper;
    @Autowired
    private SetupManager setupManager;
    @Autowired
    private PackageDefinitionDao packageDefinitionDao;
    @Autowired
    WorkflowUserManager workflowUserManager;
    @Autowired
    UserDao userDao;

    /**
     * Get Current Profile
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @throws ApiException
     */
    @Deprecated
    @RequestMapping(value = "/json/data/profile/current", method = RequestMethod.GET)
    public void getCurrentProfile(final HttpServletRequest request, final HttpServletResponse response) throws ApiException  {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
        User user = userDao.getUser(workflowUserManager.getCurrentUsername());
        if (user == null) {
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User doesn't have permission");
        }
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("username", user.getUsername());
            Blob blob = user.getProfilePicture();
            try(InputStream in = blob.getBinaryStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                BufferedImage image = ImageIO.read(in);
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageInByteArray = baos.toByteArray();
                String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);
                jsonResponse.put("profilePicture","data:image/jpg;base64,"+b64);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get Profile
     *
     * @param request    HTTP Request
     * @param response   HTTP Response
     * @throws ApiException
     */
    @RequestMapping(value = "/json/directory/profile", method = RequestMethod.GET)
    public void getProfile(final HttpServletRequest request, final HttpServletResponse response) throws ApiException  {

        LogUtil.info(getClass().getName(), "Executing JSON Rest API [" + request.getRequestURI() + "] in method [" + request.getMethod() + "] as [" + WorkflowUtil.getCurrentUsername() + "]");
        User user = userDao.getUser(workflowUserManager.getCurrentUsername());
        if (user == null) {
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "User doesn't have permission");
        }
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("username", user.getUsername());
            Blob blob = user.getProfilePicture();
            try(InputStream in = blob.getBinaryStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                BufferedImage image = ImageIO.read(in);
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageInByteArray = baos.toByteArray();
                String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);
                jsonResponse.put("profilePicture","data:image/jpg;base64,"+b64);
                response.getWriter().write(jsonResponse.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
