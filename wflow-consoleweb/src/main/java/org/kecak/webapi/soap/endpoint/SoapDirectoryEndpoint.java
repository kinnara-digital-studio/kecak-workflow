package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.AuditTrailDao;
import org.joget.apps.app.model.AuditTrail;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.model.*;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;


@Endpoint
public class SoapDirectoryEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> nameExpression;
    private XPathExpression<Element> orgIdExpression;
    private XPathExpression<Element> groupIdExpression;
    private XPathExpression<Element> gradeIdExpression;
    private XPathExpression<Element> deptIdExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;
    private XPathExpression<Element> userIdExpression;
    private XPathExpression<Element> inGroupExpression;
    private XPathExpression<Element> roleIdExpression;
    private XPathExpression<Element> activeExpression;
    private XPathExpression<Element> usernameExpression;
    private XPathExpression<Element> passwordExpression;
    private XPathExpression<Element> hashExpression;

    @Autowired
    @Qualifier("main")
    ExtDirectoryManager directoryManager;
    @Autowired
    EmploymentDao employmentDao;
    @Autowired
    HttpServletRequest httpRequest;
    @Autowired
    HttpServletResponse httpResponse;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    WorkflowManager workflowManager;

    public SoapDirectoryEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            nameExpression = xpathFactory.compile("//xps:name", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            nameExpression = null;
        }
        try {
            orgIdExpression = xpathFactory.compile("//xps:orgId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            orgIdExpression = null;
        }
        try {
            groupIdExpression = xpathFactory.compile("//xps:groupId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            groupIdExpression = null;
        }
        try {
            gradeIdExpression = xpathFactory.compile("//xps:gradeId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            gradeIdExpression = null;
        }
        try {
            deptIdExpression = xpathFactory.compile("//xps:deptId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            deptIdExpression = null;
        }
        try {
            sortExpression = xpathFactory.compile("//xps:sort", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            sortExpression = null;
        }
        try {
            descExpression = xpathFactory.compile("//xps:desc", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            descExpression = null;
        }
        try {
            startExpression = xpathFactory.compile("//xps:start", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            startExpression = null;
        }
        try {
            rowsExpression = xpathFactory.compile("//xps:rows", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            rowsExpression = null;
        }
        try {
            userIdExpression = xpathFactory.compile("//xps:userId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            userIdExpression = null;
        }
        try {
            inGroupExpression = xpathFactory.compile("//xps:inGroup", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            inGroupExpression = null;
        }
        try {
            roleIdExpression = xpathFactory.compile("//xps:roleId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            roleIdExpression = null;
        }
        try {
            activeExpression = xpathFactory.compile("//xps:active", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            activeExpression = null;
        }
        try {
            usernameExpression = xpathFactory.compile("//xps:username", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            usernameExpression = null;
        }
        try {
            passwordExpression = xpathFactory.compile("//xps:password", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            passwordExpression = null;
        }
        try {
            hashExpression = xpathFactory.compile("//xps:hash", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            hashExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminDeptListRequest")
    public @ResponsePayload Element handleAdminDeptList(@RequestPayload Element adminDeptListElement) {
        final String name = nameExpression.evaluate(adminDeptListElement).get(0).getValue();
        String orgId = orgIdExpression.evaluate(adminDeptListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminDeptListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminDeptListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminDeptListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminDeptListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminDeptListResponse",namespace);
        Collection<Department> departments = null;

        if ("".equals(orgId)) {
            orgId = null;
        }

        departments = directoryManager.getDepartmentsByOrganizationId(name, orgId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (departments != null) {
            for (Department department : departments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(department.getId()));
                item.addContent(new Element("name",namespace).setText(department.getName()));
                item.addContent(new Element("description",namespace).setText(department.getDescription()));
                item.addContent(new Element("organization.name",namespace).setText((department.getOrganization() != null) ? department.getOrganization().getName() : ""));
                item.addContent(new Element("parent.name",namespace).setText((department.getParent() != null) ? department.getParent().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalDepartmentnsByOrganizationId(name, orgId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminEmploymentListRequest")
    public @ResponsePayload Element handleAdminEmploymentList(@RequestPayload Element adminEmploymentListElement) {
        final String name = nameExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        String orgId = orgIdExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        String deptId = deptIdExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        String gradeId = gradeIdExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminEmploymentListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminEmploymentListResponse",namespace);
        Collection<Employment> employments = null;
        Element data = new Element("data",namespace);

        if ("".equals(orgId)) {
            orgId = null;
        }
        if ("".equals(deptId)) {
            deptId = null;
        }
        if ("".equals(gradeId)) {
            gradeId = null;
        }

        employments = directoryManager.getEmployments(name, orgId, deptId, gradeId, sort, desc, start, rows);
        if (employments != null) {
            for (Employment employment : employments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("user.id",namespace).setText(employment.getUser().getId()));
                item.addContent(new Element("user.username",namespace).setText(employment.getUser().getUsername()));
                item.addContent(new Element("user.firstName",namespace).setText(employment.getUser().getFirstName()));
                item.addContent(new Element("user.lastName",namespace).setText(employment.getUser().getLastName()));
                item.addContent(new Element("employeeCode",namespace).setText(employment.getEmployeeCode()));
                item.addContent(new Element("role",namespace).setText(employment.getRole()));
                item.addContent(new Element("organization.name",namespace).setText((employment.getOrganization() != null) ? employment.getOrganization().getName() : ""));
                item.addContent(new Element("department.name",namespace).setText((employment.getDepartment() != null) ? employment.getDepartment().getName() : ""));
                item.addContent(new Element("grade.name",namespace).setText((employment.getGrade() != null) ? employment.getGrade().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalEmployments(name, orgId, deptId, gradeId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminEmploymentNoHaveOrgListRequest")
    public @ResponsePayload Element handleAdminEmploymentNoHaveOrgList(@RequestPayload Element adminEmploymentNoHaveOrgListElement) {
        final String name = nameExpression.evaluate(adminEmploymentNoHaveOrgListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminEmploymentNoHaveOrgListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminEmploymentNoHaveOrgListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminEmploymentNoHaveOrgListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminEmploymentNoHaveOrgListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminEmploymentNoHaveOrgListResponse",namespace);
        Collection<Employment> employments = null;
        Element data = new Element("data",namespace);

        employments = employmentDao.getEmploymentsNoHaveOrganization(name, sort, desc, start, rows);
        if (employments != null) {
            for (Employment employment : employments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("user.id",namespace).setText(employment.getUser().getId()));
                item.addContent(new Element("user.username",namespace).setText(employment.getUser().getUsername()));
                item.addContent(new Element("user.firstName",namespace).setText(employment.getUser().getFirstName()));
                item.addContent(new Element("user.lastName",namespace).setText(employment.getUser().getLastName()));
                item.addContent(new Element("employeeCode",namespace).setText(employment.getEmployeeCode()));
                item.addContent(new Element("role",namespace).setText(employment.getRole()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(employmentDao.getTotalEmploymentsNoHaveOrganization(name).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminEmploymentNoInDeptListRequest")
    public @ResponsePayload Element handleAdminEmploymentNoInDeptList(@RequestPayload Element adminEmploymentNoInDeptListElement) {
        final String name = nameExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String orgId = orgIdExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        String deptId = deptIdExpression.evaluate(adminEmploymentNoInDeptListElement).get(0).getValue();
        Element response = new Element("AdminEmploymentNoInDeptListResponse",namespace);
        Collection<Employment> employments = null;

        employments = directoryManager.getEmploymentsNotInDepartment(name, orgId, deptId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (employments != null) {
            for (Employment employment : employments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("user.id",namespace).setText(employment.getUser().getId()));
                item.addContent(new Element("user.username",namespace).setText(employment.getUser().getUsername()));
                item.addContent(new Element("user.firstName",namespace).setText(employment.getUser().getFirstName()));
                item.addContent(new Element("user.lastName",namespace).setText(employment.getUser().getLastName()));
                item.addContent(new Element("employeeCode",namespace).setText(employment.getEmployeeCode()));
                item.addContent(new Element("role",namespace).setText(employment.getRole()));
                item.addContent(new Element("department.name",namespace).setText((employment.getDepartment() != null) ? employment.getDepartment().getName() : ""));
                item.addContent(new Element("grade.name",namespace).setText((employment.getGrade() != null) ? employment.getGrade().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalEmploymentsNotInDepartment(name, orgId, deptId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminEmploymentNoInGradeListRequest")
    public @ResponsePayload Element handleAdminEmploymentNoInGradeList(@RequestPayload Element adminEmploymentNoInGradeListElement) {
        final String name = nameExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String orgId = orgIdExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        String gradeId = gradeIdExpression.evaluate(adminEmploymentNoInGradeListElement).get(0).getValue();
        Element response = new Element("AdminEmploymentNoInGradeListResponse",namespace);
        Collection<Employment> employments = null;

        employments = directoryManager.getEmploymentsNotInGrade(name, orgId, gradeId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (employments != null) {
            for (Employment employment : employments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("user.id",namespace).setText(employment.getUser().getId()));
                item.addContent(new Element("user.username",namespace).setText(employment.getUser().getUsername()));
                item.addContent(new Element("user.firstName",namespace).setText(employment.getUser().getFirstName()));
                item.addContent(new Element("user.lastName",namespace).setText(employment.getUser().getLastName()));
                item.addContent(new Element("employeeCode",namespace).setText(employment.getEmployeeCode()));
                item.addContent(new Element("role",namespace).setText(employment.getRole()));
                item.addContent(new Element("department.name",namespace).setText((employment.getDepartment() != null) ? employment.getDepartment().getName() : ""));
                item.addContent(new Element("grade.name",namespace).setText((employment.getGrade() != null) ? employment.getGrade().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalEmploymentsNotInGrade(name, orgId, gradeId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminGroupListRequest")
    public @ResponsePayload Element handleAdminGroupList(@RequestPayload Element adminGroupListElement) {
        final String name = nameExpression.evaluate(adminGroupListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminGroupListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminGroupListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminGroupListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminGroupListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String orgId = orgIdExpression.evaluate(adminGroupListElement).get(0).getValue();
        Element response = new Element("AdminGroupListResponse",namespace);
        Collection<Group> groups = null;

        if ("".equals(orgId)) {
            orgId = null;
        }

        groups = directoryManager.getGroupsByOrganizationId(name, orgId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (groups != null) {
            for (Group group : groups) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(group.getId()));
                item.addContent(new Element("name",namespace).setText(group.getName()));
                item.addContent(new Element("description",namespace).setText(group.getDescription()));
                item.addContent(new Element("organization.name",namespace).setText((group.getOrganization() != null) ? group.getOrganization().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalGroupsByOrganizationId(name, orgId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminGradeListRequest")
    public @ResponsePayload Element handleAdminGradeList(@RequestPayload Element adminGradeListElement) {
        final String name = nameExpression.evaluate(adminGradeListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminGradeListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminGradeListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminGradeListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminGradeListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String orgId = orgIdExpression.evaluate(adminGradeListElement).get(0).getValue();
        Element response = new Element("AdminGradeListResponse",namespace);
        Collection<Grade> grades = null;

        grades = directoryManager.getGradesByOrganizationId(name, orgId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (grades != null) {
            for (Grade grade : grades) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(grade.getId()));
                item.addContent(new Element("name",namespace).setText(grade.getName()));
                item.addContent(new Element("description",namespace).setText(grade.getDescription()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalGradesByOrganizationId(name, orgId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminOrgListRequest")
    public @ResponsePayload Element handleAdminOrgList(@RequestPayload Element adminOrgListElement) {
        final String name = nameExpression.evaluate(adminOrgListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminOrgListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminOrgListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminOrgListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminOrgListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminOrgListResponse",namespace);
        Collection<Organization> organizations = null;
        organizations = directoryManager.getOrganizationsByFilter(name, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (organizations != null) {
            for (Organization organization : organizations) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(organization.getId()));
                item.addContent(new Element("name",namespace).setText(organization.getName()));
                item.addContent(new Element("description",namespace).setText(organization.getDescription()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalOrganizationsByFilter(name).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminSubDeptListRequest")
    public @ResponsePayload Element handleAdminSubDeptList(@RequestPayload Element adminSubDeptListElement) {
        final String name = nameExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final String deptId = deptIdExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminSubDeptListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminSubDeptListResponse",namespace);
        Collection<Department> departments = null;
        departments = directoryManager.getDepartmentsByParentId(name, deptId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (departments != null) {
            for (Department department : departments) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(department.getId()));
                item.addContent(new Element("name",namespace).setText(department.getName()));
                item.addContent(new Element("description",namespace).setText(department.getDescription()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalDepartmentsByParentId(name, deptId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminUserDeptAndGradeOptionsRequest")
    public @ResponsePayload Element handleAdminUserDeptAndGradeOptions(@RequestPayload Element adminUserDeptAndGradeOptionsElement) {
        final String orgId = orgIdExpression.evaluate(adminUserDeptAndGradeOptionsElement).get(0).getValue();
        Element response = new Element("AdminUserDeptAndGradeOptionsResponse",namespace);
        Collection<Department> departments = null;
        Collection<Grade> grades = null;

        if (orgId != null && orgId.trim().length() > 0) {

            departments = getRecursiveDepartmentList(orgId);
            if (departments != null) {
                Element deptOptions = new Element("departments",namespace);
                Element empty = new Element("item",namespace);
                empty.addContent(new Element("id",namespace).setText(""));
                empty.addContent(new Element("prefix",namespace).setText(""));
                empty.addContent(new Element("name",namespace).setText(""));
                deptOptions.addContent(empty);
                for (Department department : departments) {
                    Element item = new Element("item",namespace);
                    item.addContent(new Element("id",namespace).setText(department.getId()));
                    item.addContent(new Element("prefix",namespace).setText(department.getName()));
                    item.addContent(new Element("name",namespace).setText((department.getTreeStructure() != null) ? department.getTreeStructure() : ""));
                    deptOptions.addContent(item);
                }
                response.addContent(deptOptions);
            }

            grades = directoryManager.getGradesByOrganizationId(null, orgId, "name", false, null, null);
            if (grades != null) {
                Element gradeOptions = new Element("grades",namespace);
                Element empty2 = new Element("item",namespace);
                empty2.addContent(new Element("id",namespace).setText(""));
                empty2.addContent(new Element("prefix",namespace).setText(""));
                empty2.addContent(new Element("name",namespace).setText(""));
                gradeOptions.addContent(empty2);
                for (Grade grade : grades) {
                    Element item = new Element("item",namespace);
                    item.addContent(new Element("id",namespace).setText(grade.getId()));
                    item.addContent(new Element("name",namespace).setText(grade.getName()));
                    gradeOptions.addContent(item);
                }
                response.addContent(gradeOptions);
            }
        }

        return response;
    }

    private Collection<Department> getRecursiveDepartmentList(String orgId) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Collection<Department> result = new ArrayList();
        Collection<Department> parents = directoryManager.getDepartmentListByOrganization(orgId, "name", false, null, null);
        if (parents != null && parents.size() > 0) {
            for (Department p : parents) {
                if (p.getParent() == null) {
                    result.add(p);
                    Collection<Department> childs = getRecursiveDepartmentListByParent(p.getId(), 0);
                    if (childs != null && childs.size() > 0) {
                        result.addAll(childs);
                    }
                }
            }
        }
        return result;
    }

    private Collection<Department> getRecursiveDepartmentListByParent(String parentId, int level) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Collection<Department> result = new ArrayList();
        Collection<Department> parents = directoryManager.getDepartmentsByParentId(null, parentId, "name", false, null, null);
        if (parents != null && parents.size() > 0) {
            for (Department p : parents) {
                String prefix = "";
                for (int i = 0; i < level + 1; i++) {
                    prefix += "--";
                }
                p.setTreeStructure(prefix);
                result.add(p);
                Collection<Department> childs = getRecursiveDepartmentListByParent(p.getId(), level + 1);
                if (childs != null && childs.size() > 0) {
                    result.addAll(childs);
                }
            }
        }
        return result;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminUserGroupListRequest")
    public @ResponsePayload Element handleAdminUserGroupList(@RequestPayload Element adminUserGroupListElement) {
        final String name = nameExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        String userId = userIdExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        String orgId = orgIdExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final String inGroupString = inGroupExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final boolean inGroup = (inGroupString == null)?true:Boolean.parseBoolean(inGroupString);
        final String sort = sortExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminUserGroupListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminUserGroupListResponse",namespace);
        Collection<Group> groups = null;

        if ("".equals(userId)) {
            userId = null;
        }
        if ("".equals(orgId)) {
            orgId = null;
        }

        groups = directoryManager.getGroupsByUserId(name, userId, orgId, inGroup, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (groups != null) {
            for (Group group : groups) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(group.getId()));
                item.addContent(new Element("name",namespace).setText(group.getName()));
                item.addContent(new Element("description",namespace).setText(group.getDescription()));
                item.addContent(new Element("organization.name",namespace).setText((group.getOrganization() != null) ? group.getOrganization().getName() : ""));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalGroupsByUserId(name, userId, orgId, inGroup).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminUserListRequest")
    public @ResponsePayload Element handleAdminUserList(@RequestPayload Element adminUserListElement) {
        final String name = nameExpression.evaluate(adminUserListElement).get(0).getValue();
        String orgId = orgIdExpression.evaluate(adminUserListElement).get(0).getValue();
        String deptId = deptIdExpression.evaluate(adminUserListElement).get(0).getValue();
        String gradeId = gradeIdExpression.evaluate(adminUserListElement).get(0).getValue();
        String groupId = groupIdExpression.evaluate(adminUserListElement).get(0).getValue();
        String roleId = roleIdExpression.evaluate(adminUserListElement).get(0).getValue();
        final String activeString = activeExpression.evaluate(adminUserListElement).get(0).getValue();
        final boolean activeBoolean = (activeString == null)?null:Boolean.parseBoolean(activeString);
        final String sort = sortExpression.evaluate(adminUserListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminUserListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminUserListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminUserListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminUserListResponse",namespace);
        Collection<User> users = null;
        String active = (activeBoolean)?"1":"0";

        if ("".equals(orgId)) {
            orgId = null;
        }
        if ("".equals(deptId)) {
            deptId = null;
        }
        if ("".equals(gradeId)) {
            gradeId = null;
        }
        if ("".equals(groupId)) {
            groupId = null;
        }
        if ("".equals(roleId)) {
            roleId = null;
        }

        users = directoryManager.getUsers(name, orgId, deptId, gradeId, groupId, roleId, active, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (users != null) {
            for (User user : users) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(user.getId()));
                item.addContent(new Element("username",namespace).setText(user.getUsername()));
                item.addContent(new Element("firstName",namespace).setText(user.getFirstName()));
                item.addContent(new Element("lastName",namespace).setText(user.getLastName()));
                item.addContent(new Element("email",namespace).setText(user.getEmail()));
                item.addContent(new Element("active",namespace).setText((user.getActive() == 1)? ResourceBundleUtil.getMessage("console.directory.user.common.label.status.active") : ResourceBundleUtil.getMessage("console.directory.user.common.label.status.inactive")));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalUsers(name, orgId, deptId, gradeId, groupId, roleId, active).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminUserNotInGroupListRequest")
    public @ResponsePayload Element handleAdminUserNotInGroupList(@RequestPayload Element adminUserNotInGroupListElement) {
        final String name = nameExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        String groupId = groupIdExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        final String descString = descExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(adminUserNotInGroupListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AdminUserNotInGroupListResponse",namespace);
        Collection<User> users = null;

        if ("".equals(groupId)) {
            groupId = null;
        }

        users = directoryManager.getUsersNotInGroup(name, groupId, sort, desc, start, rows);
        Element data = new Element("data",namespace);
        if (users != null) {
            for (User user : users) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(user.getId()));
                item.addContent(new Element("username",namespace).setText(user.getUsername()));
                item.addContent(new Element("firstName",namespace).setText(user.getFirstName()));
                item.addContent(new Element("lastName",namespace).setText(user.getLastName()));
                item.addContent(new Element("email",namespace).setText(user.getEmail()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(directoryManager.getTotalUsersNotInGroup(name, groupId).toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AdminUserSSORequest")
    public @ResponsePayload Element handleAdminUserSSO(@RequestPayload Element adminUserSSOElement) {
        final String username = usernameExpression.evaluate(adminUserSSOElement).get(0).getValue();
        String password = passwordExpression.evaluate(adminUserSSOElement).get(0).getValue();
        final String hash = hashExpression.evaluate(adminUserSSOElement).get(0).getValue();
        Element response = new Element("AdminUserSSOResponse",namespace);
        try {
            if (password == null) {
                password = hash;
            }
            Authentication request = new UsernamePasswordAuthenticationToken(username, password);
            Authentication result = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);

            // generate new session to avoid session fixation vulnerability
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(httpRequest, httpResponse);
                session.invalidate();
                session = httpRequest.getSession(true);
                if (savedRequest != null) {
                    new HttpSessionRequestCache().saveRequest(httpRequest, httpResponse);
                }
            }

            // add success to audit trail
            boolean authenticated = result.isAuthenticated();
            LogUtil.info(getClass().getName(), "Authentication for user " + username + ": " + authenticated);
            WorkflowHelper workflowHelper = (WorkflowHelper) AppUtil.getApplicationContext().getBean("workflowHelper");
            workflowHelper.addAuditTrail(this.getClass().getName(), "authenticate", "Authentication for user " + username + ": " + authenticated);

        } catch (AuthenticationException e) {
            // add failure to audit trail
            if (username != null) {
                LogUtil.info(getClass().getName(), "Authentication for user " + username + ": false");
                WorkflowHelper workflowHelper = (WorkflowHelper) AppUtil.getApplicationContext().getBean("workflowHelper");
                workflowHelper.addAuditTrail(this.getClass().getName(), "authenticate", "Authentication for user " + username + ": false");
            }
        }

        if (WorkflowUtil.isCurrentUserAnonymous()) {
            response.addContent(new Element("error",namespace).setText("Unauthorized"));
        }
        response.addContent(new Element("username",namespace).setText(WorkflowUtil.getCurrentUsername()));

        boolean isAdmin = WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN);
        if (isAdmin) {
            response.addContent(new Element("isAdmin",namespace).setText("true"));
        }

        // csrf token
        String csrfToken = SecurityUtil.getCsrfTokenName() + "=" + SecurityUtil.getCsrfTokenValue(httpRequest);
        response.addContent(new Element("token",namespace).setText(csrfToken));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "WorkflowCurrentUsernameRequest")
    public @ResponsePayload Element handleWorkflowCurrentUsername() {
        Element response = new Element("WorkflowCurrentUsernameResponse",namespace);
        response.addContent(new Element("username",namespace).setText(workflowManager.getWorkflowUserManager().getCurrentUsername()));
        return response;
    }

}
