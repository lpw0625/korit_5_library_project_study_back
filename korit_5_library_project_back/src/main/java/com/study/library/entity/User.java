package com.study.library.entity;

import com.study.library.security.PrincipalUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private List<RoleRegister> roleRegisters;

    public List<SimpleGrantedAuthority> getAuthorities() {
//        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        for(RoleRegister roleRegister : roleRegisters) {
//            authorities.add(new SimpleGrantedAuthority(roleRegister.getRole().getRoleName())); // 이름을 넣는것이 목적.
//        return authorities;
        return roleRegisters.stream()
                .map(roleRegister ->
                        new SimpleGrantedAuthority(roleRegister.getRole().getRoleName())) // 레지스터 하나 안에 롤 객체가 들어있고 이 롤 객체는 롤 네임을 가지고 있다.
                .collect(Collectors.toList());
                    // 결국 SimpleGrantedAuthority를 리턴을 한다.

    }

    public PrincipalUser toPrincipalUser() {
        return PrincipalUser.builder()
                .userId(userId)
                .username(username)
                .name(name)
                .email(email)
                .authorities(getAuthorities())
                .build();
    }
}
