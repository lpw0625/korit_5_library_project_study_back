package com.study.library.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Data
public class PrincipalUser implements UserDetails {
    private int userId;
    private String username;
    private String name;
    private String email;
    private Collection<? extends GrantedAuthority> authorities; // 컬렉션을 담을 수있고 GrantedAuthority 상속받은 녀석들만 제너릭에 넣는다.
                                        // list인데 sga도 가능하다 업캐스팅 해서 들어올수도 있다.
                                        // PrincipalUser로 authecation을 만들 수 있다.
    @Override
    public String getPassword() {
        return "";
    }

    //계정 사용기간 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 잠금
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 사용기간 만료
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 비활성화
    @Override
    public boolean isEnabled() {
        return true;
    }
}
