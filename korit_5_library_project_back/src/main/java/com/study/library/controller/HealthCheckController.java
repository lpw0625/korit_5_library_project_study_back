package com.study.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${server.name}") // application.properties에서 server.name 속성값을 주입받습니다.
    private String serverName;

    // "/server/health" 경로에 대한 GET 요청을 처리하는 핸들러 메소드입니다.
    @GetMapping("/server/health")

    public ResponseEntity<?> check() {
        // 서버의 상태를 확인하는 요청에 대한 응답으로, 서버의 이름을 포함한 맵을 반환합니다.

        return ResponseEntity.ok(Map.of("serverName", serverName)); // 서버의 이름을 포함한 응답을 반환합니다.
    }


}
