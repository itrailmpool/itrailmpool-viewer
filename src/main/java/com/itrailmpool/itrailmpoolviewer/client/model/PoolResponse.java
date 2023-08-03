package com.itrailmpool.itrailmpoolviewer.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PoolResponse {

    private List<PoolInfo> pools;
}
