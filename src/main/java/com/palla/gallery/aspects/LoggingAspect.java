package com.palla.gallery.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut(value = "execution(* com.palla.gallery.controller.UserController.*(..) )")
    public void controllerMethodPointCut() {

    }

    @Around("controllerMethodPointCut()")
    public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().toString();
        Object[] array = pjp.getArgs();

        StopWatch watch = new StopWatch(getClass().getSimpleName());

        log.info(className + " : " + methodName + "() ---> Arguments( " + mapper.writeValueAsString(array) + " )");
        watch.start();
        Object response = pjp.proceed();
        watch.stop();
        log.info("Method Execution completed with in " + watch.getLastTaskTimeMillis() + "ms");
        log.info(className + " : " + methodName + "() ---> Response( " + mapper.writeValueAsString(response) + " )");
        return response;
    }

}
