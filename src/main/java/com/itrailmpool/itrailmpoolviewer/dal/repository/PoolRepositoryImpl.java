package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.PoolEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PoolRepositoryImpl implements PoolRepository{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<PoolEntity> findAll() {
        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM reports.rep_pools""",
                getPoolEntityRowMapper());
    }

    private static RowMapper<PoolEntity> getPoolEntityRowMapper() {
        return (resultSet, i) -> {
            PoolEntity poolEntity = new PoolEntity();

            poolEntity.setId(resultSet.getLong("pool_id"));
            poolEntity.setPoolId(resultSet.getString("pool_name"));

            return poolEntity;
        };
    }
}
