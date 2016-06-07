package org.joget.plugin.enterprise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.joget.apps.app.model.AuditTrail;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataAuditTrailDao;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormDataAuditTrail;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.DefaultAuditTrailPlugin;
import org.json.JSONArray;
import org.springframework.util.StringUtils;

public class FormDataAuditTrailPlugin extends DefaultAuditTrailPlugin {

	FormDataAuditTrailDao formDataAuditTrailDao = null;
	
	public String getLabel() {
		return ResourceBundleUtil
				.getMessage((String) "app.fd.audit.trail.label");
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public String getPropertyOptions() {
		return AppUtil.readPluginResource((String) this.getClass().getName(),
				(String) "/properties/app/formdataAuditTrail.json",
				(Object[]) null, (boolean) true, (String) null);
	}

	public String getName() {
		return "Form Data Audit Trail";
	}

	public String getVersion() {
		return "5.0.0";
	}

	public String getDescription() {
		return ResourceBundleUtil
				.getMessage((String) "app.fd.audit.trail.desc");
	}

	@Override
	public Object execute(@SuppressWarnings("rawtypes") Map props) {
		AuditTrail auditTrail = (AuditTrail) props.get("auditTrail");
		if (auditTrail != null
				&& auditTrail.getClazz().startsWith(
						"org.joget.apps.form.dao.FormDataDaoImpl")) {
			String method = auditTrail.getMethod();
			if ("true".equals(props.get("captureLoad"))
					&& (method.equals("load") || method
							.equals("loadWithoutTransaction"))) {
				this.storeLoadAuditTrail(props, auditTrail);
			} else if ("true".equals(props.get("captureSave"))
					&& method.equals("saveOrUpdate")) {
				this.storeSaveAuditTrail(props, auditTrail);
			} else if ("true".equals(props.get("captureDelete"))
					&& method.equals("delete")) {
				this.storeDeleteAuditTrail(props, auditTrail);
			}
		}
		return null;
	}

	private void storeLoadAuditTrail(@SuppressWarnings("rawtypes") Map props, AuditTrail auditTrail) {
		FormDataAuditTrail data = this.populateData(auditTrail);
		Object[] args = auditTrail.getArgs();
		if (this.isSelected(props, data.getFormId()) && args != null
				&& (args.length == 2 || args.length == 3)) {
			data.setData((String) args[args.length - 1]);
			this.getDao().addAuditTrail(data);
		}
	}
	
	private void storeSaveAuditTrail(@SuppressWarnings("rawtypes") Map props, AuditTrail auditTrail) {
		FormDataAuditTrail data = this.populateData(auditTrail);
        Object[] args = auditTrail.getArgs();
        if (this.isSelected(props, data.getFormId()) && args != null && (args.length == 2 || args.length == 3) && args[args.length - 1] instanceof FormRowSet) {
            FormRowSet rowSet = (FormRowSet)args[args.length - 1];
            if ("true".equals(props.get("captureSavedDatainJson"))) {
                data.setData(this.getJson(rowSet));
            } else {
                data.setData(this.getIds(rowSet));
            }
            this.getDao().addAuditTrail(data);
        }
	}

	private void storeDeleteAuditTrail(@SuppressWarnings("rawtypes") Map props, AuditTrail auditTrail) {
		 FormDataAuditTrail data = this.populateData(auditTrail);
	        Object[] args = auditTrail.getArgs();
	        if (this.isSelected(props, data.getFormId()) && args != null && (args.length == 2 || args.length == 3)) {
	            if (args.length == 3 && args[2] instanceof FormRowSet) {
	                FormRowSet rowSet = (FormRowSet)args[2];
	                data.setData(this.getIds(rowSet));
	            } else {
	                Object[] key = (String[])args[args.length - 1];
	                data.setData(StringUtils.arrayToDelimitedString((Object[])key, (String)";"));
	            }
	            this.getDao().addAuditTrail(data);
	        }
	}
	@SuppressWarnings("rawtypes")
	private String getJson(FormRowSet rowSet) {
		JSONArray arr = new JSONArray();
		try {
			if (!rowSet.isEmpty()) {
				for (FormRow row : rowSet) {
					arr.put((Map) row);
				}
			}
		} catch (Exception e) {
		}
		return arr.toString();
	}
	
	private String getIds(FormRowSet rowSet) {
		ArrayList<String> ids = new ArrayList<String>();
        if (!rowSet.isEmpty()) {
            for (FormRow row : rowSet) {
                ids.add(row.getId());
            }
        }
        if (!ids.isEmpty()) {
            return StringUtils.arrayToDelimitedString((Object[])ids.toArray(), (String)";");
        }
        return "";
	}
	
	private FormDataAuditTrail populateData(AuditTrail auditTrail) {
		FormDataAuditTrail data = new FormDataAuditTrail();
		data.setId(UuidGenerator.getInstance().getUuid());
		data.setAction(auditTrail.getMethod());
		data.setAppId(auditTrail.getAppId());
		data.setAppVersion(auditTrail.getAppVersion());
		data.setDatetime(new Date());
		data.setUsername(auditTrail.getUsername());
		if (auditTrail.getArgs() != null && auditTrail.getArgs().length >= 2) {
			Object[] args = auditTrail.getArgs();
			if (args[0] instanceof Form) {
				Form form = (Form) args[0];
				String formDefId = form.getPropertyString("id");
				String tableName = form.getPropertyString("tableName");
				data.setFormId(formDefId);
				data.setTableName(tableName);
			} else {
				data.setFormId((String) args[0]);
				data.setTableName((String) args[1]);
			}
		}
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isSelected(Map props, String formId) {
		@SuppressWarnings("unused")
		ArrayList<String> formIdList;
        if ("all".equals(props.get("captureMode"))) {
            return true;
        }
        String formIds = (String)props.get("formDefIds");
        if (formIds != null && !formIds.isEmpty() && (formIdList = new ArrayList<String>(Arrays.asList(formIds.split(";")))).contains(formId)) {
            return true;
        }
        return false;
	}
	
	private FormDataAuditTrailDao getDao() {
		if (this.formDataAuditTrailDao == null) {
            this.formDataAuditTrailDao = (FormDataAuditTrailDao)AppUtil.getApplicationContext().getBean("formDataAuditTrailDao");
        }
        return this.formDataAuditTrailDao;
	}
}
