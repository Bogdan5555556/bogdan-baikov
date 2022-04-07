package com.apitest.aspects;

import com.apitest.services.AttachmentsProvider;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AllureAttachmentAspect {

    @Autowired
    AttachmentsProvider attachmentsProvider;

    @AfterReturning(pointcut = "execution(public * com.apitest.services.RestService.get*(..))",returning="responseEntity")
    public void attachGetResponse(JoinPoint joinPoint, ResponseEntity responseEntity) throws Throwable {
        attachmentsProvider.attachGetResponse(responseEntity);
    }

    @AfterReturning(pointcut = "execution(public * com.apitest.services.RestService.post*(..))",returning="responseEntity")
    public void attachPostResponse(JoinPoint joinPoint, ResponseEntity responseEntity) throws Throwable {
        attachmentsProvider.attachPostResponse(responseEntity);
    }

    @AfterReturning(pointcut = "execution(public * com.apitest.services.RestService.put*(..))",returning="responseEntity")
    public void attachPutResponse(JoinPoint joinPoint, ResponseEntity responseEntity) throws Throwable {
        attachmentsProvider.attachPutResponse(responseEntity);
    }

    @AfterReturning(pointcut = "execution(public * com.apitest.services.RestService.delete*(..))",returning="responseEntity")
    public void attachDeleteResponse(JoinPoint joinPoint, ResponseEntity responseEntity) throws Throwable {
        attachmentsProvider.attachDeleteResponse(responseEntity);
    }

}
