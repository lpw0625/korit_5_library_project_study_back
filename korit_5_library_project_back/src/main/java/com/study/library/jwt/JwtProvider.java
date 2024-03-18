package com.study.library.jwt;

import com.study.library.entity.User;
import com.study.library.repository.UserMapper;
import com.study.library.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private UserMapper userMapper;

    // JwtProvider 클래스의 생성자입니다.
    public JwtProvider(@Value("${jwt.secret}") String secret, @Autowired UserMapper userMapper) {

        // secret 값을 사용하여 키(Key)를 생성합니다.
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        // 사용자 매퍼(UserMapper)를 주입받습니다.
        this.userMapper = userMapper;
    }

    // 주어진 사용자(User)에 대한 JWT 토큰을 생성하여 반환합니다.
    public String generateToken(User user) {

        // 사용자 정보에서 필요한 데이터를 추출합니다.
        int userId = user.getUserId();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // 토큰의 만료일을 설정합니다 (현재 시간 + 24시간).
        Date expireDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));

        // JWT 토큰을 생성합니다.
        String accessToken = Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("authorities", authorities)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // 주어진 토큰에서 "Bearer " 부분을 제거한 문자열을 반환합니다.
    public String removeBearer(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return token.substring("Bearer ".length());
    }

    // 주어진 토큰에서 클레임(Claims)을 추출하여 반환합니다.
    public Claims getClaims(String token) {
        Claims claims = null;

        try {
            // 주어진 토큰을 파싱하여 클레임을 추출합니다.
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {

            // 토큰 파싱 중 오류가 발생한 경우, 에러 로그를 출력합니다.
            log.error("JWT 인증 오류: {}", e.getMessage()); // 이 메세지를 뜨는 이유  클레임을 추출하는 과정에서 터졌기 때문.
                                                            // 토큰이 인증이 되지 않는 경우 401
        }

        return claims;
    }

    // 주어진 클레임(Claims)을 사용하여 사용자의 인증(Authentication) 객체를 반환합니다.
    public Authentication getAuthentication(Claims claims) {
        // 클레임에서 사용자명(username)을 추출합니다.
        String username = (String) claims.get("username");
        // 사용자명을 이용하여 사용자 정보를 데이터베이스에서 조회합니다.
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            // 사용자 정보가 존재하지 않는 경우, null을 반환합니다.
            // 토큰은 유효하지만 데이터베이스에서 사용자 정보가 삭제되었을 경우를 대비합니다.
            return null;
        }
        // 조회된 사용자 정보를 PrincipalUser 객체로 변환합니다.
        PrincipalUser principalUser = user.toPrincipalUser();
        // PrincipalUser 객체와 사용자의 권한 정보를 사용하여 Authentication 객체를 생성하여 반환합니다.
        return new UsernamePasswordAuthenticationToken(principalUser, principalUser.getPassword(), principalUser.getAuthorities());

    // 업 캐스팅이 되어서 리턴.
    }
}
