package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrailmpool.itrailmpoolviewer.dal.entity.ShareStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ShareStatisticRepositoryImpl {

    private static final RowMapper<ShareStatistic> ROW_MAPPER = getRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private static String findActiveWorkersCount() {
        return "SELECT COUNT(DISTINCT s.worker) FROM shares s WHERE s.poolid = ? AND created >= NOW() - INTERVAL '90 seconds'";
    }

    public Integer getActiveWorkersCount(String poolId) {
        return jdbcTemplate.queryForObject(findActiveWorkersCount(), Integer.class, poolId);
    }

    private static RowMapper<ShareStatistic> getRowMapper(){
        return (resultSet, i) -> {
            ShareStatistic shareStatistic = new ShareStatistic();

            shareStatistic.setPoolId(resultSet.getString("poolid"));
            shareStatistic.setBlockHeight(resultSet.getLong("blockheight"));
            shareStatistic.setDifficulty(resultSet.getBigDecimal("difficulty"));
            shareStatistic.setNetworkDifficulty(resultSet.getBigDecimal("networkdifficulty"));
            shareStatistic.setMiner(resultSet.getString("miner"));
            shareStatistic.setWorker(resultSet.getString("worker"));
            shareStatistic.setUserAgent(resultSet.getString("useragent"));
            shareStatistic.setIpAddress(resultSet.getString("ipaddress"));
            shareStatistic.setSource(resultSet.getString("source"));
            shareStatistic.setCreated(resultSet.getTimestamp("created").toInstant());
            shareStatistic.setIsValid(resultSet.getBoolean("isvalid"));
            shareStatistic.setDevice(resultSet.getString("device"));

            return shareStatistic;
        };
    }
}
