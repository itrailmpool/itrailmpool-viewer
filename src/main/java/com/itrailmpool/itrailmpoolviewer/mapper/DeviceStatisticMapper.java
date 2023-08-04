package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceSharesStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.model.DeviceStatisticDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeviceStatisticMapper {

    String KEY_SPLITTER = ".";

    default List<DeviceStatisticEntity> toDeviceStatisticEntity(List<DeviceSharesStatisticEntity> deviceSharesStatistics, List<DeviceHashRateStatisticEntity> deviceHashRateStatistics) {
        Map<String, DeviceHashRateStatisticEntity> hashRateStatisticMap = deviceHashRateStatistics.stream()
                .collect(Collectors.toMap(
                        hashRateStatistic -> hashRateStatistic.getWorkerName() + KEY_SPLITTER + hashRateStatistic.getDeviceName(),
                        Function.identity()
                ));

        List<DeviceStatisticEntity> deviceStatistics = new ArrayList<>();
        for (DeviceSharesStatisticEntity sharesStatistic : deviceSharesStatistics) {
            DeviceHashRateStatisticEntity hashRateStatistic = hashRateStatisticMap.get(sharesStatistic.getWorkerName() + KEY_SPLITTER + sharesStatistic.getDeviceName());
            deviceStatistics.add(toDeviceStatisticEntity(sharesStatistic, hashRateStatistic));
        }

        return deviceStatistics;
    }

    @Mapping(target = "workerName", source = "hashRateStatistic.workerName")
    @Mapping(target = "deviceName", source = "hashRateStatistic.deviceName")
    @Mapping(target = "lastShareDate", source = "sharesStatistic.lastValidShareDate")
    @Mapping(target = "isOnline", source = "sharesStatistic.isOnline")
    @Mapping(target = "currentHashRate", source = "hashRateStatistic.currentHashRate", defaultValue = "0")
    @Mapping(target = "hourlyAverageHashRate", source = "hashRateStatistic.hourlyAverageHashRate", defaultValue = "0")
    @Mapping(target = "dailyAverageHashRate", source = "hashRateStatistic.dailyAverageHashRate", defaultValue = "0")
    DeviceStatisticEntity toDeviceStatisticEntity(DeviceSharesStatisticEntity sharesStatistic, DeviceHashRateStatisticEntity hashRateStatistic);

    List<DeviceStatisticDto> toDeviceStatistic(List<DeviceStatisticEntity> deviceStatisticEntities);

    @Mapping(target = "workerName", source = "workerName")
    @Mapping(target = "deviceName", source = "deviceName")
    @Mapping(target = "lastShareDate", source = "lastShareDate")
    @Mapping(target = "currentHashRate", source = "currentHashRate", defaultValue = "0")
    @Mapping(target = "hourlyAverageHashRate", source = "hourlyAverageHashRate", defaultValue = "0")
    @Mapping(target = "dailyAverageHashRate", source = "dailyAverageHashRate", defaultValue = "0")
    @Mapping(target = "isOnline", source = "isOnline")
    DeviceStatisticDto toDeviceStatistic(DeviceStatisticEntity deviceStatisticEntity);
}