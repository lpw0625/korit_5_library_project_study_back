package com.study.library.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ParamsPrintAop {
    // ParamsPrintAspect 애너테이션을 사용하는 메소드를 대상으로 하는 포인트컷을 정의합니다.
    @Pointcut("@annotation(com.study.library.aop.annotation.ParamsPrintAspect)")
    private void pointCut() {}

    // 포인트컷을 실행하기 위한 Around 어드바이스를 정의합니다.
    @Around("pointCut()")

    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 조인 포인트에서 메소드의 시그니처를 가져옵니다.

        CodeSignature codeSignature = (CodeSignature) proceedingJoinPoint.getSignature();
        // 클래스 이름을 가져옵니다.

        String className = codeSignature.getDeclaringTypeName();
        // 메소드 이름을 가져옵니다.

        String methodName = codeSignature.getName();
        // 메소드의 매개변수 이름을 가져옵니다.

        String[] argNames = codeSignature.getParameterNames();
        // 메소드의 매개변수 값들을 가져옵니다.

        Object[] args = proceedingJoinPoint.getArgs();
        // 매개변수 이름과 값들을 로깅합니다.
        for(int i = 0; i < argNames.length; i++) {
            log.info("{}: {} ({}.{}", argNames[i], args[i], className, methodName);
        }

        // 원래의 메소드를 실행하고 결과를 반환합니다.
        return proceedingJoinPoint.proceed();
    }
}
