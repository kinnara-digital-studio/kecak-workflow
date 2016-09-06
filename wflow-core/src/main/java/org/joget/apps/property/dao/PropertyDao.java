package org.joget.apps.property.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joget.apps.property.model.Property;
import org.joget.commons.spring.model.AbstractSpringDao;

public class PropertyDao extends AbstractSpringDao {

	public static final String ENTITY_NAME = "Property";
	
	
	public void saveOrUpdate(Property property) {
		super.saveOrUpdate(ENTITY_NAME, property);
	}

	public void delete(Property property) {
		super.delete(ENTITY_NAME, property);
	}
	
	public Property getPropertyById(String id) {
		return (Property) super.find(ENTITY_NAME, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Property> getProperties(String condition, String[] param, String sort, Boolean desc, Integer start, Integer rows) {
        if (condition != null && condition.trim().length() != 0) {
            return (List<Property>) super.find(ENTITY_NAME, condition, param, sort, desc, start, rows);
        } else {
            return (List<Property>) super.find(ENTITY_NAME, "", new Object[]{}, sort, desc, start, rows);
        }
    }

	public Long count(String condition, Object[] params) {
        return super.count(ENTITY_NAME, condition, params);
    }
	
	@SuppressWarnings("unchecked")
	public Property getPropertyByLabel(String propertyLabel) {
		Collection<Property> results = new ArrayList<Property>();
		results = super.find(ENTITY_NAME, "WHERE e.propertyLabel = ?", new String[] { propertyLabel }, null, null, null, null);
		
		Property result = null;
		if (results != null && results.size() != 0) {
			result = results.iterator().next();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Property> getAll() {
		return (List<Property>) super.findAll(ENTITY_NAME);
	}
}
