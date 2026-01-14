package com.sky.aspect;

import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.aspect.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void before(JoinPoint joinPoint){
        log.info("开始填充公共字段");

        MethodSignature signature=(MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity=args[0];
        LocalDateTime now=LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();

        try{
            Class<?> entityClass = entity.getClass();
            if(operationType==OperationType.INSERT){
                Method setCreateTime = entityClass.getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = entityClass.getDeclaredMethod("setCreateUser", Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
            }
            Method setUpdateTime=entityClass.getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser=entityClass.getDeclaredMethod("setUpdateUser", Long.class);

            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("公共字段自动填充失败：{}", e.getMessage());
            throw new RuntimeException("公共字段自动填充失败，请检查实体类是否有 setCreateTime/Update 等方法", e);
        }
    }
}
