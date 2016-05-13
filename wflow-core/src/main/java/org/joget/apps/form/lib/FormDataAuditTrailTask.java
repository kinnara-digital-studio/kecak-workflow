package org.joget.apps.form.lib;

import javax.transaction.Transactional;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataAuditTrailDao;
import org.joget.apps.form.model.FormDataAuditTrail;

public class FormDataAuditTrailTask implements Runnable {

	FormDataAuditTrail formDataAuditTrail;
	
	public FormDataAuditTrailTask(FormDataAuditTrail formDataAuditTrail) {
		this.formDataAuditTrail = formDataAuditTrail;
	}
	
	@Transactional
	public void run() {
		FormDataAuditTrailDao formDataAuditTrailDao = (FormDataAuditTrailDao) AppUtil.getApplicationContext().getBean("formDataAuditTrailDao");
		formDataAuditTrailDao.addAuditTrail(formDataAuditTrail);
	}

	
}
