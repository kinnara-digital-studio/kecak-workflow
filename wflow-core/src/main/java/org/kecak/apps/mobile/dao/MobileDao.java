/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kecak.apps.mobile.dao;

import org.kecak.apps.mobile.model.Mobile;

import java.util.Collection;

/**
 *
 * @author Yonathan
 */
public interface MobileDao {
    public void saveOrUpdate(Mobile mobile);
    public void delete(Mobile mobile);
    public Mobile getDeviceById(String id);
    public Collection<Mobile> find(String condition, String[] args, String sort, boolean desc, int start, int rows);
}
