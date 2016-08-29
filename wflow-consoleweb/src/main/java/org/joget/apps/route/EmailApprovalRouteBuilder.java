package org.joget.apps.route;

import org.apache.camel.builder.RouteBuilder;
import org.joget.commons.util.SetupManager;

public class EmailApprovalRouteBuilder extends RouteBuilder {
	
	
	
	public void configure() {
		String emailAccount = SetupManager.getSettingValue("emailAccount");
		String emailPassword = SetupManager.getSettingValue("emailPassword");
		String emailProtocol = SetupManager.getSettingValue("emailProtocol");
		String emailHost = SetupManager.getSettingValue("emailHost");
		String emailPort = SetupManager.getSettingValue("emailPort");
		String emailFolder = SetupManager.getSettingValue("emailFolder");
		
		StringBuilder fromUriBuilder = new StringBuilder();
		fromUriBuilder.append(emailProtocol).append("://").append(emailHost).append(":").append(emailPort);
		fromUriBuilder.append("?username=").append(emailAccount);
		fromUriBuilder.append("&password=").append(emailPassword);
		fromUriBuilder.append("&folderName=").append(emailFolder == null?"INBOX":emailFolder);
		fromUriBuilder.append("&delete=false&unseen=true");
		
		String fromUri = fromUriBuilder.toString();
		System.out.println("###fromUri#"+fromUri);
		
		from(fromUri).beanRef("emailApprovalProcessor", "parseEmail");
	}


	
}