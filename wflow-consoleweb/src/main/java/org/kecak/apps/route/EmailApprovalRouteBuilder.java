package org.kecak.apps.route;

import org.apache.camel.builder.RouteBuilder;
import org.joget.commons.util.LogUtil;
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

		// set default port
		if(emailPort == null || emailPort.isEmpty()) {
			if("imap".equals(emailProtocol))
				emailPort = "143"; // default IMAP
			else if("imaps".equals(emailProtocol))
				emailPort = "993"; // default IMAPS
		}

		// set default folder
		if(emailFolder == null || emailFolder.isEmpty())
			emailFolder = "INBOX";

		if (emailAccount != null && !emailAccount.isEmpty() && emailPassword != null && !emailPassword.isEmpty()
				&& emailProtocol != null && !emailProtocol.isEmpty() && emailHost != null && !emailHost.isEmpty()
				&& emailPort != null && !emailPort.isEmpty()) {

			StringBuilder fromUriBuilder = new StringBuilder();
			fromUriBuilder.append(emailProtocol).append("://").append(emailHost).append(":").append(emailPort);
			fromUriBuilder.append("?username=").append(emailAccount);
			fromUriBuilder.append("&password=").append(emailPassword);
			fromUriBuilder.append("&folderName=").append(emailFolder);
			fromUriBuilder.append("&delete=false&unseen=true");

			String fromUri = fromUriBuilder.toString();
			LOGGER.info("###fromUri#" + fromUri);

			from(fromUri).beanRef("emailApprovalProcessor", "parseEmail");
		} else {
			LogUtil.info(getClass().getName(), "Skipping Email Approval; not configured");
		}
	}

}