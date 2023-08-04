package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.client.model.PoolResponse;
import com.itrailmpool.itrailmpoolviewer.model.PoolResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoolResponseMapper {

    PoolResponseDto toPoolResponseDto(PoolResponse poolResponse);

}
