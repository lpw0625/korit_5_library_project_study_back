package com.study.library.aop;

import com.study.library.dto.SignupReqDto;
import com.study.library.exception.ValidException;
import com.study.library.repository.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class ValidAop {

    @Autowired
    private UserMapper userMapper;

    // ValidAspect 애너테이션이 부착된 메소드를 대상으로 하는 포인트컷을 정의합니다.
    @Pointcut("@annotation(com.study.library.aop.annotation.ValidAspect)")
    private void pointCut() {}

    // 포인트컷을 실행하기 위한 Around 어드바이스를 정의합니다.
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 메소드의 이름을 가져옵니다.
        String methodName = proceedingJoinPoint.getSignature().getName();

        // 메소드의 매개변수를 가져옵니다.
        Object[] args = proceedingJoinPoint.getArgs();

        // BeanPropertyBindingResult 객체를 초기화합니다.
        BeanPropertyBindingResult bindingResult = null;

        // 매개변수 중에 BeanPropertyBindingResult 객체를 찾습니다.
        for(Object arg : args) {
            if(arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
            }
        }

        // 메소드 이름이 "signup"인 경우에만 처리합니다.
        if(methodName.equals("signup")) {
            SignupReqDto signupReqDto = null;

            // 매개변수 중에 SignupReqDto 객체를 찾습니다.
            for(Object arg : args) {
                if(arg.getClass() == SignupReqDto.class) {
                    signupReqDto = (SignupReqDto) arg;
                }
            }

            // 사용자가 이미 존재하는지 확인합니다.
            if(userMapper.findUserByUsername(signupReqDto.getUsername()) != null){
                // 사용자가 이미 존재하는 경우 에러를 추가합니다.
                ObjectError objectError = new FieldError("username", "username", "이미 존재하는 사용자입니다.");
                bindingResult.addError(objectError);
            }
        }

        // 바인딩 결과에 에러가 있을 경우, 예외를 발생시킵니다.
        if(bindingResult.hasErrors()) {
            // 바인딩 결과에서 발생한 에러들을 리스트로 가져옵니다.
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            // 에러를 DTO 필드명과 메세지로 매핑하는 맵을 생성합니다.
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fieldError : fieldErrors) {
                // DTO 변수명과 메세지를 가져와서 맵에 추가합니다.
                String fieldName = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errorMap.put(fieldName, message);
            }
            // 맵을 이용하여 ValidException을 발생시킵니다.
            throw new ValidException(errorMap);
        }

        // 원래의 메소드를 실행하고 결과를 반환합니다.
        return proceedingJoinPoint.proceed();
    }
}











