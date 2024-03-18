package com.study.library.security.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class PermitAllFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // HTTPServletRequest와 HTTPServletResponse로 캐스팅합니다.
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 접근을 허용할 URI 패턴들을 리스트로 정의합니다.
        List<String> antMatchers = List.of("/error", "/server", "/auth");

        // 현재 요청의 URI를 가져옵니다.
        String uri = request.getRequestURI();

        // 기본적으로는 모든 요청이 인증이 필요한 것으로 가정합니다.
        request.setAttribute("isPermitAll", false);

        // 각각의 URI 패턴에 대해 반복하여 요청의 URI가 패턴과 일치하는지 확인합니다.
        for(String antMatcher : antMatchers) {

            // URI가 패턴으로 시작하는지 확인합니다.
            if(uri.startsWith(antMatcher)) {

                // 만약 URI가 허용된 패턴과 일치한다면, 인증이 필요하지 않은 것으로 설정합니다
                request.setAttribute("isPermitAll", true);
            }
        }

        // 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}
