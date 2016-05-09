package org.joget.apps.app.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.joget.commons.spring.model.AbstractSpringDao;

import com.google.gson.Gson;

public class JdbcDataListDaoImpl extends AbstractSpringDao implements JdbcDataListDao  {

	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getDataListMap(String query, Integer start, Integer rows) {
		Query q = findSession().createSQLQuery(query);
        q.setFirstResult(start);
        q.setMaxResults(rows);
        q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        
        return q.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getDataListObject(String query, Integer start, Integer rows) {
		Query q = findSession().createSQLQuery(query);
        q.setFirstResult(start);
        q.setMaxResults(rows);
        return q.list();

	}
	
	@SuppressWarnings("unchecked")
	public String getDataListJson(String query, Integer start, Integer rows) {
		List<Object> result = new ArrayList<Object>();
		Query q = findSession().createSQLQuery(query);
        q.setFirstResult(start);
        q.setMaxResults(rows);
        result = q.list();
        return new Gson().toJson(result);

	}
	
	public Long getCountDataList(String query) {
		Query q = findSession().createSQLQuery(query);
		return ((BigInteger)q.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDataListMap(String query, Integer start, Integer rows, String[] parameters) {
		Query q = findSession().createSQLQuery(query);
        q.setFirstResult(start);
        q.setMaxResults(rows);
        q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        if (parameters != null) {
	        for (int i = 0 ; i<parameters.length ; i++) {
	        	q.setParameter(i, parameters[i]);
	        }
        }
        return q.list();

	}

	public Long getCountDataList(String query, String[] parameters) {
		Query q = findSession().createSQLQuery(query);
		if (parameters != null) {
	        for (int i = 0 ; i<parameters.length ; i++) {
	        	q.setParameter(i, parameters[i]);
	        }
        }
		return ((BigInteger)q.uniqueResult()).longValue();

	}
}
