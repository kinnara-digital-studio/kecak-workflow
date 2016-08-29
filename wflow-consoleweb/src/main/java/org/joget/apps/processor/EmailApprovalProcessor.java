package org.joget.apps.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailApprovalProcessor {

	public static final String MAIL_SUBJECT_PATTERN = "{unuse}ID:{processId}{unuse}Activity:{activityId}{unuse}";
	public static final String MAIL_CONTENT_PATTERN = "{form_approval_new_application_approval_action_status}{unuse}{unuse}ID: {processId}{unuse}Remarks: [{form_approval_new_application_approval_action_remarks}]{unuse}";

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailApprovalProcessor.class);
    

	public void parseEmail(@Body String body, Exchange exchange) {
		String subject = (String) exchange.getIn().getHeader("subject");
		LOGGER.info("Subject :"+subject);
		
		String subjectRegex = createRegex(MAIL_SUBJECT_PATTERN);
		LOGGER.info("Subject Pattern :"+subjectRegex);
		
		Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
        Matcher matcher = pattern.matcher(MAIL_SUBJECT_PATTERN);

        Pattern pattern2 = Pattern.compile("^" + subjectRegex + "$");
        Matcher matcher2 = pattern2.matcher(subject);

        String processId = null;
        String activityId = null;
        
        while (matcher2.find()) {
            int count = 1;
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher2.group(count);
                if ("processId".equals(key)) {
                    processId = value;
                } else if ("activityId".equals(key)) {
                    activityId = value;
                } 
                count++;
            }
        }
		
		LOGGER.info("Process ID:"+processId);
		LOGGER.info("Activity ID:"+activityId);
		
//		LOGGER.info("Content :"+body);
//		String contentPattern = Constant.MAIL_CONTENT_PATTERN;
//		contentPattern = contentPattern.replaceAll("\\\\\\{unuse\\\\\\}", "([\\\\s\\\\S]*)");
//		contentPattern = contentPattern.replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
//		
		
	}
	
	public String createRegex(String raw) {
		String result = escapeString(raw, null);
		return result.replaceAll("\\\\\\{unuse\\\\\\}", "([\\\\s\\\\S]*)").replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String escapeString(String inStr, Map<String, String> replaceMap) {
        if (replaceMap != null) {
            Iterator it = replaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                inStr = inStr.replaceAll(pairs.getKey(), escapeRegex(pairs.getValue()));
            }
        }
        
        return escapeRegex(inStr);
    }
	
	public String escapeRegex(String inStr) {
        return (inStr != null) ?  inStr.replaceAll("([\\\\*+\\[\\](){}\\$.?\\^|])", "\\\\$1") : null;
    }
}
