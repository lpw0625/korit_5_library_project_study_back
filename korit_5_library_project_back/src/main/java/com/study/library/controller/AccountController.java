package com.study.library.controller;

import com.study.library.security.PrincipalUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 클래스는 RESTful API를 제공하는 컨트롤러임을 나타냅니다.
@RequestMapping("/account") // 이 클래스의 모든 요청은 "/account" 경로 아래에서 처리됩니다.
public class AccountController {

    // HTTP GET 요청을 처리하는 핸들러 메소드입니다. 경로는 "/account/principal"입니다.
    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
       Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();
        return ResponseEntity.ok(null);
        // ResponseEntity를 사용하여 HTTP 응답을 생성합니다. 데이터는 null입니다.
        // 200 OK 응답을 반환합니다.
    }
}
