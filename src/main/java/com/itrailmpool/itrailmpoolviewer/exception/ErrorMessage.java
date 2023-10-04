package com.itrailmpool.itrailmpoolviewer.exception;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ErrorMessage {

    private String errorCode;
    private String userMessage;
    private String debugMessage;
}
