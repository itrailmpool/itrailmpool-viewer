package com.itrailmpool.itrailmpoolviewer.controller;

import com.itrailmpool.itrailmpoolviewer.model.response.Block;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerPerformanceStatsDto;
import com.itrailmpool.itrailmpoolviewer.model.response.MinerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.Payment;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolResponseDto;
import com.itrailmpool.itrailmpoolviewer.model.response.PoolStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerPerformanceStatsContainer;
import com.itrailmpool.itrailmpoolviewer.model.response.WorkerStatisticResponse;
import com.itrailmpool.itrailmpoolviewer.service.PoolStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/api/pools", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PoolStatisticController {

    private final PoolStatisticService poolStatisticService;

    @GetMapping()
    public PoolResponseDto getPools() {
        return poolStatisticService.getPools();
    }

    @GetMapping(value = "/{poolId}/performance")
    public PoolStatisticResponse getPoolPerformance(@PathVariable String poolId) {
        return poolStatisticService.getPoolPerformance(poolId);
    }

    @GetMapping(value = "/{poolId}/blocks")
    public List<Block> getBlocks(@PathVariable String poolId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "100") int size) {
        return poolStatisticService.getBlocks(poolId, page, size);
    }

    @GetMapping(value = "/{poolId}/payments")
    public List<Payment> getPayments(@PathVariable String poolId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "100") int size) {
        return poolStatisticService.getPayments(poolId, page, size);
    }

    @GetMapping(value = "/{poolId}/miners/{address}")
    public MinerStatisticResponse getMinerStatistic(@PathVariable String poolId,
                                                    @PathVariable String address) {
        return poolStatisticService.getMinerStatistic(poolId, address);
    }

    @GetMapping(value = "/{poolId}/miners/{address}/performance")
    public List<WorkerPerformanceStatsContainer> getMinerPerformance(@PathVariable String poolId,
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
    public WorkerStatisticResponse getWorkerStatistic(@PathVariable String poolId,
                                                      @RequestParam String workerName) {
        return poolStatisticService.getWorkerStatistic(poolId, workerName);
    }

    @GetMapping(value = "/{poolId}/workers/{workerName}/performance")
    public List<WorkerPerformanceStatsContainer> getWorkerPerformance(@PathVariable String poolId,
                                                                      @PathVariable String workerName) {
        return poolStatisticService.getWorkerPerformance(poolId, workerName);
    }

}
