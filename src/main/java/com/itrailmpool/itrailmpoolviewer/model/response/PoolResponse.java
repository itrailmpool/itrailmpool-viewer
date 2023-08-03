package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PoolResponse {

    private List<PoolInfo> pools;
}
