package com.itrailmpool.itrailmpoolviewer.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PoolContainerDto {

    private List<PoolInfoDto> pools;
}
