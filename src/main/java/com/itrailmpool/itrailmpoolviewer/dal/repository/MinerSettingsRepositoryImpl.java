package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.MinerSettingsEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MinerSettingsRepositoryImpl implements MinerSettingsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinerSettingsRepositoryImpl.class);
    private static final RowMapper<MinerSettingsEntity> MINER_SETTINGS_ROW_MAPPER = getMinerSettingsRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public MinerSettingsEntity findByPoolIdAndWorkerName(String poolId, String workerName) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM miner_settings s WHERE s.poolid = ? AND s.workername = ?", MINER_SETTINGS_ROW_MAPPER, poolId, workerName);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("MinerSettings for poolId {} and workerName {} not found", poolId, workerName);
        }
        return null;
    }


    private static RowMapper<MinerSettingsEntity> getMinerSettingsRowMapper() {
        return (resultSet, i) -> {
            MinerSettingsEntity minerSettings = new MinerSettingsEntity();

            minerSettings.setPoolId(resultSet.getString("poolid"));
            minerSettings.setAddress(resultSet.getString("address"));
            minerSettings.setPaymentThreshold(resultSet.getBigDecimal("paymentthreshold"));
            minerSettings.setCreated(resultSet.getTimestamp("created").toInstant());
            minerSettings.setCreated(resultSet.getTimestamp("updated").toInstant());
            minerSettings.setWorkerName(resultSet.getString("workername"));
            minerSettings.setPassword(resultSet.getString("password"));

            return minerSettings;
        };
    }
}
