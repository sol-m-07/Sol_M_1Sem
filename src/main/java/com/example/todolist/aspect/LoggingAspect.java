package com.example.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект, логирующий начало и окончание выполнения методов в пакете service,
 * с выводом результата (для void-методов результат не выводится).
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.todolist.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[Service] Start: {}", methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.info("[Service] End: {} -> exception: {}", methodName, t.getMessage());
            throw t;
        }

        if (result != null) {
            log.info("[Service] End: {} -> result: {}", methodName, formatResult(result));
        } else {
            log.info("[Service] End: {} -> result: (void/null)", methodName);
        }
        return result;
    }

    private static String formatResult(Object result) {
        if (result instanceof java.util.Collection) {
            return "Collection(size=" + ((java.util.Collection<?>) result).size() + ")";
        }
        if (result instanceof java.util.Map) {
            return "Map(size=" + ((java.util.Map<?, ?>) result).size() + ")";
        }
        if (result instanceof java.util.Optional) {
            return ((java.util.Optional<?>) result).isPresent()
                    ? "Optional(" + ((java.util.Optional<?>) result).get() + ")"
                    : "Optional.empty";
        }
        return String.valueOf(result);
    }
}
