/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kecak.apps.mobile.dao;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.kecak.apps.mobile.model.Mobile;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 *
 * @author Yonathan
 */

public class MobileDaoImpl extends AbstractSpringDao<Mobile> implements MobileDao {

    public static final String ENTITY_NAME = "Mobile";

    @Override
    public void saveOrUpdate(Mobile mobile) {
        super.saveOrUpdate(ENTITY_NAME, mobile);
    }

    @Override
    public void delete(Mobile mobile) {
        super.delete(ENTITY_NAME, mobile);
    }

    @Override
    public Mobile getDeviceById(String id) {
        return (Mobile) super.find(ENTITY_NAME, id);
    }

    @Override
    public Collection<Mobile> find(String condition, String[] args, String sort, boolean desc, int start, int rows) {
        return find(ENTITY_NAME, condition, args, sort, desc, start, rows);
    }

}
