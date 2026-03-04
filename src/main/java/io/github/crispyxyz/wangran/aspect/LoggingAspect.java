package io.github.crispyxyz.wangran.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Value("${monitor.time.threshold:50}")
    private long threshold;

    @Pointcut("(within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)) && execution(public * *(..))")
    public void controllerMethods() {}

    @Pointcut("within(@org.springframework.stereotype.Service *) && execution(public * *(..))")
    public void serviceMethods() {}

    @Around("controllerMethods() || serviceMethods()")
    public Object monitorTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (duration > threshold) {
                log.warn("耗时警告 - 方法：[{}.{}] 执行耗时：{}ms，参数：{}", className, methodName, duration, args);
            }
        }
    }
}
