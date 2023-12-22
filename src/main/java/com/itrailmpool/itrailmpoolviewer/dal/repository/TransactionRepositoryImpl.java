package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRepositoryImpl.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public TransactionEntity findLastTransactionByPoolId(String poolId) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("poolId", poolId);

            return namedParameterJdbcTemplate.queryForObject("""
                            SELECT *
                            FROM transactions
                            WHERE poolid = :poolId
                            ORDER BY creation_date DESC
                            LIMIT 1;""",
                    parameters,
                    getTransactionEntityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("TransactionEntity for poolId {} not found", poolId);

            return null;
        }
    }

    @Override
    public List<TransactionEntity> findAllByPoolId(String poolId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM transactions
                        WHERE poolid = :poolId
                        ORDER BY creation_date DESC;""",
                parameters,
                getTransactionEntityRowMapper());
    }

    @Override
    public List<TransactionEntity> findAllByPoolId(String poolId, int pageNumber, int pageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);
        parameters.addValue("pageNumber", pageNumber);
        parameters.addValue("pageSize", pageSize);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM transactions
                        WHERE poolid = :poolId
                        ORDER BY creation_date DESC
                        LIMIT :pageSize OFFSET :pageNumber * :pageSize;""",
                parameters,
                getTransactionEntityRowMapper());
    }

    @Override
    public Long insert(TransactionEntity transaction) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("poolId", transaction.getPoolId());
        sqlParameterSource.addValue("hash", transaction.getHash());
        sqlParameterSource.addValue("amount", transaction.getAmount());
        sqlParameterSource.addValue("createdDate", Timestamp.from(transaction.getCreatedDate()));
        sqlParameterSource.addValue("modifiedDate", Timestamp.from(transaction.getModifiedDate()));

        namedParameterJdbcTemplate.update("""
                        INSERT INTO transactions (poolid, hash, amount, creation_date, modification_date)
                        VALUES (:poolId, :hash, :amount, :createdDate, :modifiedDate) RETURNING id;""",
                sqlParameterSource,
                keyHolder);

        return keyHolder.getKey().longValue();
    }

    private static RowMapper<TransactionEntity> getTransactionEntityRowMapper() {
        return (resultSet, i) -> {
            TransactionEntity transactionEntity = new TransactionEntity();

            transactionEntity.setId(resultSet.getLong("id"));
            transactionEntity.setPoolId(resultSet.getString("poolid"));
            transactionEntity.setHash(resultSet.getString("hash"));
            transactionEntity.setAmount(resultSet.getBigDecimal("amount"));
            transactionEntity.setCreatedDate(resultSet.getTimestamp("creation_date").toInstant());
            transactionEntity.setModifiedDate(resultSet.getTimestamp("modification_date").toInstant());

            return transactionEntity;
        };
    }
}
