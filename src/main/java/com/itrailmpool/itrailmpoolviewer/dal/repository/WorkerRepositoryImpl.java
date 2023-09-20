package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkerRepositoryImpl implements WorkerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRepositoryImpl.class);

    private static final RowMapper<WorkerEntity> WORKER_ENTITY_ROW_MAPPER = getWorkerRowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static RowMapper<WorkerEntity> getWorkerRowMapper() {
        return (resultSet, i) -> {
            WorkerEntity workerEntity = new WorkerEntity();

            workerEntity.setId(resultSet.getLong("id"));
            workerEntity.setName(resultSet.getString("name"));
            workerEntity.setPoolId(resultSet.getString("poolid"));
            workerEntity.setCreationDate(resultSet.getTimestamp("creation_date").toInstant());

            return workerEntity;
        };
    }

    @Override
    public List<WorkerEntity> findAll() {
        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM workers w""",
                WORKER_ENTITY_ROW_MAPPER);
    }


    @Override
    public WorkerEntity findByNameAndPoolId(String workerName, String poolId) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", workerName);
            parameters.addValue("poolId", poolId);

            return namedParameterJdbcTemplate.queryForObject("""
                            SELECT *
                            FROM workers w
                            WHERE w.name = (:name)
                                AND w.poolid = (:poolId)""",
                    parameters,
                    WORKER_ENTITY_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("Worker not found: workerName=[{}], poolId=[{}]", workerName, poolId);

            return null;
        }
    }
}
