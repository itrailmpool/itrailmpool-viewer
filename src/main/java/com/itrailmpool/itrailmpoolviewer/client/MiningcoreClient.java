package com.itrailmpool.itrailmpoolviewer.client;

import com.itrailmpool.itrailmpoolviewer.model.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class MiningcoreClient {

    private final RestTemplate restTemplate;
    private final String primaryUrl;
    private final String secondaryUrl;

    public MiningcoreClient(RestTemplate restTemplate,
                            @Value("${miningcore.api.primary.url}") String primaryUrl,
                            @Value("${miningcore.api.secondary.url}") String secondaryUrl) {
        this.restTemplate = restTemplate;
        this.primaryUrl = primaryUrl;
        this.secondaryUrl = secondaryUrl;
    }

    public PoolResponse getPools() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools");
        try {
            ResponseEntity<PoolResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools");
            ResponseEntity<PoolResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }

    public PoolStatisticResponse getPoolPerformance(String poolId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolId, "performance");
        try {
            ResponseEntity<PoolStatisticResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolId + "/performance");
            ResponseEntity<PoolStatisticResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }

    public MinerStatisticResponse getMinerStatistic(String poolId, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolId, "miners", address);
        try {
            ResponseEntity<MinerStatisticResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolId + "/miners/" + address);
            ResponseEntity<MinerStatisticResponse> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }

    public List<WorkerPerformanceStatsContainer> getMinerPerformance(String poolId, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolId, "miners", address, "performance");
        try {
            ResponseEntity<List<WorkerPerformanceStatsContainer>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolId + "/miners/" + address + "/performance");
            ResponseEntity<List<WorkerPerformanceStatsContainer>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }

    public List<MinerPerformanceStats> getMiners(String poolId, int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolId, "miners")
                .queryParam("page", page)
                .queryParam("pageSize", size);
        try {
            ResponseEntity<List<MinerPerformanceStats>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolId + "/miners");
            ResponseEntity<List<MinerPerformanceStats>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }


    public List<Block> getBlocks(String poolId, int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolId, "blocks")
                .queryParam("page", page)
                .queryParam("pageSize", size);
        try {
            ResponseEntity<List<Block>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolId + "/blocks");
            ResponseEntity<List<Block>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }

    public List<Payment> getPayments(String poolName, int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(primaryUrl)
                .pathSegment("pools", poolName, "payments")
                .queryParam("page", page)
                .queryParam("pageSize", size);
        try {
            ResponseEntity<List<Payment>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        } catch (RestClientException e) {
            builder.replacePath(secondaryUrl + "pools/" + poolName + "/payments");
            ResponseEntity<List<Payment>> response =
                    restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });

            return response.getBody();
        }
    }
}
