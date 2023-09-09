package com.itrailmpool.itrailmpoolviewer.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MiningcoreClientAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiningcoreClientAspect.class);

    @Pointcut("execution(public * com.itrailmpool.itrailmpoolviewer.client.MiningcoreClient.*(..))")
    private void miningcoreClientMethods() {
    }

    @AfterThrowing(pointcut = "miningcoreClientMethods()", throwing = "exception")
    public void logException(JoinPoint jp, Throwable exception) {
        LOGGER.error("Miningcore client request exception: [{}], method signature: [{}]", exception.getMessage(), jp.getSignature(), exception);
    }
}
