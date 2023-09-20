package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.DeviceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {

    public static final RowMapper<DeviceEntity> DEVICE_ENTITY_ROW_MAPPER = getDeviceRowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private static RowMapper<DeviceEntity> getDeviceRowMapper() {
        return (resultSet, i) -> {
            DeviceEntity deviceEntity = new DeviceEntity();

            deviceEntity.setName(resultSet.getString("name"));
            deviceEntity.setWorkerId(resultSet.getLong("worker_id"));
            deviceEntity.setCreatedDate(resultSet.getTimestamp("creation_date").toInstant());
            deviceEntity.setModifiedDate(resultSet.getTimestamp("modification_date").toInstant());
            deviceEntity.setLastValidShareDate(resultSet.getTimestamp("last_valid_share_date").toInstant());
            deviceEntity.setIsEnabled(resultSet.getBoolean("is_enabled"));

            return deviceEntity;
        };
    }

    public List<DeviceEntity> findByWorkerId(Long workerId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("worker_id", workerId);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM devices d
                        WHERE d.worker_id = (:worker_id) AND d.is_enabled = true""",
                parameters,
                DEVICE_ENTITY_ROW_MAPPER);
    }

    public void addDevices(List<DeviceEntity> devices) {
        namedParameterJdbcTemplate.batchUpdate("""
                        INSERT INTO devices (name, worker_id, creation_date, modification_date, last_valid_share_date, is_enabled)
                        VALUES (:name, :worker_id, :creation_date, :modification_date, :last_valid_share_date, :is_enabled)""",
                devices.stream().map(device -> {
                    MapSqlParameterSource parameters = new MapSqlParameterSource();
                    parameters.addValue("name", device.getName());
                    parameters.addValue("worker_id", device.getWorkerId());
                    parameters.addValue("creation_date", Timestamp.from(device.getCreatedDate()));
                    parameters.addValue("modification_date", Timestamp.from(device.getModifiedDate()));
                    parameters.addValue("last_valid_share_date", Timestamp.from(device.getLastValidShareDate()));
                    parameters.addValue("is_enabled", device.getIsEnabled());
                    return parameters;
                }).toArray(SqlParameterSource[]::new));
    }

    public void updateDevices(List<DeviceEntity> devices) {
        namedParameterJdbcTemplate.batchUpdate("""
                        UPDATE devices
                        SET
                            name = :name,
                            worker_id = :worker_id,
                            creation_date = :creation_date,
                            modification_date = :modification_date,
                            last_valid_share_date = :last_valid_share_date,
                            is_enabled = :is_enabled
                        WHERE id = :id""",
                devices.stream().map(device -> {
                    MapSqlParameterSource parameters = new MapSqlParameterSource();
                    parameters.addValue("id", device.getId());
                    parameters.addValue("name", device.getName());
                    parameters.addValue("worker_id", device.getWorkerId());
                    parameters.addValue("creation_date", Timestamp.from(device.getCreatedDate()));
                    parameters.addValue("modification_date", Timestamp.from(device.getModifiedDate()));
                    parameters.addValue("last_valid_share_date", Timestamp.from(device.getLastValidShareDate()));
                    parameters.addValue("is_enabled", device.getIsEnabled());
                    return parameters;
                }).toArray(SqlParameterSource[]::new));

    }
}
