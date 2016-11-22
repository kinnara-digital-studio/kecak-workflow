/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kecak.apps.mobile.dao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.kecak.apps.mobile.model.Mobile;

/**
 *
 * @author Yonathan
 */
public class MobileDaoImpl extends AbstractSpringDao implements MobileDao{
    
    public static final String ENTITY_NAME = "Mobile";

    public void saveOrUpdate(Mobile mobile) {
        super.saveOrUpdate(ENTITY_NAME, mobile);
    }

    public void delete(Mobile mobile) {
        super.delete(ENTITY_NAME, mobile);
    }

    public Mobile getDeviceById(String id) {
        return (Mobile) super.find(ENTITY_NAME, id);
    }

}
