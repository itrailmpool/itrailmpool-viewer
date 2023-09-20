package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;

import java.util.List;

public interface DeviceRepository {

    List<DeviceEntity> findByWorkerId(Long workerId);

    void addDevices(List<DeviceEntity> devices);

    void updateDevices(List<DeviceEntity> devices);
}
