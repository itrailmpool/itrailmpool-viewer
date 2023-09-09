package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerHashRateStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerPaymentStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.dal.entity.WorkerStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.model.WorkerHashRateDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WorkerStatisticMapper {

    default List<WorkerStatisticEntity> toWorkerStatistic(List<WorkerHashRateStatisticEntity> workerHashRateStatistics,
                                                          List<WorkerShareStatisticEntity> workerShareStatistics,
                                                          List<WorkerPaymentStatisticEntity> workerPaymentStatistics) {
        Map<Instant, WorkerHashRateStatisticEntity> hashRateStatisticMap = workerHashRateStatistics.stream()
                .collect(Collectors.toMap(WorkerHashRateStatisticEntity::getDate, Function.identity()));
        Map<Instant, WorkerShareStatisticEntity> shareStatisticMap = workerShareStatistics.stream()
                .collect(Collectors.toMap(WorkerShareStatisticEntity::getDate, Function.identity()));
        Map<Instant, WorkerPaymentStatisticEntity> paymentStatisticMap = workerPaymentStatistics.stream()
                .collect(Collectors.toMap(WorkerPaymentStatisticEntity::getDate, Function.identity()));

        List<WorkerStatisticEntity> workerStatistics = new ArrayList<>(workerHashRateStatistics.size());
        shareStatisticMap.forEach((date, shareStatistic) -> {
            WorkerHashRateStatisticEntity hashRateStatistic = hashRateStatisticMap.get(date);
            WorkerPaymentStatisticEntity paymentStatistic = paymentStatisticMap.get(date);

            workerStatistics.add(toWorkerStatistic(hashRateStatistic, shareStatistic, paymentStatistic));
        });

        return workerStatistics;
    }

    @Mapping(target = "date", source = "shareStatistic.date", qualifiedByName = "instantToLocalDate")
    @Mapping(target = "averageHashRate", expression = "java( toAverageHashRate(hashRateStatistic) )")
    @Mapping(target = "workerName", source = "shareStatistic.workerName")
    @Mapping(target = "poolId", source = "shareStatistic.poolId")
    @Mapping(target = "totalAcceptedShares", source = "shareStatistic.totalAcceptedShares", defaultValue = "0")
    @Mapping(target = "totalRejectedShares", source = "shareStatistic.totalRejectedShares", defaultValue = "0")
    @Mapping(target = "totalPayment", expression = "java( toTotalPayment(paymentStatistic) )")
    WorkerStatisticEntity toWorkerStatistic(WorkerHashRateStatisticEntity hashRateStatistic, WorkerShareStatisticEntity shareStatistic, WorkerPaymentStatisticEntity paymentStatistic);

    List<WorkerStatisticDto> toWorkerStatisticDto(List<WorkerStatisticEntity> workerStatisticEntities);

    WorkerStatisticDto toWorkerStatisticDto(WorkerStatisticEntity workerStatisticEntity);

    WorkerHashRateDto toWorkerHashRateDto(WorkerHashRateEntity workerHashRateEntity);

    default BigDecimal toTotalPayment(WorkerPaymentStatisticEntity paymentStatistic) {
        if (paymentStatistic == null || paymentStatistic.getTotalPayments() == null) {
            return BigDecimal.ZERO;
        }

        return paymentStatistic.getTotalPayments();
    }

    default BigDecimal toAverageHashRate(WorkerHashRateStatisticEntity hashRateStatistic) {
        if (hashRateStatistic == null || hashRateStatistic.getAverageHashRate() == null) {
            return BigDecimal.ZERO;
        }

        return hashRateStatistic.getAverageHashRate();
    }

    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}