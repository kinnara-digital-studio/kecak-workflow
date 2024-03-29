package org.joget.apps.workflow.controller;

import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.workflow.security.WorkflowUserDetails;
import org.joget.commons.util.*;
import org.kecak.directory.dao.ClientAppDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.*;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.workflow.model.dao.WorkflowHelper;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.kecak.directory.model.ClientApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DirectoryJsonController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    @Qualifier("main")
    ExtDirectoryManager directoryManager;
    @Autowired
    UserDao userDao;
    @Autowired
    EmploymentDao employmentDao;
    @Autowired
    ClientAppDao clientAppDao;

    public EmploymentDao getEmploymentDao() {
        return employmentDao;
    }

    public void setEmploymentDao(EmploymentDao employmentDao) {
        this.employmentDao = employmentDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public ExtDirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public void setDirectoryManager(ExtDirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    /**
     * Manage org chart
     */
    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/organization/list")
    public void listOrganization(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Organization> organizations = null;

        organizations = getDirectoryManager().getOrganizationsByFilter(name, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (organizations != null) {
            for (Organization organization : organizations) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", organization.getId());
                data.put("name", organization.getName());
                data.put("description", organization.getDescription());
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalOrganizationsByFilter(name));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/dept/list")
    public void listDepartment(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Department> departments = null;

        if ("".equals(orgId)) {
            orgId = null;
        }

        departments = getDirectoryManager().getDepartmentsByOrganizationId(name, orgId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (departments != null) {
            for (Department department : departments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", department.getId());
                data.put("name", department.getName());
                data.put("description", department.getDescription());
                data.put("description", department.getDescription());
                data.put("organization.name", (department.getOrganization() != null) ? department.getOrganization().getName() : "");
                data.put("parent.name", (department.getParent() != null) ? department.getParent().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalDepartmentnsByOrganizationId(name, orgId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/subdept/list")
    public void listSubDepartment(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "deptId", required = false) String deptId, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Department> departments = null;

        departments = getDirectoryManager().getDepartmentsByParentId(name, deptId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (departments != null) {
            for (Department department : departments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", department.getId());
                data.put("name", department.getName());
                data.put("description", department.getDescription());
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalDepartmentsByParentId(name, deptId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/grade/list")
    public void listGrade(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Grade> grades = null;

        grades = getDirectoryManager().getGradesByOrganizationId(name, orgId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (grades != null) {
            for (Grade grade : grades) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", grade.getId());
                data.put("name", grade.getName());
                data.put("description", grade.getDescription());
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalGradesByOrganizationId(name, orgId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/group/list")
    public void listGroup(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Group> groups = null;

        if ("".equals(orgId)) {
            orgId = null;
        }

        groups = getDirectoryManager().getGroupsByOrganizationId(name, orgId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (groups != null) {
            for (Group group : groups) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", group.getId());
                data.put("name", group.getName());
                data.put("description", group.getDescription());
                data.put("organization.name", (group.getOrganization() != null) ? group.getOrganization().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalGroupsByOrganizationId(name, orgId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/user/group/list")
    public void listUserGroup(Writer writer, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "orgId", required = false) String orgId, @RequestParam(value = "inGroup", required = false) Boolean inGroup, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Group> groups = null;

        if ("".equals(userId)) {
            userId = null;
        }
        if ("".equals(orgId)) {
            orgId = null;
        }

        groups = getDirectoryManager().getGroupsByUserId(name, userId, orgId, inGroup, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (groups != null) {
            for (Group group : groups) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", group.getId());
                data.put("name", group.getName());
                data.put("description", group.getDescription());
                data.put("organization.name", (group.getOrganization() != null) ? group.getOrganization().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalGroupsByUserId(name, userId, orgId, inGroup));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/user/list")
    public void listUser(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId,
            @RequestParam(value = "deptId", required = false) String deptId, @RequestParam(value = "gradeId", required = false) String gradeId,
            @RequestParam(value = "groupId", required = false) String groupId, @RequestParam(value = "roleId", required = false) String roleId,
            @RequestParam(value = "active", required = false) String active, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<User> users = null;

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
        if ("".equals(active)) {
            active = null;
        }

        users = getDirectoryManager().getUsers(name, orgId, deptId, gradeId, groupId, roleId, active, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (users != null) {
            for (User user : users) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("firstName", user.getFirstName());
                data.put("lastName", user.getLastName());
                data.put("email", user.getEmail());
                data.put("active", (user.getActive() == null || user.getActive() == 1)? ResourceBundleUtil.getMessage("console.directory.user.common.label.status.active") : ResourceBundleUtil.getMessage("console.directory.user.common.label.status.inactive"));
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalUsers(name, orgId, deptId, gradeId, groupId, roleId, active));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/user/notInGroup/list")
    public void listUserNotInGroup(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "groupId", required = false) String groupId,
            @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<User> users = null;

        if ("".equals(groupId)) {
            groupId = null;
        }

        users = directoryManager.getUsersNotInGroup(name, groupId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (users != null) {
            for (User user : users) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("firstName", user.getFirstName());
                data.put("lastName", user.getLastName());
                data.put("email", user.getEmail());
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", directoryManager.getTotalUsersNotInGroup(name, groupId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/user/deptAndGrade/options")
    public void getDeptAndGradeOptions(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam String orgId) throws JSONException, IOException {

        JSONObject jsonObject = new JSONObject();
        Collection<Department> departments = null;
        Collection<Grade> grades = null;

        if (orgId != null && orgId.trim().length() > 0) {
            @SuppressWarnings("rawtypes")
			Map empty = new HashMap();
            empty.put("id", "");
            empty.put("prefix", "");
            empty.put("name", "");

            //JSONArray deptArray = new JSONArray();
            departments = getRecursiveDepartmentList(orgId);
            if (departments != null) {
                jsonObject.accumulate("departments", empty);
                for (Department department : departments) {
                    @SuppressWarnings("rawtypes")
					Map data = new HashMap();
                    data.put("id", department.getId());
                    data.put("name", department.getName());
                    data.put("prefix", (department.getTreeStructure() != null) ? department.getTreeStructure() : "");
                    jsonObject.accumulate("departments", data);
                }
            }

            //JSONArray gradeArray = new JSONArray();
            grades = directoryManager.getGradesByOrganizationId(null, orgId, "name", false, null, null);
            if (grades != null) {
                jsonObject.accumulate("grades", empty);
                for (Grade grade : grades) {
                    @SuppressWarnings("rawtypes")
					Map data = new HashMap();
                    data.put("id", grade.getId());
                    data.put("name", grade.getName());
                    jsonObject.accumulate("grades", data);
                }
            }
        }

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/employment/list")
    public void listEmployment(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId,
            @RequestParam(value = "deptId", required = false) String deptId, @RequestParam(value = "gradeId", required = false) String gradeId,
            @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Employment> employments = null;

        if ("".equals(orgId)) {
            orgId = null;
        }
        if ("".equals(deptId)) {
            deptId = null;
        }
        if ("".equals(gradeId)) {
            gradeId = null;
        }

        employments = getDirectoryManager().getEmployments(name, orgId, deptId, gradeId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (employments != null) {
            for (Employment employment : employments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("user.id", employment.getUser().getId());
                data.put("user.username", employment.getUser().getUsername());
                data.put("user.firstName", employment.getUser().getFirstName());
                data.put("user.lastName", employment.getUser().getLastName());
                data.put("employeeCode", employment.getEmployeeCode());
                data.put("role", employment.getRole());
                data.put("organization.name", (employment.getOrganization() != null) ? employment.getOrganization().getName() : "");
                data.put("department.name", (employment.getDepartment() != null) ? employment.getDepartment().getName() : "");
                data.put("grade.name", (employment.getGrade() != null) ? employment.getGrade().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", getDirectoryManager().getTotalEmployments(name, orgId, deptId, gradeId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/employment/noHaveOrganization/list")
    public void listEmploymentNoHaveOrganization(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Employment> employments = null;

        employments = employmentDao.getEmploymentsNoHaveOrganization(name, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (employments != null) {
            for (Employment employment : employments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("user.id", employment.getUser().getId());
                data.put("user.username", employment.getUser().getUsername());
                data.put("user.firstName", employment.getUser().getFirstName());
                data.put("user.lastName", employment.getUser().getLastName());
                data.put("employeeCode", employment.getEmployeeCode());
                data.put("role", employment.getRole());
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", employmentDao.getTotalEmploymentsNoHaveOrganization(name));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/employment/noInDept/list")
    public void listEmploymentNotInDepartment(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId,
            @RequestParam(value = "deptId", required = false) String deptId,
            @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Employment> employments = null;

        employments = directoryManager.getEmploymentsNotInDepartment(name, orgId, deptId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (employments != null) {
            for (Employment employment : employments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("user.id", employment.getUser().getId());
                data.put("user.username", employment.getUser().getUsername());
                data.put("user.firstName", employment.getUser().getFirstName());
                data.put("user.lastName", employment.getUser().getLastName());
                data.put("employeeCode", employment.getEmployeeCode());
                data.put("role", employment.getRole());
                data.put("department.name", (employment.getDepartment() != null) ? employment.getDepartment().getName() : "");
                data.put("grade.name", (employment.getGrade() != null) ? employment.getGrade().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", directoryManager.getTotalEmploymentsNotInDepartment(name, orgId, deptId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

    @SuppressWarnings("unchecked")
	@RequestMapping("/json/directory/admin/employment/noInGrade/list")
    public void listEmploymentNotInGrade(Writer writer, @RequestParam(value = "callback", required = false) String callback,
            @RequestParam(value = "name", required = false) String name, @RequestParam(value = "orgId", required = false) String orgId,
            @RequestParam(value = "gradeId", required = false) String gradeId,
            @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<Employment> employments = null;

        employments = directoryManager.getEmploymentsNotInGrade(name, orgId, gradeId, sort, desc, start, rows);

        JSONObject jsonObject = new JSONObject();
        if (employments != null) {
            for (Employment employment : employments) {
                @SuppressWarnings("rawtypes")
				Map data = new HashMap();
                data.put("user.id", employment.getUser().getId());
                data.put("user.username", employment.getUser().getUsername());
                data.put("user.firstName", employment.getUser().getFirstName());
                data.put("user.lastName", employment.getUser().getLastName());
                data.put("employeeCode", employment.getEmployeeCode());
                data.put("role", employment.getRole());
                data.put("department.name", (employment.getDepartment() != null) ? employment.getDepartment().getName() : "");
                data.put("grade.name", (employment.getGrade() != null) ? employment.getGrade().getName() : "");
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", directoryManager.getTotalEmploymentsNotInGrade(name, orgId, gradeId));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
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

    @RequestMapping("/json/directory/user/sso")
    public void singleSignOn(Writer writer, HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "password", required = false) String password, @RequestParam(value = "hash", required = false) String hash) throws JSONException, IOException, ServletException {
        boolean loginWithFilter = false;
        
        //WorkflowHttpAuthProcessingFilter
        if (httpRequest.getParameter("j_username") != null && (httpRequest.getParameter("j_password") != null || hash != null)) {
            loginWithFilter = true;
        }
        
        String header = httpRequest.getHeader("Authorization");
        if ((header != null) && header.startsWith("Basic ")) {
            loginWithFilter = true;
        }
        
        if (username != null && !username.isEmpty() && !loginWithFilter) {
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
        }
        
        if (WorkflowUtil.isCurrentUserAnonymous()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("username", WorkflowUtil.getCurrentUsername());
      
        boolean isAdmin = WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN);
        if (isAdmin) {
            jsonObject.accumulate("isAdmin", "true");
        }
        
        // csrf token
        String csrfToken = SecurityUtil.getCsrfTokenName() + "=" + SecurityUtil.getCsrfTokenValue(httpRequest);
        jsonObject.accumulate("token", csrfToken);

        AppUtil.writeJson(writer, jsonObject, callback);
    }
    
    @RequestMapping("/json/directory/user/ssov2")
    public void singleSignOnVersion2(Writer writer, HttpServletRequest httpRequest, HttpServletResponse httpResponse, 
    		@RequestParam(value = "callback", required = false) String callback, 
    		@RequestParam(value = "username", required = true) String username, 
    		@RequestParam(value = "hash", required = true) String hash, 
    		@RequestParam(value = "loginAs", required = true) String loginAs) throws JSONException, IOException, ServletException {
    	String masterLoginUsername = SetupManager.getSettingValue("masterLoginUsername");
    	String masterLoginPassword = SetupManager.getSettingValue("masterLoginPassword");
        
    	WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
    	
    	DirectoryManager dm = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
    	User user = dm.getUserByUsername(loginAs);
    	
		if ((masterLoginUsername != null && masterLoginUsername.trim().length() > 0)
				&& (masterLoginPassword != null && masterLoginPassword.length() > 0)) {
			// decryt masterLoginPassword
			masterLoginPassword = SecurityUtil.decrypt(masterLoginPassword);
			
			User master = new User();
			master.setUsername(masterLoginUsername.trim());
			master.setPassword(StringUtil.md5Base16(masterLoginPassword));
			
			String loginHash = master.getLoginHash().toUpperCase();
			
			if (username.equals(master.getUsername()) && hash.toUpperCase().equals(loginHash)) {
				WorkflowUserDetails userDetail = new WorkflowUserDetails(user);
		
                Authentication request = new UsernamePasswordAuthenticationToken(userDetail, userDetail.getUsername(), userDetail.getAuthorities());
             
                //Login the user
                SecurityContextHolder.getContext().setAuthentication(request);
                workflowUserManager.setCurrentThreadUser(user.getUsername());
             
                // generate new session to avoid session fixation vulnerability
                HttpSession session = httpRequest.getSession(false);
                if (session != null) {
                    SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST_KEY");
                    session.invalidate();
                    session = httpRequest.getSession(true);
                    if (savedRequest != null) {
                        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST_KEY", savedRequest);
                    }
                }

                // add success to audit trail
                boolean authenticated = request.isAuthenticated();
                System.out.println("###authenticated :"+authenticated);
                LogUtil.info(getClass().getName(), "Authentication for user " + username + ": " + authenticated);
                WorkflowHelper workflowHelper = (WorkflowHelper) AppUtil.getApplicationContext().getBean("workflowHelper");
                workflowHelper.addAuditTrail(this.getClass().getName(), "authenticate", "Authentication for user " + username + ": " + authenticated);                

            } 
        }
        
        if (WorkflowUtil.isCurrentUserAnonymous()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("username", WorkflowUtil.getCurrentUsername());
      
        boolean isAdmin = WorkflowUtil.isCurrentUserInRole(WorkflowUtil.ROLE_ADMIN);
        if (isAdmin) {
            jsonObject.accumulate("isAdmin", "true");
        }
        
        // csrf token
        String csrfToken = SecurityUtil.getCsrfTokenName() + "=" + SecurityUtil.getCsrfTokenValue(httpRequest);
        jsonObject.accumulate("token", csrfToken);

        AppUtil.writeJson(writer, jsonObject, callback);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/json/setting/admin/clientApp/list")
    public void listClientApp(Writer writer, @RequestParam(value = "callback", required = false) String callback,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "active", required = false) String active, @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "desc", required = false) Boolean desc,
                         @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "rows", required = false) Integer rows) throws JSONException, IOException {

        Collection<ClientApp> clientApps = null;
        if ("".equals(active)) {
            active = null;
        }
        clientApps = clientAppDao.getClientAppList(name,active,sort,desc,start,rows);
        JSONObject jsonObject = new JSONObject();
        if (clientApps != null) {
            for (ClientApp clientApp : clientApps) {
                @SuppressWarnings("rawtypes")
                Map data = new HashMap();
                data.put("id", clientApp.getId());
                data.put("appName", clientApp.getAppName());
                data.put("clientSecret", clientApp.getClientSecret());
                data.put("redirectUrl", clientApp.getRedirectUrl());
                data.put("active", (clientApp.getActive() == 1)? ResourceBundleUtil.getMessage("console.directory.clientApp.common.label.status.active") : ResourceBundleUtil.getMessage("console.directory.clientApp.common.label.status.inactive"));
                jsonObject.accumulate("data", data);
            }
        }

        jsonObject.accumulate("total", clientAppDao.getTotalClientApp(name,active));
        jsonObject.accumulate("start", start);
        jsonObject.accumulate("sort", sort);
        jsonObject.accumulate("desc", desc);

        if (callback != null && callback.trim().length() != 0) {
            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
        } else {
            jsonObject.write(writer);
        }
    }

//    @SuppressWarnings("unchecked")
//    @RequestMapping("/api/user.identity")
//    public void getUserIdentity(Writer writer, HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestParam(value = "callback", required = false) String callback, @RequestParam(value = "token", required = false) String token) throws Exception {
//        String bearerToken = Optional.of(httpRequest)
//                .map(r -> r.getHeader("Authorization"))
//                .map(s -> s.replace("Bearer ", ""))
//                .orElse("");
//        User user = TokenAuthenticationService.getAuthentication(bearerToken);
//        JSONObject jsonObject = new JSONObject();
//        if (user != null) {
//            JSONObject data = new JSONObject();
//            data.put("id", user.getId());
//            data.put("username", user.getUsername());
//            data.put("firstName", user.getFirstName());
//            data.put("lastName", user.getLastName());
//            data.put("email", user.getEmail());
//            data.put("active", (user.getActive() == 1)? ResourceBundleUtil.getMessage("console.directory.user.common.label.status.active") : ResourceBundleUtil.getMessage("console.directory.user.common.label.status.inactive"));
//            jsonObject.accumulate("data", data);
//        }
//
//        if (callback != null && callback.trim().length() != 0) {
//            writer.write(StringEscapeUtils.escapeHtml(callback) + "(" + jsonObject + ");");
//        } else {
//            jsonObject.write(writer);
//        }
//    }
}
