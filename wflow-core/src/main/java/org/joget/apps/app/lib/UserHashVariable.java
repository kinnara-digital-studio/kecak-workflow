package org.joget.apps.app.lib;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.model.DefaultHashVariablePlugin;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.springframework.context.ApplicationContext;

import com.kinnarastudio.commons.Try;

public class UserHashVariable extends DefaultHashVariablePlugin {
    private Map<String, User> userCache = new HashMap<String, User>();
    
    public String processHashVariable(String variableKey) {
        //if variableKey contains unprocessing hash variable as username
        if (variableKey.startsWith("{") && variableKey.contains("}")) {
            return null;
        }
        
        String username = variableKey;
        String attribute = "";
        for (String v : availableSyntax()) {
            v = v.replaceAll("user.USERNAME", "");
            if (username.contains(v)) {
                username = username.replaceAll(v, "");
                attribute = v.substring(1);
            }
        }
        
        return getUserAttribute(username, attribute);
    }

    public String getName() {
        return "User Hash Variable";
    }

    public String getPrefix() {
        return "user";
    }

    public String getVersion() {
        return "5.0.0";
    }

    public String getDescription() {
        return "";
    }

    protected String getUserAttribute(String username, String attribute) {
        String attributeValue = null;

        try {
            User user = userCache.get(username);
            if (user == null) {
                ApplicationContext appContext = AppUtil.getApplicationContext();
                DirectoryManager directoryManager = (DirectoryManager) appContext.getBean("directoryManager");
                user = directoryManager.getUserByUsername(username);
                userCache.put(username, user);
            }
            if (user != null) {
                //convert first character to upper case
                char firstChar = attribute.charAt(0);
                firstChar = Character.toUpperCase(firstChar);
                attribute = firstChar + attribute.substring(1, attribute.length());

                Method method = User.class.getDeclaredMethod("get" + attribute, new Class[]{});
                String returnResult = ((Object) method.invoke(user, new Object[]{})).toString();
                
                if(!attribute.equals("profilePicture")) {
	                if (returnResult == null || attribute.equals("Password")) {
	                    returnResult = "";
	                }
                }else {
                	Blob blob = ((Blob) method.invoke(user, new Object[]{}));
                	int blobLength = (int) blob.length();
                	byte[] blobAsBytes = blob.getBytes(1, blobLength);
                	String pp = Base64.getEncoder().encodeToString(blobAsBytes);
                	if(pp!=null)
    					returnResult = "data:image/jpeg;base64,"+pp;
    				else
    					returnResult = AppUtil.getRequestContextPath()+"/images/default-avatar.png";
                }

                attributeValue = returnResult;
            }
        } catch (Exception e) {
            LogUtil.error(UserHashVariable.class.getName(), e, "Error retrieving user attribute " + attribute);
        }
        return attributeValue;
    }

    public String getLabel() {
        return "User Hash Variable";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }
    
    @Override
    public Collection<String> availableSyntax() {
        Collection<String> syntax = new ArrayList<String>();
        syntax.add("user.USERNAME.firstName");
        syntax.add("user.USERNAME.lastName");
        syntax.add("user.USERNAME.email");
        syntax.add("user.USERNAME.active");
        syntax.add("user.USERNAME.timeZone");
        syntax.add("user.USERNAME.profilePicture");
        
        return syntax;
    }
}
