package com.itrailmpool.itrailmpoolviewer.dal.entity;

import lombok.Data;

import java.time.Instant;
import java.util.Objects;

@Data
public class DeviceEntity {

    private Long id;
    private String name;
    private Long workerId;
    private Instant createdDate;
    private Instant modifiedDate;
    private Instant lastValidShareDate;
    private Boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceEntity that = (DeviceEntity) o;
        return name.equals(that.name) && workerId.equals(that.workerId) && isEnabled.equals(that.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, workerId, isEnabled);
    }
}
