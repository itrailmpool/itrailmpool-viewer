package com.itrailmpool.itrailmpoolviewer.dal.repository;

import com.itrailmpool.itrailmpoolviewer.dal.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<PaymentEntity> findByPoolIdAndWorkerName(String poolId, String workerName) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);
        parameters.addValue("workerName", workerName);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM payments p
                                 INNER JOIN miner_settings ms ON p.address = ms.address
                        WHERE p.poolid = :poolId
                          AND ms.workername = :workerName
                        ORDER BY p.created DESC ;""",
                parameters,
                getPaymentEntityRowMapper());
    }

    @Override
    public List<PaymentEntity> findByPoolIdAndCreatedDateAfter(String poolId, Instant date) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);
        parameters.addValue("created", Timestamp.from(date));

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM payments p
                        WHERE p.poolid = :poolId
                          AND p.created > :created
                        ORDER BY p.created DESC;""",
                parameters,
                getPaymentEntityRowMapper());
    }

    @Override
    public List<PaymentEntity> findByPoolId(String poolId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("poolId", poolId);

        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM payments p
                        WHERE p.poolid = :poolId
                        ORDER BY p.created DESC;""",
                parameters,
                getPaymentEntityRowMapper());
    }

    private static RowMapper<PaymentEntity> getPaymentEntityRowMapper() {
        return (resultSet, i) -> {
            PaymentEntity paymentEntity = new PaymentEntity();

            paymentEntity.setId(resultSet.getLong("id"));
            paymentEntity.setPoolId(resultSet.getString("poolid"));
            paymentEntity.setCoin(resultSet.getString("coin"));
            paymentEntity.setAddress(resultSet.getString("address"));
            paymentEntity.setAmount(resultSet.getBigDecimal("amount"));
            paymentEntity.setTransactionConfirmationData(resultSet.getString("transactionconfirmationdata"));
            paymentEntity.setCreatedDate(resultSet.getTimestamp("created").toInstant());

            return paymentEntity;
        };
    }
}
