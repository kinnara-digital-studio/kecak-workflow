package org.joget.apps.route;

import org.apache.camel.builder.RouteBuilder;
import org.joget.commons.util.SetupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailApprovalRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailApprovalRouteBuilder.class);
    
	public void configure() {
		String emailAccount = SetupManager.getSettingValue("emailAccount");
		String emailPassword = SetupManager.getSettingValue("emailPassword");
		String emailProtocol = SetupManager.getSettingValue("emailProtocol");
		String emailHost = SetupManager.getSettingValue("emailHost");
		String emailPort = SetupManager.getSettingValue("emailPort");
		
		String emailFolder = SetupManager.getSettingValue("emailFolder");
		if (emailFolder == null) {
			emailFolder = "INBOX";
		}
		
		if (emailAccount != null && emailPassword != null && emailProtocol != null && emailHost != null
				&& emailPort != null) {
			StringBuilder fromUriBuilder = new StringBuilder();
			fromUriBuilder.append(emailProtocol).append("://").append(emailHost).append(":").append(emailPort);
			fromUriBuilder.append("?username=").append(emailAccount);
			fromUriBuilder.append("&password=").append(emailPassword);
			fromUriBuilder.append("&folderName=").append(emailFolder.isEmpty() ? "INBOX" : emailFolder);
			fromUriBuilder.append("&delete=false&unseen=true");

			String fromUri = fromUriBuilder.toString();
			LOGGER.info("###fromUri#" + fromUri);

			from(fromUri).beanRef("emailApprovalProcessor", "parseEmail");
		}
	}

}