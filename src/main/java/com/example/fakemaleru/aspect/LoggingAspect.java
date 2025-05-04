package com.example.fakemaleru.aspect;

import com.example.fakemaleru.logging.NoLog;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)"
            + " || within(@org.springframework.stereotype.Service *)"
            + " || within(@org.springframework.stereotype.Repository *)"
            + " && !@annotation(com.example.fakemaleru.logging.NoLog)") // Добавлено исключение
    public void applicationPointcut() {
    }

    @AfterThrowing(pointcut = "applicationPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String message = e.getMessage() != null ? e.getMessage() : "No message available";
        log.error("Exception in {}.{}() with message = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                message);
    }

    @Around("applicationPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Получаем класс текущего метода
        Class<?> targetClass = joinPoint.getSignature().getDeclaringType();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = Arrays.stream(joinPoint.getArgs())
                .map(Object::getClass)
                .toArray(Class[]::new);

        // Проверка на наличие аннотации @NoLog
        try {
            if (targetClass.getMethod(methodName, parameterTypes)
                    .isAnnotationPresent(NoLog.class)) {
                return joinPoint.proceed();
            }
        } catch (NoSuchMethodException e) {
            // Метод не найден, продолжаем выполнение без логирования
        }

        if (log.isInfoEnabled()) {
            log.info("Enter: {}.{}() with argument[s] = {}",
                    targetClass.getName(),
                    methodName,
                    Arrays.toString(joinPoint.getArgs()));
        }

        Object result;
        try {
            result = joinPoint.proceed();
            if (log.isInfoEnabled()) {
                log.info("Exit: {}.{}() with result = {}",
                        targetClass.getName(),
                        methodName,
                        result != null ? result.toString() : "null");
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()",
                    Arrays.toString(joinPoint.getArgs()),
                    targetClass.getName(),
                    methodName);
            throw e;
        } catch (Throwable e) {
            log.error("Unexpected error in {}.{}(): {}",
                    targetClass.getName(),
                    methodName,
                    e.getMessage() != null ? e.getMessage() : "No message available");
            throw e;
        }
    }
}