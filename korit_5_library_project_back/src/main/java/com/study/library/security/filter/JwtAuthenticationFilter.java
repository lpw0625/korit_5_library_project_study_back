package com.study.library.security.filter;

import com.study.library.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

                    @Component
public class JwtAuthenticationFilter extends GenericFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Override

    // 요청에 대한 필터링을 수행합니다
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // HttpServletRequest와 HttpServletResponse로 캐스팅합니다.
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 요청에 "isPermitAll" 속성이 있는지 확인합니다.
        Boolean isPermitAll = (Boolean) request.getAttribute("isPermitAll");



        // "isPermitAll"이 false인 경우에만 인증 절차를 수행합니다.
        // if가 참일 경우 인증이 필요한 요청이라는 뜻이다.
        // 만약에 필요하지 않으면 dofilter라는 메서드가 호출이 되어 다음 필터로 넘어간다.

        if(!isPermitAll) {

            // Authorization 헤더에서 Access Token을 가져옵니다.
            String accessToken = request.getHeader("Authorization"); // Authorization: 리엑트에다가 셋팅을 함.

            // Bearer 토큰을 제거합니다.
            String removedBearerToken = jwtProvider.removeBearer(accessToken);

            // 토큰이 없을 경우 null을 반환합니다.
            Claims claims = jwtProvider.getClaims(removedBearerToken); // 토큰이 없을 경우 null

            // 클레임이 null인 경우, 인증 실패로 UNAUTHORIZED 상태 코드를 반환합니다.
            if (claims == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value()); //인증실패 401떠서 send error가 뜸
                return;
            }

            // 클레임을 사용하여 인증 객체를 가져옵니다.
            Authentication authentication = jwtProvider.getAuthentication(claims);

            // 인증 객체가 null인 경우, 인증 실패로 UNAUTHORIZED 상태 코드를 반환합니다.
            if(authentication == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value()); //인증 실패
                return;
            } // 안전장치.

            // SecurityContextHolder에 인증 객체를 설정합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication); // 403이면 null 이게 들어가면 인증이 된 것.
        }
        // 전처리 후에는 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
        // 후처리(는 현재 없는 상태)
    }

}
