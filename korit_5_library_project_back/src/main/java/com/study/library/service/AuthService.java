package com.study.library.service;

import com.study.library.dto.SigninReqDto;
import com.study.library.dto.SignupReqDto;
import com.study.library.entity.User;
import com.study.library.exception.SaveException;
import com.study.library.jwt.JwtProvider;
import com.study.library.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper; // UserMapper 빈을 주입받습니다.
    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // BCryptPasswordEncoder 빈을 주입받습니다.
    @Autowired
    private JwtProvider jwtProvider; // JwtProvider 빈을 주입받습니다.

    // 주어진 사용자명이 이미 존재하는지 확인하는 메소드입니다.
    public boolean isDuplicatedByUsername(String username) {
        return userMapper.findUserByUsername(username) != null; // 사용자명을 이용하여 사용자를 조회하고, 존재 여부를 반환합니다.
    }

    // 회원가입을 처리하는 메소드입니다.
    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupReqDto signupReqDto) {
        int successCount = 0; // 성공적으로 수행된 작업의 카운트를 초기화합니다.
        User user = signupReqDto.toEntity(passwordEncoder); // 회원가입 요청 정보를 사용자 엔티티로 변환합니다.

        // 사용자 정보와 사용자 역할을 저장합니다.
        successCount += userMapper.saveUser(user); // 사용자 정보를 저장하고, 성공 시 카운트를 증가시킵니다.
        successCount += userMapper.saveRole(user.getUserId()); // 사용자 역할을 저장하고, 성공 시 카운트를 증가시킵니다.

        // 저장된 작업이 모두 성공적으로 수행되지 않은 경우, 예외를 발생시킵니다.
        if(successCount < 2) {
            throw new SaveException(); // 저장 예외를 발생시킵니다.
        }
    }

    // 로그인을 처리하는 메소드입니다.
    public String signin(SigninReqDto signinReqDto) {
        User user = userMapper.findUserByUsername(signinReqDto.getUsername()); // 사용자명을 이용하여 사용자를 조회합니다.
        // 사용자가 존재하지 않는 경우, 예외를 발생시킵니다.
        if(user == null) {
            throw new UsernameNotFoundException("사용자 정보를 확인하세요"); // 사용자를 찾을 수 없는 예외를 발생시킵니다.
        }
        // 비밀번호가 일치하지 않는 경우, 예외를 발생시킵니다.
        if(!passwordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("사용자 정보를 확인하세요"); // 잘못된 자격 증명 예외를 발생시킵니다.
        }

        return jwtProvider.generateToken(user); // JWT 토큰을 생성하여 반환합니다.
    }
}


