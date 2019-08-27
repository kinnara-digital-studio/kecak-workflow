package org.joget.apps.device.service;

import org.joget.apps.device.dao.DeviceRegistrationDao;
import org.joget.apps.device.model.DeviceRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service("deviceRegistrationService")
public class DeviceRegistrationService {
    @Autowired
    DeviceRegistrationDao deviceRegistrationDao;

    /**
     * Get device list for particular user
     *
     * @param username
     * @return list of devices
     */
    @Nonnull
    public Collection<DeviceRegistration> getUserDevice(String username) {
        return getUserDevices(username, false);
    }

    /**
     * Get device list for particular user
     *
     * @param username
     * @param includeBlockedDevice
     * @return
     */
    @Nonnull
    public Collection<DeviceRegistration> getUserDevices(String username, boolean includeBlockedDevice) {
        return Optional.ofNullable(deviceRegistrationDao.find("where username = ? and blocked = ?", new String[] {username, String.valueOf(includeBlockedDevice)}, "dataModified", true, null, null)).orElse(new ArrayList<>());
    }

    /**
     * Delete current device
     *
     * @param deviceId
     */
    public void deleteDevice(String deviceId) {
        DeviceRegistration deviceRegistration = deviceRegistrationDao.load(deviceId);
        if(deviceRegistration != null)
            deviceRegistrationDao.delete(deviceRegistration);
    }

    /**
     * Block curent device
     *
     * @param deviceId
     */
    public void blockDevice(String deviceId) {
        DeviceRegistration deviceRegistration = deviceRegistrationDao.load(deviceId);
        if(deviceRegistration != null) {
            deviceRegistration.setBlocked(true);
            deviceRegistrationDao.saveOrUpdate(deviceRegistration);
        }
    }

    /**
     * Is device id blocked
     *
     * @param deviceId
     * @return
     */
    public boolean isBlocked(String deviceId) {
        return Optional.ofNullable(deviceRegistrationDao.load(deviceId))
                .map(DeviceRegistration::getBlocked)
                .orElse(false);
    }
}
