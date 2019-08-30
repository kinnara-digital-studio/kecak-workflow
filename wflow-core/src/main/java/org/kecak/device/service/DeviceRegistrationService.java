package org.kecak.device.service;

import org.kecak.device.model.DeviceRegistration;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface DeviceRegistrationService {
    @Nonnull
    Collection<DeviceRegistration> getUserDevice(String username);

    /**
     * Get device list for particular user
     *
     * @param username
     * @param includeBlockedDevice
     * @return
     */
    @Nonnull
    Collection<DeviceRegistration> getUserDevices(String username, boolean includeBlockedDevice);

    /**
     * Delete current device
     *
     * @param deviceId
     */
    void deleteDevice(String deviceId);

    /**
     * Block curent device
     *
     * @param deviceId
     */
    void blockDevice(String deviceId);

    /**
     * Is device id blocked
     *
     * @param deviceId
     * @return
     */
    boolean isBlocked(String deviceId);
}
