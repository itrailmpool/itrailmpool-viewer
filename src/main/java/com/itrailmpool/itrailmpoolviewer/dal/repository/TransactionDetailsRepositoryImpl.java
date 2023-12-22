package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.TransactionDetailsEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionDetailsRepositoryImpl implements TransactionDetailsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDetailsRepositoryImpl.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public List<TransactionDetailsEntity> findTransactionDetails(Long transactionId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("transaction_id", transactionId);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM transaction_details
                        WHERE transaction_id = :transaction_id
                        ORDER BY id DESC;""",
                parameters,
                getTransactionDetailsEntityRowMapper());
    }

    @Override
    public void insert(List<TransactionDetailsEntity> transactionDetailsEntities) {
        namedParameterJdbcTemplate.batchUpdate("""
                        INSERT INTO transaction_details (transaction_id, address, amount)
                        VALUES (:transaction_id, :address, :amount)""",
                transactionDetailsEntities.stream().map(transactionDetails -> {
                    MapSqlParameterSource parameters = new MapSqlParameterSource();
                    parameters.addValue("transaction_id", transactionDetails.getTransactionId());
                    parameters.addValue("address", transactionDetails.getAddress());
                    parameters.addValue("amount", transactionDetails.getAmount());
                    return parameters;
                }).toArray(SqlParameterSource[]::new));
    }

    private static RowMapper<TransactionDetailsEntity> getTransactionDetailsEntityRowMapper() {
        return (resultSet, i) -> {
            TransactionDetailsEntity transactionDetailsEntity = new TransactionDetailsEntity();

            transactionDetailsEntity.setId(resultSet.getLong("id"));
            transactionDetailsEntity.setTransactionId(resultSet.getLong("transaction_id"));
            transactionDetailsEntity.setAddress(resultSet.getString("address"));
            transactionDetailsEntity.setAmount(resultSet.getBigDecimal("amount"));

            return transactionDetailsEntity;
        };
    }
}
