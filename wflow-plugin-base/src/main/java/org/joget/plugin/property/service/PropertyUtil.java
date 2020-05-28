package org.joget.plugin.property.service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.commons.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility method used to deal with Plugin Properties Options values (JSON format)
 * 
 */
public class PropertyUtil {
    public final static String PASSWORD_PROTECTED_VALUE = "****SECURE_VALUE****-";
    public final static String TYPE_PASSWORD = "password";
    public final static String TYPE_ELEMENT_SELECT = "elementselect";

    /**
     * Parses default properties string (JSON format) from Plugin Properties 
     * Options (JSON format)
     * @param json
     * @return 
     */
    public static String getDefaultPropertyValues(String json) {
        try {
            JSONArray pages = new JSONArray(Optional.ofNullable(json).orElse(""));
            JSONObject values = new JSONObject();
            
            //loop page
            for (int i = 0; i < pages.length(); i++) {
                JSONObject page = (JSONObject) pages.get(i);

                if (page.has("properties")) {
                    //loop properties
                    JSONArray properties = (JSONArray) page.get("properties");
                    for (int j = 0; j < properties.length(); j++) {
                        JSONObject property = (JSONObject) properties.get(j);
                        if (property.has("value")) {
                            values.put(property.getString("name"), property.get("value"));
                        }
                    }
                }
            }
            
            return values.toString();
        } catch (Exception ex) {
            LogUtil.error("PropertyUtil", ex, json);
        }
        return "{}";
    }

    /**
     * Parses the Plugin Properties Options values (JSON format) into a properties
     * map
     * @param json
     * @return
     */
    public static Map<String, Object> getPropertiesValueFromJson(String json) {
        try {
            if (json != null) {
                json = json.replaceAll("\n","\\\\n").replaceAll("\r","\\\\r");
            }
            JSONObject obj = new JSONObject(json);
            return getProperties(obj);
        } catch (Exception e) {
            LogUtil.error(PropertyUtil.class.getName(), e, e.getMessage());
        }
        return new HashMap<String, Object>();
    }

    /**
     * Convenient method used by system to parses a JSON object in to a map
     * @param obj
     * @return 
     */
    public static Map<String, Object> getProperties(JSONObject obj) {
        Map<String, Object> properties = new HashMap<String, Object>();
        try {
            if (obj != null) {
                @SuppressWarnings("rawtypes")
				Iterator keys = obj.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (!obj.isNull(key)) {
                        Object value = obj.get(key);
                        if (value instanceof JSONArray) {
                            properties.put(key, getProperties((JSONArray) value));
                        } else if (value instanceof JSONObject) {
                            properties.put(key, getProperties((JSONObject) value));
                        } else {
                            String stringValue = obj.getString(key);
                            if ("{}".equals(stringValue)) {
                                properties.put(key, new HashMap<String, Object>());
                            } else {
                                properties.put(key, stringValue);
                            }
                        }
                    } else {
                        properties.put(key, "");
                    }
                }
            }
        } catch (Exception e) {
        }
        return properties;
    }

    private static Object[] getProperties(JSONArray arr) throws Exception {
        Collection<Object> array = new ArrayList<Object>();
        if (arr != null && arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                Object value = arr.get(i);
                if (value != null) {
                    if (value instanceof JSONArray) {
                        array.add(getProperties((JSONArray) value));
                    } else if (value instanceof JSONObject) {
                        array.add(getProperties((JSONObject) value));
                    }
                }
            }
        }
        return array.toArray();
    }
    
    /**
     * Convenient method used by system to hide secure values in Plugin Properties 
     * Options values (JSON format)
     * @param json
     * @return 
     */
    public static String propertiesJsonLoadProcessing(String json) {
        //parse content
        if (json != null && json.contains(SecurityUtil.ENVELOPE)) {
            Pattern pattern = Pattern.compile(SecurityUtil.ENVELOPE + "((?!" + SecurityUtil.ENVELOPE + ").)*" + SecurityUtil.ENVELOPE);
            Matcher matcher = pattern.matcher(json);
            Set<String> sList = new HashSet<String>();
            while (matcher.find()) {
                sList.add(matcher.group(0));
            }

            try {
                if (!sList.isEmpty()) {
                    int count = 0;
                    for (String s : sList) {
                        json = json.replaceAll(StringUtil.escapeRegex(s), PASSWORD_PROTECTED_VALUE + count);
                        count++;
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(PropertyUtil.class.getName(), ex, "");
            }
        }
        
        return json;
    }
    
    /**
     * Convenient method used by system to reverse the replaced/hided secure values in Plugin Properties 
     * Options values (JSON format)
     * @param oldJson
     * @param newJson
     * @return 
     */
    public static String propertiesJsonStoreProcessing(String oldJson, String newJson) {
        Map<String, String> passwordProperty = new HashMap<String, String>();
        
        if (oldJson != null && !oldJson.isEmpty() && oldJson.contains(SecurityUtil.ENVELOPE)) {
            Pattern pattern = Pattern.compile(SecurityUtil.ENVELOPE + "((?!" + SecurityUtil.ENVELOPE + ").)*" + SecurityUtil.ENVELOPE);
            Matcher matcher = pattern.matcher(oldJson);
            Set<String> sList = new HashSet<String>();
            while (matcher.find()) {
                sList.add(matcher.group(0));
            }
            
            if (!sList.isEmpty()) {
                int count = 0;
                for (String s : sList) {
                    passwordProperty.put(SecurityUtil.ENVELOPE + PASSWORD_PROTECTED_VALUE + count + SecurityUtil.ENVELOPE, s);
                    count++;
                }
            }
        }
        
        if (newJson != null && !newJson.isEmpty() && (newJson.contains(SecurityUtil.ENVELOPE) || newJson.contains(PASSWORD_PROTECTED_VALUE))) {
            Pattern pattern = Pattern.compile(SecurityUtil.ENVELOPE + "((?!" + SecurityUtil.ENVELOPE + ").)*" + SecurityUtil.ENVELOPE);
            Matcher matcher = pattern.matcher(newJson);
            Set<String> sList = new HashSet<String>();
            while (matcher.find()) {
                sList.add(matcher.group(0));
            }
            
            Pattern pattern2 = Pattern.compile("\"("+StringUtil.escapeRegex(PASSWORD_PROTECTED_VALUE)+"[^\"]*)\"");
            Matcher matcher2 = pattern2.matcher(newJson);
            while (matcher2.find()) {
                sList.add(SecurityUtil.ENVELOPE + matcher2.group(1) + SecurityUtil.ENVELOPE);
                newJson = newJson.replaceAll(StringUtil.escapeRegex(matcher2.group(1)), SecurityUtil.ENVELOPE + matcher2.group(1) + SecurityUtil.ENVELOPE);
            }
            
            //For datalist binder initialization (getBuilderDataColumnList) 
            if (newJson.startsWith(PASSWORD_PROTECTED_VALUE)) {
                sList.add(SecurityUtil.ENVELOPE + newJson + SecurityUtil.ENVELOPE);
                newJson = SecurityUtil.ENVELOPE + newJson + SecurityUtil.ENVELOPE;
            }
            
            try {
                if (!sList.isEmpty()) {
                    for (String s : sList) {
                        if (s.contains(PASSWORD_PROTECTED_VALUE)) {
                            newJson = newJson.replaceAll(StringUtil.escapeRegex(s), passwordProperty.get(s));
                        } else {
                            String tempS = s.replaceAll(SecurityUtil.ENVELOPE, "");
                            tempS = SecurityUtil.encrypt(tempS);

                            newJson = newJson.replaceAll(StringUtil.escapeRegex(s), tempS);
                        }
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(PropertyUtil.class.getName(), ex, "");
            }
        }
        
        return newJson;
    }
}