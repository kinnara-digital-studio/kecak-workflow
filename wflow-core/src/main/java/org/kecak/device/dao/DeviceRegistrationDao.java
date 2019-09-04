/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kecak.device.dao;

import org.kecak.device.model.DeviceRegistration;

import java.util.Collection;

/**
 *
 * @author Yonathan
 */
public interface DeviceRegistrationDao {
    /**
     *
     * @param deviceRegistration
     */
    public void saveOrUpdate(DeviceRegistration deviceRegistration);

    /**
     * Mark as delete
     *
     * @param deviceRegistration
     */
    public void delete(DeviceRegistration deviceRegistration);

    /**
     * Delete from table
     *
     * @param deviceRegistration
     */
    public void hardDelete(DeviceRegistration deviceRegistration);

    /**
     * Load by device ID
     *
     * @param deviceId
     * @return
     */
    public DeviceRegistration load(String deviceId);

    /**
     *
     * @param condition
     * @param args
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    public Collection<DeviceRegistration> find(final String condition, final String[] args, final String sort, final Boolean desc, final Integer start, final Integer rows);
}
