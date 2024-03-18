package com.study.library.controller;

import com.study.library.aop.annotation.ParamsPrintAspect;
import com.study.library.aop.annotation.ValidAspect;
import com.study.library.dto.SigninReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    // AuthService 빈을 주입받습니다.

    // "/signup" 경로에 대한 POST 요청을 처리하는 핸들러 메소드입니다.

    @ValidAspect // 유효성 검사 애스펙트를 적용합니다.
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {
        // AuthService를 사용하여 회원가입을 수행합니다.

        authService.signup(signupReqDto);
        // 생성된 리소스의 URI와 함께 201 Created 응답을 반환합니다.

        return ResponseEntity.created(null).body(true); // true 값을 포함한 응답을 반환합니다.
    }

    // "/signin" 경로에 대한 POST 요청을 처리하는 핸들러 메소드입니다.
    @PostMapping("/signin")

    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {
        // AuthService를 사용하여 로그인을 수행하고 결과를 반환합니다.

        return ResponseEntity.ok(authService.signin(signinReqDto)); // AuthService의 결과를 반환합니다.
    }

}

