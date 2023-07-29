package com.itrailmpool.itrailmpoolviewer.controller;

import com.itrailmpool.itrailmpoolviewer.model.response.*;
import com.itrailmpool.itrailmpoolviewer.service.PoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/pools")
@RequiredArgsConstructor
public class PoolStatisticController {

    private final PoolService poolService;

    @GetMapping()
    public PoolResponse getPools() {
        return poolService.getPools();
    }

    @GetMapping("/{poolId}/performance")
    public PoolStatisticResponse getPoolPerformance(@PathVariable String poolId) {
        return poolService.getPoolPerformance(poolId);
    }

    @GetMapping("/{poolId}/blocks")
    public List<Block> getBlocks(@PathVariable String poolId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "100") int size) {
        return poolService.getBlocks(poolId, page, size);
    }

    @GetMapping("/{poolId}/payments")
    public List<Payment> getPayments(@PathVariable String poolId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "100") int size) {
        return poolService.getPayments(poolId, page, size);
    }

    @GetMapping("/{poolId}/miners/{address}")
    public MinerStatisticResponse getMinerStatistic(@PathVariable String poolId,
                                                    @PathVariable String address) {
        return poolService.getMinerStatistic(poolId, address);
    }

    @GetMapping("/{poolId}/miners/{address}/performance")
    public List<WorkerPerformanceStatsContainer> getMinerPerformance(@PathVariable String poolId,
                                                                     @PathVariable String address) {
        return poolService.getMinerPerformance(poolId, address);
    }

    @GetMapping("/{poolId}/miners")
    public List<MinerPerformanceStats> getMiners(@PathVariable String poolId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return poolService.getMiners(poolId, page, size);
    }

}
