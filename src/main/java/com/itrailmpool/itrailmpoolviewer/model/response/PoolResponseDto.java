package com.itrailmpool.itrailmpoolviewer.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PoolResponseDto {

    private List<PoolInfoDto> pools;
}
