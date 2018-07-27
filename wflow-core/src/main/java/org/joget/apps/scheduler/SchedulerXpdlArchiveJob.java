package org.joget.apps.scheduler;

import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joget.commons.util.LogUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SchedulerXpdlArchiveJob implements Job{

	private SessionFactory sessionFactory = null;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Session session = (Session) sessionFactory.openSession();
		try {
			Iterator iter = session.createQuery("SELECT COUNT(*),XPDLId FROM SHKXPDLS GROUP BY XPDLId").iterate();
			while(iter.hasNext()) {
				Object[] result = (Object[]) iter.next();
				int count = (int) result[0];
				String xpdlid = (String) result[1];
				if(count>20) {
					Query query = session.createQuery( "INSERT INTO SHKXPDLArchives (XPDLId,XPDLVersion,XPDLClassVersion,XPDLUploadTime,oid,VERSION,dateCreated,id) " + 
							"SELECT  *,CURRENT_TIMESTAMP(),UUID() FROM SHKXPDLS WHERE SHKXPDLS.XPDLId = :xpdlid LIMIT 0,20 " );
					query.setParameter("xpdlid", xpdlid);
					query.executeUpdate();
				}
			}
			
			session.flush();
		} catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        } finally {
            session.close();
        }   
	}

}
