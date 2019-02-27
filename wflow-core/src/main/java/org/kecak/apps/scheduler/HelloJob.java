package org.kecak.apps.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Deprecated
public class HelloJob implements Job {
	
	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("Hello Quartz!");

	}

}
