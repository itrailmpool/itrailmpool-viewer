package com.itrailmpool.itrailmpoolviewer.controller;

import com.itrailmpool.itrailmpoolviewer.model.BlockDto;
import com.itrailmpool.itrailmpoolviewer.model.DeviceStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.MinerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.PaymentDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.PoolStatisticContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerCurrentStatisticDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerPerformanceStatsContainerDto;
import com.itrailmpool.itrailmpoolviewer.model.WorkerStatisticDto;
import com.itrailmpool.itrailmpoolviewer.service.PoolStatisticService;
import com.itrailmpool.itrailmpoolviewer.service.WorkerStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping(value = "/api/pools", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PoolStatisticController {

    private final PoolStatisticService poolStatisticService;
    private final WorkerStatisticService workerStatisticService;

    @GetMapping()
    public PoolContainerDto getPools() {
        return poolStatisticService.getPools();
    }

    @GetMapping(value = "/{poolId}/performance")
    public PoolStatisticContainerDto getPoolPerformance(@PathVariable String poolId) {
        return poolStatisticService.getPoolPerformance(poolId);
    }

    @GetMapping(value = "/{poolId}/blocks")
    public List<BlockDto> getBlocks(@PathVariable String poolId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "100") int size) {
        return poolStatisticService.getBlocks(poolId, page, size);
    }

    @GetMapping(value = "/{poolId}/payments")
    public List<PaymentDto> getPayments(@PathVariable String poolId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "100") int size) {
        return poolStatisticService.getPayments(poolId, page, size);
    }

    @GetMapping(value = "/{poolId}/miners/{address}")
    public MinerStatisticDto getMinerStatistic(@PathVariable String poolId,
                                               @PathVariable String address) {
        return poolStatisticService.getMinerStatistic(poolId, address);
    }

    @GetMapping(value = "/{poolId}/miners/{address}/performance")
    public List<WorkerPerformanceStatsContainerDto> getMinerPerformance(@PathVariable String poolId,
                                                                        @PathVariable String address) {
        return poolStatisticService.getMinerPerformance(poolId, address);
    }

    @GetMapping(value = "/{poolId}/miners")
    public List<MinerPerformanceStatsDto> getMiners(@PathVariable String poolId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return poolStatisticService.getMiners(poolId, page, size);
    }

    @GetMapping(value = "/{poolId}/workers")
    public WorkerCurrentStatisticDto getWorkerStatistic(@PathVariable String poolId,
                                                        @RequestParam String workerName) {
        return workerStatisticService.getWorkerCurrentStatistic(poolId, workerName);
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/performance")
    public List<WorkerPerformanceStatsContainerDto> getWorkerPerformance(@PathVariable String poolId,
                                                                         @PathVariable String workerName) {
        return workerStatisticService.getWorkerPerformance(poolId, workerName);
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/statistics")
    public Page<WorkerStatisticDto> getWorkerStatistic(Pageable pageable,
                                                       @PathVariable String poolId,
                                                       @PathVariable String workerName) {
        return workerStatisticService.getWorkerStatistic(pageable, poolId, workerName);
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/statistics/csv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getWorkerStatisticCsv(@PathVariable String poolId,
                                                        @PathVariable String workerName,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(3);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        String csv = workerStatisticService.getWorkerStatisticCsv(poolId, workerName, startDate, endDate);
        String fileName = String.format("%s-statistics-%s-to-%s.csv", workerName, startDate, endDate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/payments")
    public Page<PaymentDto> getWorkerPayments(Pageable pageable,
                                              @PathVariable String poolId,
                                              @PathVariable String workerName) {
        return workerStatisticService.getWorkerPayments(pageable, poolId, workerName);
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/devices")
    public Page<DeviceStatisticDto> getDeviceStatistics(Pageable pageable,
                                                        @PathVariable String poolId,
                                                        @PathVariable String workerName,
                                                        @RequestParam(value = "deviceName", required = false) String deviceName) {
        return workerStatisticService.getDeviceStatistics(pageable, poolId, workerName, deviceName);
    }
}
