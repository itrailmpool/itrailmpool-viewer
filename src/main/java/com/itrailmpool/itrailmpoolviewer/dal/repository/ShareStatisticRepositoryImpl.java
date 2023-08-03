package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.ShareStatisticEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ShareStatisticRepositoryImpl implements ShareStatisticRepository {

    private static final RowMapper<ShareStatisticEntity> SHARE_STATISTIC_ROW_MAPPER = getShareStatisticRowMapper();

    private final JdbcTemplate jdbcTemplate;

    private static RowMapper<ShareStatisticEntity> getShareStatisticRowMapper() {
        return (resultSet, i) -> {
            ShareStatisticEntity shareStatistic = new ShareStatisticEntity();

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
