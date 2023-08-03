package com.itrailmpool.itrailmpoolviewer.mapper;

import com.itrailmpool.itrailmpoolviewer.dal.entity.ShareStatisticEntity;
import com.itrailmpool.itrailmpoolviewer.model.Share;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShareStatisticMapper {

    Share toDto(ShareStatisticEntity shareStatistic);
}
