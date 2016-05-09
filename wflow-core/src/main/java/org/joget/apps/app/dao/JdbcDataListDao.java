package org.joget.apps.app.dao;

import java.util.List;
import java.util.Map;

public interface JdbcDataListDao {

	List<Map<String,Object>> getDataListMap(String query, Integer start, Integer rows);
	
	List<Map<String,Object>> getDataListMap(String query, Integer start, Integer rows, String[]parameters);
	
	List<Object> getDataListObject(String query, Integer start, Integer rows);
	
	String getDataListJson(String query, Integer start, Integer rows);
	
	Long getCountDataList(String query);
	
	Long getCountDataList(String query, String[]parameters);

}
