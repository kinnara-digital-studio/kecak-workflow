/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kecak.device.dao;

import org.kecak.device.model.DeviceRegistration;
import org.joget.commons.spring.model.AbstractSpringDao;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 *
 * @author Yonathan
 */
public class DeviceRegistrationDaoImpl extends AbstractSpringDao<DeviceRegistration> implements DeviceRegistrationDao {
    public static final String ENTITY_NAME = "DeviceRegistration";

    @Override
    public void saveOrUpdate(DeviceRegistration deviceRegistration) {
        saveOrUpdate(ENTITY_NAME, deviceRegistration);
    }

    @Override
    public void delete(DeviceRegistration deviceRegistration) {
        internalDelete(deviceRegistration, false);
    }

    @Override
    public void hardDelete(DeviceRegistration deviceRegistration) {
        internalDelete(deviceRegistration, true);
    }

    @Override
    public DeviceRegistration load(String id) {
        return internalLoad(id);
    }

    @Override
    public Collection<DeviceRegistration> find(final String condition, final String[] args, final String sort, final Boolean desc, final Integer start, final Integer rows) {
        return internalFind(condition, args, sort, desc, start, rows);
    }

    /**
     * Internal Delete
     *
     * @param deviceRegistration
     * @param deleteFromTable hard delete
     */
    private void internalDelete(final DeviceRegistration deviceRegistration, boolean deleteFromTable) {
        if(deleteFromTable) {
            // delete from table
            delete(ENTITY_NAME, deviceRegistration);
        } else {
            // mark as deleted
            deviceRegistration.setDeleted(true);
            saveOrUpdate(ENTITY_NAME, deviceRegistration);
        }
    }

    /**
     * Internal Load
     *
     * @param id device id
     * @return null if id not found
     */
    private DeviceRegistration internalLoad(String id) {
        DeviceRegistration deviceRegistration = (DeviceRegistration) find(ENTITY_NAME, id);
        return deviceRegistration == null || deviceRegistration.getDeleted() || deviceRegistration.getBlocked() ? null : deviceRegistration;
    }

    /**
     * Internal Find
     *
     * @param condition
     * @param args
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return collection of {@link DeviceRegistration}
     */
    private Collection<DeviceRegistration> internalFind(final String condition, final String[] args, final String sort, final Boolean desc, final Integer start, final Integer rows) {
        final String where;
        // filter by delete setatus
        if(condition != null) {
            where = "where deleted = false and (1 = 1 " + condition.trim().replaceAll("where ", "and ") + ")";
        } else {
            where = "where deleted = false";
        }

        return find(ENTITY_NAME, where, args, sort, desc, start, rows);
    }
}
